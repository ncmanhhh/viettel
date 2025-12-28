package com.datn.viettel.services;

import com.datn.viettel.common.Constants;
import com.datn.viettel.entities.core.FtthPackage;
import com.datn.viettel.entities.core.MobilePackage;
import com.datn.viettel.entities.core.Sim;
import com.datn.viettel.entities.core.VectorStore;
import com.datn.viettel.repositories.core.FtthPackageRepository;
import com.datn.viettel.repositories.core.MobilePackageRepository;
import com.datn.viettel.repositories.core.SimRepository;
import com.datn.viettel.repositories.core.VectorStoreRepository;
import com.datn.viettel.services.iservice.AIService;
import com.datn.viettel.services.iservice.ElasticsearchService;
import com.datn.viettel.services.iservice.EmbedService;
import com.datn.viettel.utils.DataUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmbedServiceImpl implements EmbedService {

    private final MobilePackageRepository mobilePackageRepository;
    private final ElasticsearchService elasticsearchService;
    private final AIService aiService;
    private final Environment environment;
    private final SimRepository simRepository;
    private final FtthPackageRepository ftthPackageRepository;
    private final VectorStoreRepository vectorStoreRepository;

    @Autowired
    public EmbedServiceImpl(MobilePackageRepository mobilePackageRepository,
                            ElasticsearchService elasticsearchService,
                            AIService aiService,
                            Environment environment,
                            SimRepository simRepository,
                            FtthPackageRepository ftthPackageRepository,
                            VectorStoreRepository vectorStoreRepository) {
        this.mobilePackageRepository = mobilePackageRepository;
        this.elasticsearchService = elasticsearchService;
        this.aiService = aiService;
        this.environment = environment;
        this.simRepository = simRepository;
        this.ftthPackageRepository = ftthPackageRepository;
        this.vectorStoreRepository = vectorStoreRepository;
    }

    @NotNull
    private static Map<String, Object> buildContentBodyMobilePackage(MobilePackage mobilePackage) {
        Map<String, Object> description = Map.of(
                "en", Objects.isNull(mobilePackage.getShortDesEn()) ? "" : mobilePackage.getShortDesEn(),
                "vi", Objects.isNull(mobilePackage.getShortDesVi()) ? "" : mobilePackage.getShortDesVi()
        );
        Map<String, Object> content = new HashMap<>();
        content.put("id", mobilePackage.getCode());
        content.put("code", mobilePackage.getCode());
        content.put("money_fee", DataUtils.parseLong(mobilePackage.getMoneyFee()));
        content.put("data_free", DataUtils.parseInteger(mobilePackage.getDataFree()));
        content.put("expire_value", DataUtils.parseInteger(mobilePackage.getExpireValue()));
        content.put("expire_type", mobilePackage.getExpireType());
        content.put("priority", mobilePackage.getPriority());
        content.put("description", description);
        return content;
    }


    @NotNull
    private static Map<String, Object> buildContentBodyFtthPackage(FtthPackage ftth) {
        Map<String, Object> description = Map.of(
                "en", DataUtils.parseString(ftth.getShortDesEn(), ""),
                "vi", DataUtils.parseString(ftth.getShortDesVi(), "")
        );

        Map<String, Object> promotion = Map.of(
                "en", DataUtils.parseString(ftth.getPromotionEn(), ""),
                "vi", DataUtils.parseString(ftth.getPromotionVi(), "")
        );

        Map<String, Object> content = new HashMap<>();
        content.put("id", ftth.getCode());
        content.put("code", ftth.getCode());
        content.put("group_name", DataUtils.parseString(ftth.getGroupName(), ""));
        content.put("cycle", ftth.getCycle());
        content.put("price", DataUtils.parseLong(ftth.getPrice()));
        content.put("promotion_price", DataUtils.parseLong(ftth.getPromotionPrice()));
        content.put("priority", ftth.getPriority() == null ? 0 : ftth.getPriority());
        content.put("speed_network", DataUtils.parseString(ftth.getSpeedNetwork(), ""));
        content.put("description", description);
        content.put("promotion", promotion);

        return content;
    }

    @NotNull
    private Map<String, Object> buildContentBodySim(Sim sim) {
        Map<String, Object> desc = new java.util.LinkedHashMap<>();
        desc.put("en", DataUtils.parseString(sim.getDesEn(), ""));
        desc.put("vi", DataUtils.parseString(sim.getDesVi(), ""));

        Map<String, Object> content = new java.util.LinkedHashMap<>();
        content.put("id", sim.getId() != null ? sim.getId().toString() : null);
        content.put("phone_number", sim.getPhoneNumber());
        content.put("sim_type", sim.getSimType());
        content.put("number_type", sim.getNumberType());

        // price/promotion_price: entity là String => lưu cả raw string
        content.put("price", sim.getPrice());
        content.put("promotion_price", sim.getPromotionPrice());

        content.put("description", desc);
        return content;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void embedMobilePackages() {
        List<MobilePackage> mobilePackages = mobilePackageRepository.findAll();
        if (mobilePackages.isEmpty()) {
            return;
        }
        List<String> productIds = mobilePackages.stream()
                .filter(mobilePackage -> mobilePackage.getStatus().equals(Constants.Status.INACTIVE))
                .map(MobilePackage::getCode)
                .collect(Collectors.toList());
        if (!productIds.isEmpty()) {
            deleteDocuments(environment.getProperty("scheduled.embedding.mobile-package.index"), productIds);
        }
        for (MobilePackage mobilePackage : mobilePackages) {
            if (mobilePackage.getStatus().equals(Constants.Status.INACTIVE)
                    || mobilePackage.getIsEmbed().equals(Constants.Status.ACTIVE)) {
                continue;
            }

            try {
                String contentFull = buildContentEmbedMobilePackage(mobilePackage);
                contentFull = contentFull.replaceAll("(?m)(\\r?\\n){2,}", "\n");

                float[] embedding = aiService.embeddingVectorV2(contentFull);

                Thread.sleep(500);

                if (embedding == null) {
                    log.error("Failed to get embedding for MobilePackage with ID: {}", mobilePackage.getCode());
                    continue;
                }

                Map<String, Object> content = buildContentBodyMobilePackage(mobilePackage);
                Map<String, Object> documentBody = Map.of(
                        "id", mobilePackage.getCode(),
                        "content", content,
                        "contentFull", contentFull,
                        "contentVector", embedding
                );

                boolean created = elasticsearchService.createDocument(
                        environment.getProperty("scheduled.embedding.mobile-package.index"),
                        mobilePackage.getCode(),
                        documentBody
                );

                if (created) {
                    mobilePackage.setIsEmbed(Constants.Status.ACTIVE);
                    mobilePackageRepository.save(mobilePackage);
                } else {
                    log.error("Failed to create document in Elasticsearch for MobilePackage with ID: {}",
                            mobilePackage.getCode());
                }

            } catch (Exception e) {
                log.error("Error embedding MobilePackage with ID: {} - {}",
                        mobilePackage.getCode(), e.getMessage(), e);
            }
        }

    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void embedMobilePackagesV2() {
//        List<MobilePackage> mobilePackages = mobilePackageRepository.findAll();
//        if (!mobilePackages.isEmpty()) {
//            List<Long> productIds = mobilePackages.stream()
//                    .filter(mobilePackage -> mobilePackage.getStatus().equals(Constants.Status.INACTIVE))
//                    .map(MobilePackage::getProductId)
//                    .collect(Collectors.toList());
//            if (!productIds.isEmpty()) {
//                deleteVectorStoreByProductId(environment.getProperty("scheduled.embedding.mobile-package.index"), productIds);
//            }
//            for (MobilePackage mobilePackage : mobilePackages) {
//                if (mobilePackage.getStatus().equals(Constants.Status.INACTIVE) || mobilePackage.getIsEmbed().equals(Constants.Status.ACTIVE)) {
//                    continue;
//                }
//                try {
//                    String contentFull = buildContentEmbedMobilePackage(mobilePackage);
//                    contentFull = contentFull.replaceAll("(?m)(\\r?\\n){2,}", "\n");
//                    float[] embedding = aiService.embeddingVectorV2(contentFull);
//                    if (embedding != null) {
//                        Map<String, Object> content = buildContentBodyMobilePackage(mobilePackage);
//                        String vectorString = Arrays.toString(embedding);
//                        String metadataJson = convertMapToJsonString(content);
//                        LocalDateTime now = LocalDateTime.now();
//                        vectorStoreRepository.insertVectorStore(
//                                mobilePackage.getId(),
//                                environment.getProperty("scheduled.embedding.mobile-package.index"),
//                                contentFull,
//                                metadataJson,
//                                vectorString,
//                                now,
//                                now,
//                                "PROCESS",
//                                "PROCESS"
//                        );
//                        Map<String, Object> documentBody = Map.of(
//                                "id", mobilePackage.getProductId().toString(),
//                                "content", content,
//                                "contentFull", contentFull,
//                                "contentVector", embedding
//                        );
//                        elasticsearchService.createDocument(environment.getProperty("scheduled.embedding.mobile-package.index"),
//                                String.valueOf(mobilePackage.getProductId()), documentBody);
//                        mobilePackage.setIsEmbed(Constants.Status.ACTIVE);
//                        mobilePackageRepository.save(mobilePackage);
//                    } else {
//                        log.error("Failed to get embedding for MobilePackage with ID - v2: {}", mobilePackage.getId());
//                    }
//                } catch (Exception e) {
//                    log.error("Error embedding MobilePackage with ID - v2: {} - {}", mobilePackage.getId(), e.getMessage());
//                }
//            }
//        }
//    }
@Override
@Transactional(rollbackFor = Exception.class)
public void embedMobilePackagesV2() {
    List<MobilePackage> mobilePackages = mobilePackageRepository.findAll();
    if (mobilePackages == null || mobilePackages.isEmpty()) return;

    String index = environment.getProperty("scheduled.embedding.mobile-package.index");

    // 1) Xoá vector_store cho các record INACTIVE (theo UUID id)
    List<UUID> inactiveIds = mobilePackages.stream()
            .filter(p -> p.getStatus().equals(Constants.Status.INACTIVE))
            .map(MobilePackage::getId)
            .filter(Objects::nonNull)
            .toList();

    if (!inactiveIds.isEmpty()) {
        deleteVectorStoreByProductId(index, inactiveIds);
    }

    // 2) Embed + lưu vector_store(pgvector) + lưu ES
    for (MobilePackage mobilePackage : mobilePackages) {
        if (mobilePackage.getStatus().equals(Constants.Status.INACTIVE)
                || mobilePackage.getIsEmbed().equals(Constants.Status.ACTIVE)) {
            continue;
        }

        try {
            String contentFull = buildContentEmbedMobilePackage(mobilePackage)
                    .replaceAll("(?m)(\\r?\\n){2,}", "\n");

            float[] embedding = aiService.embeddingVectorV2(contentFull);
            if (embedding == null || embedding.length == 0) {
                log.error("Failed to get embedding for MobilePackage id={}", mobilePackage.getId());
                continue;
            }

            Map<String, Object> content = buildContentBodyMobilePackage(mobilePackage);
            LocalDateTime now = LocalDateTime.now();

            // ✅ 2.1 Upsert vào vector_store (Postgres pgvector) bằng float[]
            // Xoá bản cũ để tránh trùng (khuyến nghị thêm unique (prod_id, prod_type))
            vectorStoreRepository.deleteByProdIdAndProdType(mobilePackage.getId(), index);

            VectorStore vs = new VectorStore();
            vs.setProdId(mobilePackage.getId());          // ✅ UUID
            vs.setProdType(index);                        // hoặc "MOBILE" nếu bạn muốn tách logical type
            vs.setContent(contentFull);
            vs.setMetadata(content);                      // ✅ Map -> jsonb
            vs.setEmbVector(embedding);                   // ✅ float[] -> vector(768)

            // BaseEntity audit (nếu bạn không có auditing tự set)
            vs.setCreatedAt(now);
            vs.setUpdatedAt(now);
            vs.setCreatedBy("PROCESS");
            vs.setUpdatedBy("PROCESS");

            vectorStoreRepository.save(vs);

            // ✅ 2.2 Lưu lên Elasticsearch
            Map<String, Object> documentBody = Map.of(
                    "id", String.valueOf(mobilePackage.getProductId()), // hoặc code tuỳ bạn
                    "content", content,
                    "contentFull", contentFull,
                    "contentVector", embedding
            );

            // Khuyến nghị: createDocument nên là upsert (/ _doc /id) để chạy lại không lỗi 409
            elasticsearchService.createDocument(
                    index,
                    String.valueOf(mobilePackage.getProductId()),
                    documentBody
            );

            // ✅ 2.3 Update trạng thái embed
            mobilePackage.setIsEmbed(Constants.Status.ACTIVE);
            mobilePackageRepository.save(mobilePackage);

        } catch (Exception e) {
            log.error("Error embedding MobilePackage id={} - {}", mobilePackage.getId(), e.getMessage(), e);
        }
    }
}

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void embedFtthPackages() {
//        List<FtthPackage> ftthPackages = ftthPackageRepository.findAll();
//        if (ftthPackages.isEmpty()) {
//            return;
//        }
//        List<String> productCodes = ftthPackages.stream()
//                .filter(ftthPackage -> ftthPackage.getStatus().equals(Constants.Status.INACTIVE))
//                .map(FtthPackage::getCode)
//                .collect(Collectors.toList());
//        if (!productCodes.isEmpty()) {
//            deleteDocuments(environment.getProperty("scheduled.embedding.ftth-package.index"), productCodes);
//        }
//        for (FtthPackage ftthPackage : ftthPackages) {
//            if (ftthPackage.getStatus().equals(Constants.Status.INACTIVE)
//                    || ftthPackage.getIsEmbed().equals(Constants.Status.ACTIVE)) {
//                continue;
//            }
//
//            try {
//                String contentFull = buildContentEmbedFtthPackage(ftthPackage);
//                contentFull = contentFull.replaceAll("(?m)(\\r?\\n){2,}", "\n");
//
//                float[] embedding = aiService.embeddingVectorV2(contentFull);
//                if (embedding == null) {
//                    log.error("Failed to get embedding for FTTH with ID: {}", ftthPackage.getCode());
//                    continue;
//                }
//
//                Map<String, Object> content = buildContentBodyFtthPackage(ftthPackage);
//                Map<String, Object> documentBody = Map.of(
//                        "id", ftthPackage.getCode(),
//                        "content", content,
//                        "contentFull", contentFull,
//                        "contentVector", embedding
//                );
//
//                boolean created = elasticsearchService.createDocument(
//                        environment.getProperty("scheduled.embedding.ftth-package.index"),
//                        ftthPackage.getCode(),
//                        documentBody
//                );
//
//                if (created) {
//                    ftthPackage.setIsEmbed(Constants.Status.ACTIVE);
//                    ftthPackageRepository.save(ftthPackage);
//                } else {
//                    log.error("Failed to create document in Elasticsearch for FTTH with ID: {}",
//                            ftthPackage.getCode());
//                }
//
//            } catch (Exception e) {
//                log.error("Error embedding FTTH with ID: {} - {}",
//                        ftthPackage.getCode(), e.getMessage(), e);
//            }
//        }
//
//    }
@Override
@Transactional(rollbackFor = Exception.class)
public void embedFtthPackages() {

    List<FtthPackage> ftthPackages = ftthPackageRepository.findAll();
    if (ftthPackages == null || ftthPackages.isEmpty()) return;

    String index = environment.getProperty("scheduled.embedding.ftth-package.index");

    // 1) Xoá vector_store cho các record INACTIVE (theo UUID id)
    List<UUID> inactiveIds = ftthPackages.stream()
            .filter(p -> p.getStatus().equals(Constants.Status.INACTIVE))
            .map(FtthPackage::getId)
            .filter(Objects::nonNull)
            .toList();

    if (!inactiveIds.isEmpty()) {
        deleteVectorStoreByProductId(index, inactiveIds);

        // (tuỳ bạn) nếu bạn vẫn muốn xoá luôn ES documents cho INACTIVE theo code:
        List<String> inactiveCodes = ftthPackages.stream()
                .filter(p -> p.getStatus().equals(Constants.Status.INACTIVE))
                .map(FtthPackage::getCode)
                .filter(Objects::nonNull)
                .toList();
        if (!inactiveCodes.isEmpty()) {
            deleteDocuments(index, inactiveCodes);
        }
    }

    // 2) Embed + lưu vector_store(pgvector) + lưu ES
    for (FtthPackage ftthPackage : ftthPackages) {
        if (ftthPackage.getStatus().equals(Constants.Status.INACTIVE)
                || ftthPackage.getIsEmbed().equals(Constants.Status.ACTIVE)) {
            continue;
        }

        try {
            String contentFull = buildContentEmbedFtthPackage(ftthPackage)
                    .replaceAll("(?m)(\\r?\\n){2,}", "\n");

            float[] embedding = aiService.embeddingVectorV2(contentFull);
            if (embedding == null || embedding.length == 0) {
                log.error("Failed to get embedding for FTTH id={} code={}", ftthPackage.getId(), ftthPackage.getCode());
                continue;
            }

            Map<String, Object> content = buildContentBodyFtthPackage(ftthPackage);
            LocalDateTime now = LocalDateTime.now();

            // ✅ 2.1 Upsert vào vector_store (Postgres pgvector)
            // Xoá bản cũ để tránh trùng (khuyến nghị unique (prod_id, prod_type))
            vectorStoreRepository.deleteByProdIdAndProdType(ftthPackage.getId(), index);

            VectorStore vs = new VectorStore();
            vs.setProdId(ftthPackage.getId());          // ✅ UUID
            vs.setProdType(index);                      // ✅ dùng index làm type (giống Mobile)
            vs.setContent(contentFull);
            vs.setMetadata(content);                    // ✅ Map -> jsonb
            vs.setEmbVector(embedding);                 // ✅ float[] -> vector(768)

            // BaseEntity audit
            vs.setCreatedAt(now);
            vs.setUpdatedAt(now);
            vs.setCreatedBy("PROCESS");
            vs.setUpdatedBy("PROCESS");

            try {
                vectorStoreRepository.save(vs);
            } catch (Exception ex) {
                log.error("❌ VectorStore save failed: ftthId={}, code={}, prodType={}, contentLen={}, err={}",
                        ftthPackage.getId(),
                        ftthPackage.getCode(),
                        index,
                        contentFull != null ? contentFull.length() : 0,
                        ex.getMessage(),
                        ex
                );
                continue; // rất quan trọng: không update isEmbed nếu VectorStore fail
            }


            // ✅ 2.2 Lưu lên Elasticsearch
            // id: ưu tiên dùng code vì FTTH code thường unique và dễ đọc
            String esId = ftthPackage.getCode() != null ? ftthPackage.getCode() : String.valueOf(ftthPackage.getId());

            Map<String, Object> documentBody = Map.of(
                    "id", esId,
                    "content", content,
                    "contentFull", contentFull,
                    "contentVector", embedding
            );

            elasticsearchService.createDocument(
                    index,
                    esId,
                    documentBody
            );

            // ✅ 2.3 Update trạng thái embed
            ftthPackage.setIsEmbed(Constants.Status.ACTIVE);
            ftthPackageRepository.save(ftthPackage);

        } catch (Exception e) {
            log.error("Error embedding FTTH id={} code={} - {}",
                    ftthPackage.getId(), ftthPackage.getCode(), e.getMessage(), e);
        }
    }
}


@Override
@Transactional(rollbackFor = Exception.class)
public void embedSims() {

    // ✅ 0) Lấy toàn bộ sims (giữ style giống bạn), nhưng khuyến nghị query theo điều kiện như dưới
    List<Sim> sims = simRepository.findAll();
    if (sims == null || sims.isEmpty()) return;

    String index = environment.getProperty("scheduled.embedding.sim.index");

    // 1) Xoá vector_store + ES cho record INACTIVE
    List<UUID> inactiveIds = sims.stream()
            .filter(s -> s.getStatus().equals(Constants.Status.INACTIVE))
            .map(Sim::getId)
            .filter(Objects::nonNull)
            .toList();

    if (!inactiveIds.isEmpty()) {
        // ✅ xóa vector store theo UUID
        deleteVectorStoreByProductId(index, inactiveIds);
    }

    List<String> inactivePhones = sims.stream()
            .filter(s -> s.getStatus().equals(Constants.Status.INACTIVE))
            .map(Sim::getPhoneNumber)
            .filter(Objects::nonNull)
            .toList();

    if (!inactivePhones.isEmpty()) {
        // ✅ xoá ES theo phone_number (id ES bạn dùng phoneNumber)
        deleteDocuments(index, inactivePhones);
    }

    // 2) Embed + lưu vector_store + lưu ES
    for (Sim sim : sims) {

        // chỉ embed record ACTIVE và CHƯA EMBED
        if (sim.getStatus().equals(Constants.Status.INACTIVE)
                || sim.getIsEmbed().equals(Constants.Status.ACTIVE)) {
            continue;
        }

        try {
            String contentFull = buildContentEmbedSim(sim)
                    .replaceAll("(?m)(\\r?\\n){2,}", "\n")
                    .trim();

            if (contentFull.isBlank()) {
                log.error("SIM contentFull blank, skip. phone={}", sim.getPhoneNumber());
                continue;
            }

            float[] embedding = aiService.embeddingVectorV2(contentFull);

            // ✅ delay nhẹ để tránh quota (bạn có thể chỉnh 400-900)
//            sleepRandom(400, 900);

            if (embedding == null || embedding.length == 0) {
                log.error("Failed to get embedding for SIM phone={}", sim.getPhoneNumber());
                continue;
            }

            Map<String, Object> content = buildContentBodySim(sim);
            LocalDateTime now = LocalDateTime.now();

            // ✅ 2.1 Upsert vào vector_store (pgvector)
            vectorStoreRepository.deleteByProdIdAndProdType(sim.getId(), index);

            VectorStore vs = new VectorStore();
            vs.setProdId(sim.getId());         // ✅ UUID
            vs.setProdType(index);             // ✅ type = index
            vs.setContent(contentFull);
            vs.setMetadata(content);
            vs.setEmbVector(embedding);

            vs.setCreatedAt(now);
            vs.setUpdatedAt(now);
            vs.setCreatedBy("PROCESS");
            vs.setUpdatedBy("PROCESS");

            vectorStoreRepository.save(vs);

            // ✅ 2.2 Lưu lên Elasticsearch
            // ES _id dùng phoneNumber như code cũ của bạn
            String esId = sim.getPhoneNumber();
            if (esId == null || esId.isBlank()) {
                log.error("SIM phoneNumber null/blank, skip ES. simId={}", sim.getId());
                continue;
            }

            Map<String, Object> documentBody = Map.of(
                    "id", esId,
                    "content", content,
                    "contentFull", contentFull,
                    "contentVector", embedding
            );

            // Khuyến nghị: dùng upsert (_doc) để chạy lại không lỗi 409
            elasticsearchService.createDocument(index, esId, documentBody);

            // ✅ 2.3 Update trạng thái embed
            sim.setIsEmbed(Constants.Status.ACTIVE);
            simRepository.save(sim);

        } catch (Exception e) {
            log.error("Error embedding SIM phone={} - {}", sim.getPhoneNumber(), e.getMessage(), e);

            // ✅ quota -> nghỉ lâu
//            if (isQuotaExceeded(e)) {
//                sleepRandom(5000, 20000);
//            }
        }
    }
}



    private String buildContentEmbedMobilePackage (MobilePackage mobilePackage) {
        String code = DataUtils.parseString(mobilePackage.getCode(), "").trim();
        long moneyFee = Long.parseLong(mobilePackage.getMoneyFee());            // LAK hoặc VND tuỳ hệ của bạn
        long dataMb = Long.parseLong(mobilePackage.getDataFree());              // DB đang lưu MB (theo code cũ bạn)
        long dataGb = dataMb > 0 ? (dataMb / 1024) : 0;

        String expireType = DataUtils.parseString(mobilePackage.getExpireType(), "").trim();
        String expireValue = DataUtils.parseString(mobilePackage.getExpireValue(), "").trim();

        String shortEn = DataUtils.parseString(mobilePackage.getShortDesEn(), "");
        String shortVi = DataUtils.parseString(mobilePackage.getShortDesVi(), "");

        StringBuilder sb = new StringBuilder();
        sb.append("=== MOBILE PACKAGE ===\n");
        sb.append("Code: ").append(code).append("\n");

        sb.append("Type: mobile data package; prepaid; internet data\n"); // keyword ngữ nghĩa
        sb.append("Price: ").append(moneyFee).append(" (currency)\n");
        sb.append("Data allowance: ").append(dataMb).append(" MB");
        if (dataGb > 0) sb.append(" (").append(dataGb).append(" GB)");
        sb.append("\n");

        sb.append("Validity: ").append(expireValue);
        if (!expireType.isBlank()) sb.append(" ").append(expireType);
        sb.append("\n");

        sb.append("Short description (EN): ").append(shortEn).append("\n");
        sb.append("Short description (VI): ").append(shortVi).append("\n");

        // Thêm vài câu “định dạng hỏi” để tăng recall khi search
        sb.append("User queries: ");
        sb.append("\"gói ").append(code).append("\", ");
        if (moneyFee > 0) sb.append("\"gói ").append(moneyFee).append("\", ");
        if (dataGb > 0) sb.append("\"gói ").append(dataGb).append("GB\", ");
        sb.append("\"gói data\", \"gói internet\", \"gói ").append(expireValue).append(" ").append(expireType).append("\"");
        sb.append("\n");

        // giảm nhiễu xuống dòng
        return sb.toString().replaceAll("(?m)(\\r?\\n){2,}", "\n").trim();
    }


private String buildContentEmbedFtthPackage(FtthPackage ftth) {
    String code = DataUtils.parseString(ftth.getCode(), "").trim();

    String groupName = DataUtils.parseString(ftth.getGroupName(), "").trim();
    String speedNetwork = DataUtils.parseString(ftth.getSpeedNetwork(), "").trim();

    long price = DataUtils.parseLong(ftth.getPrice());
    long promoPrice = DataUtils.parseLong(ftth.getPromotionPrice());

    String shortEn = DataUtils.parseString(ftth.getShortDesEn(), "");
    String shortVi = DataUtils.parseString(ftth.getShortDesVi(), "");

    String promoEn = DataUtils.parseString(ftth.getPromotionEn(), "");
    String promoVi = DataUtils.parseString(ftth.getPromotionVi(), "");

    StringBuilder sb = new StringBuilder();
    sb.append("=== FTTH PACKAGE ===\n");
    sb.append("Code: ").append(code).append("\n");

    sb.append("Type: fiber internet package; FTTH; home internet\n"); // keyword ngữ nghĩa
    if (!groupName.isBlank()) sb.append("Group name: ").append(groupName).append("\n");

    if (!speedNetwork.isBlank()) sb.append("Speed: ").append(speedNetwork).append("\n");

    sb.append("Price: ").append(price).append(" (currency)\n");
    if (promoPrice > 0) sb.append("Promotion price: ").append(promoPrice).append(" (currency)\n");

    sb.append("Description (EN): ").append(shortEn).append("\n");
    sb.append("Description (VI): ").append(shortVi).append("\n");

    if (!promoEn.isBlank()) sb.append("Promotion (EN): ").append(promoEn).append("\n");
    if (!promoVi.isBlank()) sb.append("Promotion (VI): ").append(promoVi).append("\n");

    // cycle info (JSON -> text)
    String cycleRaw = DataUtils.parseString(ftth.getCycle(), "").trim();
    if (!cycleRaw.isBlank()) {
        sb.append("Cycle options:\n");
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> cycles = mapper.readValue(cycleRaw, new TypeReference<>() {});
            for (Map<String, Object> c : cycles) {
                String months = DataUtils.parseString(c.get("cycle"), "");
                String cPrice = DataUtils.parseString(c.get("price"), "");
                String discount = DataUtils.parseString(c.get("percent_discount"), "");
                String bonus = DataUtils.parseString(c.get("bonus"), "");

                sb.append("- ").append(months).append(" month(s)");
                if (!cPrice.isBlank()) sb.append(", price ").append(cPrice).append("VNĐ");
                if (!discount.isBlank()) sb.append(", discount ").append(discount).append("%");
                if (!bonus.isBlank()) sb.append(", bonus ").append(bonus).append(" month(s)");
                sb.append("\n");
            }
        } catch (Exception e) {
            log.warn("FTTH cycle JSON parse failed for code {}: {}", code, e.getMessage());
            sb.append("- cycle_raw: ").append(cycleRaw).append("\n");
        }
    }

    // thêm câu “định dạng hỏi” để tăng recall
    sb.append("User queries: ");
    sb.append("\"internet ").append(speedNetwork).append("\", ");
    sb.append("\"cáp quang ").append(speedNetwork).append("\", ");
    sb.append("\"gói ").append(code).append("\", ");
    if (price > 0) sb.append("\"gói ").append(price).append("\", ");
    sb.append("\"gói FTTH\", \"internet gia đình\"");
    sb.append("\n");

    return sb.toString().replaceAll("(?m)(\\r?\\n){2,}", "\n").trim();
}


    private String buildContentEmbedSim(Sim sim) {
        return "Phone number: " + DataUtils.parseString(sim.getPhoneNumber(), "") + "\n" +
                "Sim type: " + DataUtils.parseString(sim.getSimType(), "") + "\n" +
                "Number type: " + DataUtils.parseString(sim.getNumberType(), "") + "\n" +
                "Price: " + DataUtils.parseString(sim.getPrice(), "") + "\n" +
                "Promotion price: " + DataUtils.parseString(sim.getPromotionPrice(), "") + "\n" +
                "Description (EN): " + DataUtils.parseString(sim.getDesEn(), "") + "\n" +
                "Description (VI): " + DataUtils.parseString(sim.getDesVi(), "");
    }


    private void deleteDocuments(String index, List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            log.info("No product IDs to delete from index: {}", index);
            return;
        }
        try {
            int totalIds = productIds.size();
            log.info("Starting deletion of {} documents from index: {}", totalIds, index);
            List<List<String>> batches = partitionList(productIds);
            int successCount = 0;
            int failureCount = 0;
            for (int i = 0; i < batches.size(); i++) {
                List<String> batch = batches.get(i);
                try {
                    Map<String, Object> query = Map.of(
                            "query", Map.of(
                                    "terms", Map.of("content.id", batch)
                            )
                    );
                    String result = elasticsearchService.deleteDocuments(index, query);
                    successCount += batch.size();
                    log.info("Batch {}/{} completed - Deleted {} documents from index {}: {}",
                            i + 1, batches.size(), batch.size(), index, result);
                } catch (Exception e) {
                    failureCount += batch.size();
                    log.error("Error deleting batch {}/{} from Elasticsearch index {}: {}",
                            i + 1, batches.size(), index, e.getMessage());
                }
            }
            log.info("Deletion completed for index {}. Success: {}, Failed: {}, Total: {}",
                    index, successCount, failureCount, totalIds);
        } catch (Exception e) {
            log.error("Error during batch deletion process for index {}: {}", index, e.getMessage());
        }
    }

    private <T> List<List<T>> partitionList(List<T> list) {
        List<List<T>> partitions = new ArrayList<>();
        int batchSize = 2000;
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(list.size(), i + batchSize);
            partitions.add(new ArrayList<>(list.subList(i, end)));
        }
        return partitions;
    }

//    private void deleteVectorStoreByProductId(String index, List<Long> productIds) {
//        try {
//            vectorStoreRepository.deleteByProdIdAndProdType(productIds, index);
//        } catch (Exception e) {
//            log.error("Error deleting vector store: {}", e.getMessage());
//        }
//    }
private void deleteVectorStoreByProductId(String index, List<UUID> productIds) {
    if (productIds == null || productIds.isEmpty()) {
        log.info("No vector_store records to delete for index {}", index);
        return;
    }

    try {
        vectorStoreRepository.deleteByProdIdInAndProdType(productIds, index);
        log.info("Deleted {} vector_store records for index {}", productIds.size(), index);
    } catch (Exception e) {
        log.error("Error deleting vector store for index {}: {}", index, e.getMessage(), e);
    }
}


    private String convertMapToJsonString(Map<String, Object> map) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("Error converting map to JSON: {}", e.getMessage());
            return "{}";
        }
    }

}

