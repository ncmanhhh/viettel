package com.datn.viettel.dto.elasticsearch;

import lombok.Data;

import java.util.Map;

@Data
public class VectorSearchRequest {
    private float[] queryVector;       // vector embedding của câu truy vấn
    private Integer topK;              // số kết quả muốn lấy
    private Double minScore;           // ngưỡng điểm tối thiểu (optional)
    private String field;              // tên field vector trong index (mặc định contentVector)
    private Map<String, Object> query; // query ES tùy chọn, nếu truyền sẽ bỏ qua script_score cosine
}

