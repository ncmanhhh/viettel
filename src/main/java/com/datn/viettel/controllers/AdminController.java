package com.datn.viettel.controllers;

import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.configs.ResourceMessageConfig;
import com.datn.viettel.dto.common.ExecutionResult;
import com.datn.viettel.dto.common.ExecutionResultFactory;
import com.datn.viettel.dto.request.EmbedRequest;
import com.datn.viettel.services.iservice.AIService;
import com.datn.viettel.services.iservice.ElasticsearchService;
import com.datn.viettel.services.iservice.EmbedService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RestController
//@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final EmbedService embedService;
    private final AIService aiService;
    private final ElasticsearchService elasticsearchService;
    private final Environment environment;

    public AdminController(EmbedService embedService,
                           AIService aiService,
                           ElasticsearchService elasticsearchService,
                           Environment environment) {
        this.embedService = embedService;
        this.aiService = aiService;
        this.elasticsearchService = elasticsearchService;
        this.environment = environment;
    }

    /**
     * Trigger embed bằng cách gọi API (manual).
     * POST /admin/embeddings/run
     */
    @PostMapping("/embeddings")
    public ResponseEntity<ExecutionResult<String>> runEmbed(
            @RequestBody EmbedRequest req,
            HttpServletRequest request
    ) {

        if (req == null || req.getType() == null || req.getType().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ExecutionResultFactory.error(
                            "INVALID_REQUEST",
                            "EMBED.MISSING_TYPE",
                            "type là bắt buộc (MOBILE | FTTH | SIM)",
                            request.getRequestURI()
                    )
            );
        }

        String type = req.getType().trim().toUpperCase();

        try {
            switch (type) {
                case "MOBILE" -> embedService.embedMobilePackagesV2();
                case "FTTH"   -> embedService.embedFtthPackages();
                case "SIM"    -> embedService.embedSims();
                default -> {
                    return ResponseEntity.badRequest().body(
                            ExecutionResultFactory.error(
                                    "INVALID_REQUEST",
                                    "EMBED.INVALID_TYPE",
                                    "type chỉ hỗ trợ: MOBILE | FTTH | SIM",
                                    request.getRequestURI()
                            )
                    );
                }
            }

            return ResponseEntity.ok(
                    ExecutionResultFactory.success(
                            "Embed " + type + " thành công",
                            Constants.ExecutionCode.SUCCESS,
                            ResponseMessage.Common.SUCCESS,
                            ResourceMessageConfig.getResourceMessage(ResponseMessage.Common.SUCCESS),
                            request.getRequestURI()
                    )
            );

        } catch (Exception e) {
            log.error("Embed error - type={}", type, e);
            return ResponseEntity.internalServerError().body(
                    ExecutionResultFactory.error(
                            "SYSTEM_ERROR",
                            "EMBED.ERROR",
                            e.getMessage(),
                            request.getRequestURI()
                    )
            );
        }
    }





    /**
     * ✅ Test nhanh vector search trên Elasticsearch (đã truy vấn được chưa)
     *
     * GET /admin/embeddings/es-test?type=MOBILE&q=goi cuoc 4g gia re&topK=5&minScore=0
     * GET /admin/embeddings/es-test?type=FTTH&q=cap quang viettel gia re&topK=5&minScore=0
     */
    @GetMapping("/es-test")
    public ResponseEntity<?> esTest(
            @RequestParam(defaultValue = "MOBILE") String type,
            @RequestParam String q,
            @RequestParam(defaultValue = "5") int topK,
            @RequestParam(defaultValue = "0") double minScore
    ) {
        long start = System.currentTimeMillis();

        String query = q != null ? q.trim() : "";
        if (!StringUtils.hasText(query)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Thiếu tham số q"
            ));
        }

        String t = type.trim().toUpperCase(Locale.ROOT);

        String index = switch (t) {
            case "MOBILE" -> environment.getProperty("scheduled.embedding.mobile-package.index");
            case "FTTH" -> environment.getProperty("scheduled.embedding.ftth-package.index");
            default -> null;
        };

        if (!StringUtils.hasText(index)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "type không hợp lệ hoặc không cấu hình index. Hỗ trợ: MOBILE | FTTH"
            ));
        }

        try {
            // 1) Embed query -> queryVector
            float[] queryVector = aiService.embeddingVectorV2(query);
            if (queryVector == null || queryVector.length == 0) {
                return ResponseEntity.internalServerError().body(Map.of(
                        "success", false,
                        "message", "Không tạo được embedding cho query"
                ));
            }

            // 2) Vector search ES (field đang dùng: contentVector)
            Object raw = elasticsearchService.searchByVector(
                    index,
                    queryVector,
                    topK,
                    minScore,
                    "contentVector",
                    null // query=null => function_score cosineSimilarity
            );

            // 3) Parse hits cho dễ nhìn
            Map<String, Object> resp = (raw instanceof Map) ? (Map<String, Object>) raw : Map.of("raw", raw);
            List<Map<String, Object>> hits = extractHits(resp);

            long took = System.currentTimeMillis() - start;

            Map<String, Object> result = new java.util.LinkedHashMap<>();
            result.put("success", true);
            result.put("type", t);
            result.put("index", index);
            result.put("query", query);
            result.put("topK", topK);
            result.put("minScore", minScore);
            result.put("tookMs", took);
            result.put("hitCount", hits.size());
            result.put("hits", hits);
            result.put("rawTook", resp.get("took"));
            result.put("timedOut", resp.get("timed_out"));

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            long took = System.currentTimeMillis() - start;
            log.error("ES test error - type={}, index={}, tookMs={}, err={}", t, index, took, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "type", t,
                    "index", index,
                    "tookMs", took,
                    "error", e.getMessage()
            ));
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractHits(Map<String, Object> esResp) {
        try {
            Object hitsObj = esResp.get("hits");
            if (!(hitsObj instanceof Map)) return List.of();

            Map<String, Object> hitsMap = (Map<String, Object>) hitsObj;
            Object hitListObj = hitsMap.get("hits");
            if (!(hitListObj instanceof List)) return List.of();

            List<Object> hitList = (List<Object>) hitListObj;

            return hitList.stream()
                    .filter(x -> x instanceof Map)
                    .map(x -> (Map<String, Object>) x)
                    .map(h -> {
                        Object id = h.get("_id");
                        Object score = h.get("_score");
                        Map<String, Object> src = (h.get("_source") instanceof Map)
                                ? (Map<String, Object>) h.get("_source")
                                : Map.of();

                        // cắt contentFull cho gọn
                        Object cf = src.get("contentFull");
                        if (cf instanceof String s && s.length() > 400) {
                            src = new java.util.LinkedHashMap<>(src);
                            src.put("contentFull", s.substring(0, 400) + "...");
                        }

                        return Map.of(
                                "id", id,
                                "score", score,
                                "source", src
                        );
                    })
                    .toList();
        } catch (Exception ignore) {
            return List.of();
        }
    }

    private Object safeGet(Map<String, Object> map, String key) {
        return map != null ? map.get(key) : null;
    }

}