package com.datn.viettel.dto.elasticsearch;

import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class MultiSearchQuery {

    private final List<String> indices;
    private final Map<String, Object> body;

    public MultiSearchQuery(List<String> indices, Map<String, Object> body) {
        this.indices = indices;
        this.body = body;
    }

    public MultiSearchQuery(String index, Map<String, Object> body) {
        this.indices = (index == null || index.isBlank()) ? Collections.emptyList() : List.of(index);
        this.body = body;
    }

}