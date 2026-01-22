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
                "vi", Objects.isNull(mobilePackage.getShortDesVi()) ? "" : mobilePackage.getShortDesVi()
        );
        Map<String, Object> fullDescription = Map.of(
                "vi", Objects.isNull(mobilePackage.getFullDesVi()) ? "" : mobilePackage.getFullDesVi()
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
        content.put("full_description", fullDescription);
        return content;
    }


    @NotNull
    private static Map<String, Object> buildContentBodyFtthPackage(FtthPackage ftthPackage) {
        Map<String, Object> description = Map.of(
                "vi", Objects.isNull(ftthPackage.getShortDesVi()) ? "" : ftthPackage.getShortDesVi()
        );
        Map<String, Object> promotion = Map.of(
                "vi", Objects.isNull(ftthPackage.getPromotionVi()) ? "" : ftthPackage.getPromotionVi()
        );
        Map<String, Object> content = new HashMap<>();
        content.put("id", ftthPackage.getCode());
        content.put("code", ftthPackage.getCode());
        content.put("group_name", ftthPackage.getGroupName());
        content.put("speed_in_text", ftthPackage.getSpeedInText());
        content.put("speed", ftthPackage.getSpeed());
        content.put("cycle", ftthPackage.getCycle());
        content.put("cycle_raw", ftthPackage.getCycleRaw());
        content.put("price", DataUtils.parseLong(ftthPackage.getPrice()));
        content.put("promotion_price", DataUtils.parseLong(ftthPackage.getPromotionPrice()));
        content.put("priority", ftthPackage.getPriority());
        content.put("description", description);
        content.put("promotion", promotion);
        return content;
    }

    @NotNull
    private static Map<String, Object> buildContentBodySim(Sim sim) {
        Map<String, Object> content = new HashMap<>();
        content.put("id", sim.getPhoneNumber());
        content.put("phone_number", sim.getPhoneNumber());
        content.put("sim_type", sim.getSimType());
        content.put("number_type", sim.getNumberType());
        content.put("price", DataUtils.parseLong(sim.getPrice()));
        content.put("promotion_price", DataUtils.parseLong(sim.getPromotionPrice()));
        Map<String, Object> description = Map.of(
                "vi", Objects.isNull(sim.getDesVi()) ? "" : sim.getDesVi()
        );
        content.put("description", description);
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

        // 1️⃣ Xoá vector_store
        deleteVectorStoreByProductId(index, inactiveIds);

        // 2️⃣ Xoá Elasticsearch theo ES _id
        // ⚠️ Bạn đang dùng ES id = productId
        List<String> inactiveEsIds = mobilePackages.stream()
                .filter(p -> p.getStatus().equals(Constants.Status.INACTIVE))
                .map(p -> p.getId().toString())
                .toList();

        deleteDocuments(index, inactiveEsIds);
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
            vs.setProdType(index);
            vs.setContent(contentFull);
            vs.setMetadata(content);                      // ✅ Map -> jsonb
            vs.setEmbVector(embedding);                   // ✅ float[] -> vector(768)

            // BaseEntity audit
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

            String esId = mobilePackage.getId().toString();

            elasticsearchService.createDocument(
                    index,
                    esId,
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

        // Xoá ES theo code
        List<String> inactiveEsIds = ftthPackages.stream()
                .filter(p -> p.getStatus().equals(Constants.Status.INACTIVE))
                .map(p -> p.getId().toString())
                .toList();

        deleteDocuments(index, inactiveEsIds);
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
            vectorStoreRepository.deleteByProdIdAndProdType(ftthPackage.getId(), index);

            VectorStore vs = new VectorStore();
            vs.setProdId(ftthPackage.getId());
            vs.setProdType(index);
            vs.setContent(contentFull);
            vs.setMetadata(content);
            vs.setEmbVector(embedding);

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
            String esId = ftthPackage.getId().toString();

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
    List<Sim> sims = simRepository.findAll();
    if (sims.isEmpty()) {
        return;
    }
    List<String> productPhoneNumbers = sims.stream()
            .filter(sim -> sim.getStatus().equals(Constants.Status.INACTIVE))
            .map(Sim::getPhoneNumber)
            .collect(Collectors.toList());
    if (!productPhoneNumbers.isEmpty()) {
        deleteDocuments(environment.getProperty("scheduled.embedding.sim.index"), productPhoneNumbers);
    }
    List<Sim> simsToEmbed = sims.stream()
            .filter(sim -> !sim.getStatus().equals(Constants.Status.INACTIVE) &&
                    !sim.getIsEmbed().equals(Constants.Status.ACTIVE))
            .collect(Collectors.toList());
    if (simsToEmbed.isEmpty()) {
        log.info("No sims to embed");
        return;
    }
    log.info("Starting embedding process for {} sims", simsToEmbed.size());
    int batchSize = 500;
    List<List<Sim>> batches = partitionSimList(simsToEmbed, batchSize);
    int totalBatches = batches.size();
    int successCount = 0;
    int failureCount = 0;
    for (int i = 0; i < totalBatches; i++) {
        List<Sim> batch = batches.get(i);
        log.info("Processing batch {}/{} with {} sims", i + 1, totalBatches, batch.size());
        List<Sim> batchResults = batch.parallelStream()
                .map(this::processSingleSim)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!batchResults.isEmpty()) {
            try {
                simRepository.saveAll(batchResults);
                successCount += batchResults.size();
                log.info("Batch {}/{} completed successfully. Processed: {}/{}",
                        i + 1, totalBatches, batchResults.size(), batch.size());
            } catch (Exception e) {
                log.error("Error saving batch {}/{}: {}", i + 1, totalBatches, e.getMessage());
                failureCount += batch.size();
            }
        }
        failureCount += (batch.size() - batchResults.size());
        if (i < totalBatches - 1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Embedding process interrupted");
                break;
            }
        }
    }
    log.info("Embedding process completed. Success: {}, Failed: {}, Total: {}", successCount, failureCount, simsToEmbed.size());
}
    private Sim processSingleSim(Sim sim) {
        try {
            // 1️⃣ Build content
            String contentFull = buildContentEmbedSim(sim)
                    .replaceAll("(?m)(\\r?\\n){2,}", "\n")
                    .trim();

            if (contentFull.isBlank()) {
                log.warn("SIM content blank, skip. phone={}", sim.getPhoneNumber());
                return null;
            }

            // 2️⃣ Embedding
            float[] embedding = aiService.embeddingVectorV2(contentFull);
            if (embedding == null || embedding.length == 0) {
                log.error("Embedding failed for SIM phone={}", sim.getPhoneNumber());
                return null;
            }

            // 3️⃣ VectorStore (pgvector)
            String prodType = environment.getProperty("scheduled.embedding.sim.index");

            // xoá vector cũ để tránh trùng
            vectorStoreRepository.deleteByProdIdAndProdType(sim.getId(), prodType);

            VectorStore vs = new VectorStore();
            vs.setProdId(sim.getId());                 // UUID
            vs.setProdType(prodType);                  // SIM
            vs.setContent(contentFull);                // text
            vs.setMetadata(buildContentBodySim(sim));  // jsonb
            vs.setEmbVector(embedding);                // vector(768)

            vs.setCreatedAt(LocalDateTime.now());
            vs.setUpdatedAt(LocalDateTime.now());
            vs.setCreatedBy("EMBED_JOB");
            vs.setUpdatedBy("EMBED_JOB");

            vectorStoreRepository.save(vs);

            // 4️⃣ Elasticsearch
            String index = environment.getProperty("scheduled.embedding.sim.index");
            String esId = sim.getPhoneNumber();

            Map<String, Object> documentBody = Map.of(
                    "id", esId,
                    "content", buildContentBodySim(sim),
                    "contentFull", contentFull,
                    "contentVector", embedding
            );

            elasticsearchService.createDocument(index, esId, documentBody);

            // 5️⃣ Update trạng thái
            sim.setIsEmbed(Constants.Status.ACTIVE);
            return sim;

        } catch (Exception e) {
            log.error("Error embedding SIM phone={} - {}", sim.getPhoneNumber(), e.getMessage(), e);
            return null;
        }
    }

    private List<List<Sim>> partitionSimList(List<Sim> list, int batchSize) {
        List<List<Sim>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(list.size(), i + batchSize);
            partitions.add(new ArrayList<>(list.subList(i, end)));
        }
        return partitions;
    }



