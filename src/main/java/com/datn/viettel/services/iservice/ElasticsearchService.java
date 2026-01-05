package com.datn.viettel.services.iservice;

import com.datn.viettel.dto.elasticsearch.ElasticsearchResultMapper;
import com.datn.viettel.dto.elasticsearch.MultiSearchQuery;

import java.util.List;
import java.util.Map;

public interface ElasticsearchService {
    boolean createIndex(String indexName, Map<String, Object> settingsAndMappings);

    Map<String, Object> getDocument(String indexName, String id);

    boolean createDocument(String indexName, String id, Map<String, Object> document);

    String updateDocument(String indexName, String id, Map<String, Object> updateFields);

    String deleteDocumentById(String indexName, String id);

    Map<String, Object> deleteDocumentByIds(String index, List<String> ids);

    String deleteDocuments(String indexName, Map<String, Object> query);

    Map<String, Object> searchDocuments(String indexName, Map<String, Object> query);

    String multiSearch(List<MultiSearchQuery> queries);

    Object searchByVector(String indexName, float[] queryVector, int topK, double minScore, String field, Map<String, Object> query);

    boolean isElasticsearchAvailable();

    void deleteIndex(String code);

}
