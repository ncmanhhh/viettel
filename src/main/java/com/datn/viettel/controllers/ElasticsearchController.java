package com.datn.viettel.controllers;

import com.datn.viettel.dto.common.ExecutionResultFactory;
import com.datn.viettel.dto.elasticsearch.MultiSearchQuery;
import com.datn.viettel.dto.elasticsearch.VectorSearchRequest;
import com.datn.viettel.services.iservice.ElasticsearchService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/elasticsearch")
@RequiredArgsConstructor
public class ElasticsearchController {

    private final ElasticsearchService elasticsearchService;

    // Kiểm tra nhanh tình trạng Elasticsearch
    @GetMapping("/health")
    public ResponseEntity<?> health(HttpServletRequest request) {
        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        Map.of("available", elasticsearchService.isElasticsearchAvailable()),
                        "SUCCESS",
                        "ELASTICSEARCH_HEALTH",
                        "Kiểm tra trạng thái Elasticsearch",
                        request.getRequestURI()
                )
        );
    }

    // Tạo index
    @PostMapping("/index/{index}")
    public ResponseEntity<?> createIndex(
            @PathVariable String index,
            @RequestBody(required = false) Map<String, Object> body,
            HttpServletRequest request
    ) {
        boolean ok = elasticsearchService.createIndex(index, body);

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        Map.of("created", ok),
                        "SUCCESS",
                        "CREATE_INDEX",
                        "Tạo index Elasticsearch",
                        request.getRequestURI()
                )
        );
    }

    // Xóa index
    @DeleteMapping("/index/{index}")
    public ResponseEntity<?> deleteIndex(
            @PathVariable String index,
            HttpServletRequest request
    ) {
        elasticsearchService.deleteIndex(index);

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        null,
                        "SUCCESS",
                        "DELETE_INDEX",
                        "Xóa index Elasticsearch",
                        request.getRequestURI()
                )
        );
    }

    // List document (match_all)
    @GetMapping("/{index}")
    public ResponseEntity<?> listDocuments(
            @PathVariable String index,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Map<String, Object> body = Map.of(
                "from", from,
                "size", size,
                "query", Map.of("match_all", Map.of())
        );

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        elasticsearchService.searchDocuments(index, body),
                        "SUCCESS",
                        "LIST_DOCUMENTS",
                        "Danh sách document",
                        request.getRequestURI()
                )
        );
    }

    // Get document by id
    @GetMapping("/{index}/{id}")
    public ResponseEntity<?> getDocument(
            @PathVariable String index,
            @PathVariable String id,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        elasticsearchService.getDocument(index, id),
                        "SUCCESS",
                        "GET_DOCUMENT",
                        "Lấy chi tiết document",
                        request.getRequestURI()
                )
        );
    }

    // Create document
    @PostMapping("/{index}/{id}")
    public ResponseEntity<?> createDocument(
            @PathVariable String index,
            @PathVariable String id,
            @RequestBody Map<String, Object> document,
            HttpServletRequest request
    ) {
        boolean ok = elasticsearchService.createDocument(index, id, document);

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        Map.of("created", ok),
                        "SUCCESS",
                        "CREATE_DOCUMENT",
                        "Tạo document",
                        request.getRequestURI()
                )
        );
    }

    // Update document
    @PutMapping("/{index}/{id}")
    public ResponseEntity<?> updateDocument(
            @PathVariable String index,
            @PathVariable String id,
            @RequestBody Map<String, Object> document,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        elasticsearchService.updateDocument(index, id, document),
                        "SUCCESS",
                        "UPDATE_DOCUMENT",
                        "Cập nhật document",
                        request.getRequestURI()
                )
        );
    }

    // Delete document by id
    @DeleteMapping("/{index}/{id}")
    public ResponseEntity<?> deleteDocument(
            @PathVariable String index,
            @PathVariable String id,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        elasticsearchService.deleteDocumentById(index, id),
                        "SUCCESS",
                        "DELETE_DOCUMENT",
                        "Xóa document",
                        request.getRequestURI()
                )
        );
    }

    // Delete multiple documents
    @DeleteMapping("/{index}/bulk")
    public ResponseEntity<?> deleteDocuments(
            @PathVariable String index,
            @RequestBody List<String> ids,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        elasticsearchService.deleteDocumentByIds(index, ids),
                        "SUCCESS",
                        "DELETE_DOCUMENTS",
                        "Xóa nhiều document",
                        request.getRequestURI()
                )
        );
    }

    // Search
    @PostMapping("/{index}/search")
    public ResponseEntity<?> search(
            @PathVariable String index,
            @RequestBody Map<String, Object> query,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        elasticsearchService.searchDocuments(index, query),
                        "SUCCESS",
                        "SEARCH_DOCUMENTS",
                        "Search document",
                        request.getRequestURI()
                )
        );
    }

    // Multi search
    @PostMapping("/multi-search")
    public ResponseEntity<?> multiSearch(
            @RequestBody List<MultiSearchQuery> queries,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        elasticsearchService.multiSearch(queries),
                        "SUCCESS",
                        "MULTI_SEARCH",
                        "Multi search Elasticsearch",
                        request.getRequestURI()
                )
        );
    }

    // Vector search
    @PostMapping("/{index}/vector-search")
    public ResponseEntity<?> vectorSearch(
            @PathVariable String index,
            @RequestBody VectorSearchRequest req,
            HttpServletRequest request
    ) {
        int topK = req.getTopK() != null ? req.getTopK() : 5;
        double minScore = req.getMinScore() != null ? req.getMinScore() : 0d;
        String field = req.getField() != null ? req.getField() : "contentVector";

        return ResponseEntity.ok(
                ExecutionResultFactory.success(
                        elasticsearchService.searchByVector(
                                index,
                                req.getQueryVector(),
                                topK,
                                minScore,
                                field,
                                req.getQuery()
                        ),
                        "SUCCESS",
                        "VECTOR_SEARCH",
                        "Vector search Elasticsearch",
                        request.getRequestURI()
                )
        );
    }
}