//        private String buildContentEmbedMobilePackage (MobilePackage mobilePackage) {
//        String code = DataUtils.parseString(mobilePackage.getCode(), "").trim();
//        long moneyFee = Long.parseLong(mobilePackage.getMoneyFee());
//        long dataMb = Long.parseLong(mobilePackage.getDataFree());
//        long dataGb = dataMb > 0 ? (dataMb / 1024) : 0;
//
//        String expireType = DataUtils.parseString(mobilePackage.getExpireType(), "").trim();
//        String expireValue = DataUtils.parseString(mobilePackage.getExpireValue(), "").trim();
//
//        String shortVi = DataUtils.parseString(mobilePackage.getShortDesVi(), "");
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("MOBILE PACKAGE\n");
//        sb.append("Code: ").append(code).append("\n");
//
//        sb.append("Type: mobile data package; prepaid; internet data\n"); // keyword ngữ nghĩa
//        sb.append("Price: ").append(moneyFee).append(" (currency)\n");
//        sb.append("Data allowance: ").append(dataMb).append(" MB");
//        if (dataGb > 0) sb.append(" (").append(dataGb).append(" GB)");
//        sb.append("\n");
//
//        sb.append("Validity: ").append(expireValue);
//        if (!expireType.isBlank()) sb.append(" ").append(expireType);
//        sb.append("\n");
//
//        sb.append("Short description (VI): ").append(shortVi).append("\n");
//
//        // Thêm vài câu “định dạng hỏi” để tăng recall khi search
//        sb.append("User queries: ");
//        sb.append("\"gói ").append(code).append("\", ");
//        if (moneyFee > 0) sb.append("\"gói ").append(moneyFee).append("\", ");
//        if (dataGb > 0) sb.append("\"gói ").append(dataGb).append("GB\", ");
//        sb.append("\"gói data\", \"gói internet\", \"gói ").append(expireValue).append(" ").append(expireType).append("\"");
//        sb.append("\n");
//
//        // giảm nhiễu xuống dòng
//        return sb.toString().replaceAll("(?m)(\\r?\\n){2,}", "\n").trim();
//    }
private String buildContentEmbedMobilePackage(MobilePackage mobilePackage) {
    long dataInMB = Long.parseLong(mobilePackage.getDataFree());
    long dataInGB = dataInMB / 1024;
    return "Mobile package code: " + mobilePackage.getCode() + "\n" +
            DataUtils.parseString(mobilePackage.getShortDesVi(), "") + "\n" +
            DataUtils.cleanHtml(mobilePackage.getFullDesVi()) + "\n" +
            "Price: " + mobilePackage.getMoneyFee() + " VNĐ" + "\n" +
            "Data: " + dataInGB + " GB" + "\n" +
            "Duration: " + mobilePackage.getExpireValue() + " " + mobilePackage.getExpireType() + "\n";
}

