package com.datn.viettel.dto.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ElasticsearchResponse {
    @JsonProperty("_index")
    private String index;
    @JsonProperty("_id")
    private String id;
    @JsonProperty("_version")
    private int version;
    @JsonProperty("result")
    private String result;
}
