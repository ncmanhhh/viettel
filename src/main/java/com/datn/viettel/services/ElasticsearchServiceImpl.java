package com.datn.viettel.services;

import com.datn.viettel.dto.elasticsearch.ElasticsearchResultMapper;
import com.datn.viettel.dto.elasticsearch.MultiSearchQuery;
import com.datn.viettel.services.iservice.ElasticsearchService;
import com.datn.viettel.utils.DataUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    private static final Header JSON = new BasicHeader("Content-Type", "application/json");
    private static final Header NDJSON = new BasicHeader("Content-Type", "application/x-ndjson");
    private final RestClient restClient; // Elasticsearch REST client
    private final ObjectMapper mapper = new ObjectMapper();

    private final ElasticsearchResultMapper resultMapper;



    @Autowired
    public ElasticsearchServiceImpl(
            @Qualifier("restClientElasticsearch") RestClient restClient,
            ElasticsearchResultMapper resultMapper) {
        this.restClient = restClient;
        this.resultMapper = resultMapper;
    }

    @NotNull
    private static Map<String, Object> getRequestBody(float[] queryVector, int topK, double minScore, String field, Map<String, Object> query) {
        if (!DataUtils.isNullOrEmpty(query)) {
            return Map.of(
                    "size", topK,
                    "track_total_hits", false,
                    "query", query,
                    "_source", List.of("id", "content", "contentFull")
            );
        } else {
            Map<String, Object> functionScoreQuery = getStringObjectMap(queryVector, field);
            return Map.of(
                    "size", topK,
                    "min_score", minScore,
                    "track_total_hits", false,
                    "query", functionScoreQuery,
                    "_source", List.of("id", "content", "contentFull")
            );
        }
    }

    @Override
    public boolean createIndex(String indexName, Map<String, Object> settingsAndMappings) {
        String path = "/" + indexName;
        String body = json(settingsAndMappings == null ? Map.of() : settingsAndMappings);
        try {
            Response resp = call("PUT", path, body, JSON);
            if (!is2xx(resp)) return false;
            JsonNode node = toJson(resp);
            return node.path("acknowledged").asBoolean(false)
                    || resp.getStatusLine().getStatusCode() == 200
                    || resp.getStatusLine().getStatusCode() == 201;
        } catch (IOException e) {
            log.error("Error creating index {}: {}", indexName, e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteIndex(String index) {
        String path = "/" + index;
        try {
            Response resp = call("DELETE", path, null, JSON);
            if (!is2xx(resp)) {
                String body = body(resp);
                throw new IllegalStateException("Failed to delete index: " + body);
            }
        } catch (IOException e) {
            log.error("Error deleting index {}: {}", index, e.getMessage());
            throw new IllegalStateException("Failed to delete index", e);
        }
    }

    @Override
    public boolean isElasticsearchAvailable() {
        try {
            Response resp = call("GET", "/_cluster/health", null, JSON);
            return is2xx(resp);
        } catch (IOException e) {
            log.warn("Elasticsearch not available: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, Object> getDocument(String indexName, String id) {
        String path = "/" + indexName + "/_doc/" + id;
        try {
            Response resp = call("GET", path, null, JSON);
            if (!is2xx(resp)) throw new IllegalStateException("Failed to get document: " + body(resp));
            // 1️⃣ Parse raw JSON
            Map<String, Object> rawResponse =
                    mapper.readValue(body(resp), new TypeReference<>() {});

            // 2️⃣ Map đúng loại response (GET DOCUMENT)
            return resultMapper.mapGetDocumentResult(rawResponse);
        } catch (IOException e) {
            log.error("Error getting document {}/{}: {}", indexName, id, e.getMessage());
            throw new IllegalStateException("Failed to get document", e);
        }
    }

    @Override
    public boolean createDocument(String indexName, String id, Map<String, Object> document) {
        String path = "/" + indexName + "/_create/" + id; //Tạo mới
//        String path = "/" + indexName + "/_doc/" + id; // Ghi đè + Tạo mới
        String payload = json(document);
        try {
            Response resp = call("PUT", path, payload, JSON);
            int code = resp.getStatusLine().getStatusCode();
            return code == HttpStatus.CREATED.value() || code == HttpStatus.OK.value();
        } catch (IOException e) {
            log.error("Error creating document {}/{}: {}", indexName, id, e.getMessage());
            return false;
        }
    }

    @Override
    public String updateDocument(String indexName, String id, Map<String, Object> updateFields) {
        String path = "/" + indexName + "/_update/" + id;
        String payload = json(Map.of("doc", updateFields));
        try {
            Response resp = call("POST", path, payload, JSON);
            if (!is2xx(resp)) throw new IllegalStateException("Failed to update document: " + body(resp));
            return body(resp);
        } catch (IOException e) {
            log.error("Error updating document {}/{}: {}", indexName, id, e.getMessage());
            throw new IllegalStateException("Failed to update document", e);
        }
    }

    @Override
    public String deleteDocumentById(String indexName, String id) {
        String path = "/" + indexName + "/_doc/" + id;
        try {
            Response resp = call("DELETE", path, null, JSON);
            if (!is2xx(resp)) throw new IllegalStateException("Failed to delete document: " + body(resp));
            return body(resp);
        } catch (IOException e) {
            log.error("Error deleting document {}/{}: {}", indexName, id, e.getMessage());
            throw new IllegalStateException("Failed to delete document", e);
        }
    }

    @Override
    public Map<String, Object> deleteDocumentByIds(String index, List<String> ids) {

        if (ids == null || ids.isEmpty()) {
            return Map.of(
                    "deleted", 0,
                    "message", "ID list is empty"
            );
        }

        String path = "/_bulk";

        try {
            // 1️⃣ Build NDJSON body
            StringBuilder bulkBody = new StringBuilder();
            for (String id : ids) {
                bulkBody.append("""
                { "delete": { "_index": "%s", "_id": "%s" } }
                """.formatted(index, id));
            }

            // 2️⃣ Gọi Elasticsearch
            Response resp = call("POST", path, bulkBody.toString(), NDJSON);

            if (!is2xx(resp)) {
                throw new IllegalStateException(
                        "Bulk delete failed: " + body(resp)
                );
            }

            // 3️⃣ Parse response
            Map<String, Object> rawResponse =
                    mapper.readValue(body(resp), new TypeReference<>() {});

            // 4️⃣ Đếm số document xóa thành công
            List<Map<String, Object>> items =
                    (List<Map<String, Object>>) rawResponse.get("items");

            long deletedCount = items.stream()
                    .filter(item -> {
                        Map<String, Object> delete =
                                (Map<String, Object>) item.get("delete");
                        return delete != null
                                && ((Number) delete.get("status")).intValue() == 200;
                    })
                    .count();

            return Map.of(
                    "deleted", deletedCount,
                    "requested", ids.size()
            );

        } catch (Exception e) {
            log.error("Bulk delete error", e);
            throw new IllegalStateException("Failed to bulk delete documents", e);
        }
    }


    // Tìm kiếm tài liệu với truy vấn tùy chỉnh
    @Override
    public Map<String, Object> searchDocuments(String indexName, Map<String, Object> query) {
        String path = "/" + indexName + "/_search";
        String payload = json(query);

        try {
            Response resp = call("POST", path, payload, JSON);
            if (!is2xx(resp)) {
                throw new IllegalStateException("Failed to search: " + body(resp));
            }

            // 1️⃣ Parse raw JSON
            Map<String, Object> rawResponse =
                    mapper.readValue(body(resp), new TypeReference<>() {});

            // 2️⃣ Map sang response sạch
            return resultMapper.mapSearchResult(rawResponse);

        } catch (IOException e) {
            log.error("Error searching documents in {}: {}", indexName, e.getMessage());
            throw new IllegalStateException("Failed to search documents", e);
        }
    }

    @Override
    public String multiSearch(List<MultiSearchQuery> queries) {
        if (queries == null || queries.isEmpty()) {
            throw new IllegalArgumentException("Queries must not be empty");
        }
        try {
            StringBuilder sb = new StringBuilder();
            for (MultiSearchQuery q : queries) {
                Map<String, Object> header = new LinkedHashMap<>();
                if (q.getIndices() != null && !q.getIndices().isEmpty()) {
                    header.put("index", String.join(",", q.getIndices()));
                }
                sb.append(mapper.writeValueAsString(header)).append("\n");
                sb.append(mapper.writeValueAsString(q.getBody() != null ? q.getBody() : Map.of())).append("\n");
            }
            String ndjson = sb.toString();
            Response resp = call("POST", "/_msearch", ndjson, NDJSON);
            if (!is2xx(resp)) throw new IllegalStateException("Failed to msearch: " + body(resp));
            return body(resp);
        } catch (IOException e) {
            log.error("Error multi-search: {}", e.getMessage());
            throw new IllegalStateException("Failed to multi-search", e);
        }
    }

    // Tìm kiếm vector sử dụng cosine similarity
    @Override
    public Object searchByVector(String indexName, float[] queryVector, int topK, double minScore, String field, Map<String, Object> query) {
        String path = "/" + indexName + "/_search";
        Map<String, Object> requestBody = getRequestBody(queryVector, topK, minScore, field, query);
        String payload = json(requestBody);
        try {
            Response resp = call("POST", path, payload, JSON);
            if (!is2xx(resp)) return null;
            return mapper.readValue(body(resp), Map.class);
        } catch (IOException e) {
            log.error("Error vector search in {}: {}", indexName, e.getMessage());
            throw new IllegalStateException("Failed to perform vector search", e);
        }
    }


    @NotNull
    private static Map<String, Object> getStringObjectMap(float[] queryVector, String field) {
        Map<String, Object> script = Map.of(
                "source", "cosineSimilarity(params.query_vector, '" + field + "') + 1.0",
                "params", Map.of("query_vector", queryVector)
        );

        Map<String, Object> scriptScoreFunction = Map.of(
                "script_score", Map.of("script", script)
        );

        return Map.of(
                "function_score", Map.of(
                        "query", Map.of("match_all", Map.of()),
                        "functions", List.of(scriptScoreFunction),
                        "boost_mode", "replace"
                )
        );
    }

    private Response call(String method, String path, String jsonBody, Header header) throws IOException {
        Request req = new Request(method, path);
        if (jsonBody != null) {
            req.setJsonEntity(jsonBody);
        }
//        req.setOptions(req.getOptions().toBuilder().addHeader(header.getName(), ElasticsearchServiceImpl.JSON.getValue()));
        req.setOptions(req.getOptions().toBuilder()
                .addHeader(header.getName(), header.getValue()));
        return restClient.performRequest(req);
    }

    private boolean is2xx(Response resp) {
        int code = resp.getStatusLine().getStatusCode();
        return code >= 200 && code < 300;
    }

    private String body(Response resp) throws IOException {
        try (InputStream is = resp.getEntity().getContent()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private JsonNode toJson(Response resp) throws IOException {
        return mapper.readTree(body(resp));
    }

    private String json(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization error", e);
        }
    }

    @Override
    public String deleteDocuments(String indexName, Map<String, Object> query) {
        String path = "/" + indexName + "/_delete_by_query";
        String payload = json(query);
        try {
            Response resp = call("POST", path, payload, JSON);
            if (!is2xx(resp)) {
                throw new IllegalStateException("Failed to delete documents: " + body(resp));
            }
            return body(resp);
        } catch (IOException e) {
            log.error("Error deleting documents from {}: {}", indexName, e.getMessage());
            throw new IllegalStateException("Failed to delete documents", e);
        }
    }
}
