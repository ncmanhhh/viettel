package com.datn.viettel.dto.elasticsearch;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ElasticsearchResultMapper {

    /**
     * Mapper kết quả search (_search, _msearch)
     * Chuẩn hóa dữ liệu trả về cho FE
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> mapSearchResult(Map<String, Object> esResponse) {

        Map<String, Object> hits = (Map<String, Object>) esResponse.get("hits");
        if (hits == null) {
            return Map.of("total", 0, "items", List.of());
        }

        // Tổng số document match
        Object totalObj = hits.get("total");
        long total = extractTotal(totalObj);

        // Danh sách hit
        List<Map<String, Object>> hitList =
                (List<Map<String, Object>>) hits.getOrDefault("hits", List.of());

        List<Map<String, Object>> items = hitList.stream()
                .map(this::mapSingleHit)
                .toList();

        return Map.of(
                "total", total,
                "items", items
        );
    }


    /**
     * Mapper kết quả GET /_doc/{id}
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> mapGetDocumentResult(Map<String, Object> esResponse) {

        Boolean found = (Boolean) esResponse.get("found");
        if (found == null || !found) {
            return Map.of(
                    "found", false
            );
        }

        String id = (String) esResponse.get("_id");
        String index = (String) esResponse.get("_index");

        Map<String, Object> source =
                (Map<String, Object>) esResponse.get("_source");

        Map<String, Object> content =
                source != null
                        ? (Map<String, Object>) source.getOrDefault("content", Map.of())
                        : Map.of();

        return Map.of(
                "found", true,
                "id", id,
                "index", index,
                "content", content
        );
    }


    /**
     * Mapper 1 document trong hits.hits[]
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> mapSingleHit(Map<String, Object> hit) {

        String id = (String) hit.get("_id");
        Double score = hit.get("_score") != null
                ? ((Number) hit.get("_score")).doubleValue()
                : null;

        Map<String, Object> source =
                (Map<String, Object>) hit.get("_source");

        // Detect index để xử lý content phù hợp
        String index = (String) hit.get("_index");
        IndexType indexType = detectIndexType(index);

        Map<String, Object> content = extractContent(source, indexType);

        return Map.of(
                "id", id,
                "score", score,
                "content", content
        );
    }

    /**
     * Trích xuất content theo từng loại index
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractContent(
            Map<String, Object> source,
            IndexType indexType
    ) {

        if (source == null) return Map.of();

        Map<String, Object> content =
                (Map<String, Object>) source.get("content");

        if (content == null) return Map.of();

        // Có thể custom thêm logic từng loại nếu cần
        return switch (indexType) {
            case FTTH, MOBILE, SIM -> content;
        };
    }

    /**
     * Detect index hiện tại
     */
    private IndexType detectIndexType(String indexName) {
        if (indexName == null) return IndexType.MOBILE;

        if (indexName.contains("ftth")) return IndexType.FTTH;
        if (indexName.contains("mobile")) return IndexType.MOBILE;
        if (indexName.contains("sim")) return IndexType.SIM;

        return IndexType.MOBILE;
    }

    /**
     * ES 7.x / 8.x có 2 kiểu total
     */
    @SuppressWarnings("unchecked")
    private long extractTotal(Object totalObj) {
        if (totalObj instanceof Map<?, ?> map) {
            Object value = map.get("value");
            return value != null ? ((Number) value).longValue() : 0;
        }
        if (totalObj instanceof Number number) {
            return number.longValue();
        }
        return 0;
    }

    /**
     * Enum xác định loại index
     */
    private enum IndexType {
        FTTH,
        MOBILE,
        SIM
    }
}