//private String buildContentEmbedFtthPackage(FtthPackage ftth) {
//    String code = DataUtils.parseString(ftth.getCode(), "").trim();
//
//    String groupName = DataUtils.parseString(ftth.getGroupName(), "").trim();
//
//    long price = DataUtils.parseLong(ftth.getPrice());
//    long promoPrice = DataUtils.parseLong(ftth.getPromotionPrice());
//
//    String shortVi = DataUtils.parseString(ftth.getShortDesVi(), "");
//
//    String promoVi = DataUtils.parseString(ftth.getPromotionVi(), "");
//
//    StringBuilder sb = new StringBuilder();
//    sb.append("FTTH PACKAGE\n");
//    sb.append("Code: ").append(code).append("\n");
//
//    sb.append("Type: fiber internet package; FTTH; home internet\n"); // keyword ngữ nghĩa
//    if (!groupName.isBlank()) sb.append("Group name: ").append(groupName).append("\n");
//
//
//    sb.append("Price: ").append(price).append(" (currency)\n");
//    if (promoPrice > 0) sb.append("Promotion price: ").append(promoPrice).append(" (currency)\n");
//
//    sb.append("Description (VI): ").append(shortVi).append("\n");
//
//    if (!promoVi.isBlank()) sb.append("Promotion (VI): ").append(promoVi).append("\n");
//
//    // cycle info (JSON -> text)
//    String cycleRaw = DataUtils.parseString(ftth.getCycle(), "").trim();
//    if (!cycleRaw.isBlank()) {
//        sb.append("Cycle options:\n");
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            List<Map<String, Object>> cycles = mapper.readValue(cycleRaw, new TypeReference<>() {});
//            for (Map<String, Object> c : cycles) {
//                String months = DataUtils.parseString(c.get("cycle"), "");
//                String cPrice = DataUtils.parseString(c.get("price"), "");
//                String discount = DataUtils.parseString(c.get("percent_discount"), "");
//                String bonus = DataUtils.parseString(c.get("bonus"), "");
//
//                sb.append("- ").append(months).append(" month(s)");
//                if (!cPrice.isBlank()) sb.append(", price ").append(cPrice).append("VNĐ");
//                if (!discount.isBlank()) sb.append(", discount ").append(discount).append("%");
//                if (!bonus.isBlank()) sb.append(", bonus ").append(bonus).append(" month(s)");
//                sb.append("\n");
//            }
//        } catch (Exception e) {
//            log.warn("FTTH cycle JSON parse failed for code {}: {}", code, e.getMessage());
//            sb.append("- cycle_raw: ").append(cycleRaw).append("\n");
//        }
//    }
//
//    // thêm câu “định dạng hỏi” để tăng recall
//    sb.append("User queries: ");
//    sb.append("\"gói ").append(code).append("\", ");
//    if (price > 0) sb.append("\"gói ").append(price).append("\", ");
//    sb.append("\"gói FTTH\", \"internet gia đình\"");
//    sb.append("\n");
//
//    return sb.toString().replaceAll("(?m)(\\r?\\n){2,}", "\n").trim();
//}
private String buildContentEmbedFtthPackage(FtthPackage ftthPackage) {
    String content = "FTTH package code: " + ftthPackage.getCode() + "\n" +
            DataUtils.parseString(ftthPackage.getShortDesVi(), "") + "\n" +
            DataUtils.parseString(ftthPackage.getPromotionVi(), "") + "\n" +
            "Group name: " + ftthPackage.getGroupName() + "\n" +
            "Speed network: " + ftthPackage.getSpeedInText() + "\n" +
            "Price: " + ftthPackage.getPrice() + " VNĐ" + "\n" +
            "Promotion price: " + ftthPackage.getPromotionPrice() + " VNĐ" + "\n";
    String cycle = ftthPackage.getCycle();
    StringBuilder cycleInfo = new StringBuilder("Cycle info:\n");
    try {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> cycles =
                mapper.readValue(cycle, new TypeReference<>() {
                });
        for (Map<String, Object> c : cycles) {
            String cycleMonths = DataUtils.parseString(c.get("cycle"), "");
            String price = DataUtils.parseString(c.get("price"), "");
            String percentDiscount = DataUtils.parseString(c.get("percent_discount"), "");
            String bonus = DataUtils.parseString(c.get("bonus"), "");
            cycleInfo.append("- ")
                    .append(cycleMonths)
                    .append(" month(s), ")
                    .append("price ")
                    .append(price)
                    .append(" VNĐ, ")
                    .append("discount ")
                    .append(percentDiscount).append("%")
                    .append(" bonus ")
                    .append(bonus).append(" month(s)");
            cycleInfo.append("\n");
        }
    } catch (Exception e) {
        log.error("Error parsing cycle info for FTTH with ID: {} - {}", ftthPackage.getCode(), e.getMessage());
        cycleInfo.append("[]");
    }
    return content + cycleInfo;
}

    private String buildContentEmbedSim(Sim sim) {
        return "Phone number: " + sim.getPhoneNumber() + "\n" +
                "Sim type: " + sim.getSimType() + "\n" +
                "Number type: " + sim.getNumberType() + "\n" +
                "Price: " + sim.getPrice() + " VNĐ" + "\n" +
                "Promotion price: " + sim.getPromotionPrice() + " VNĐ" + "\n" +
                DataUtils.parseString(sim.getDesVi(), "");
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
                                    "ids", Map.of("values", batch)
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

