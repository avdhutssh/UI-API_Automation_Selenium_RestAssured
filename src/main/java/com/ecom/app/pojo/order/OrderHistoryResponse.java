package com.ecom.app.pojo.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderHistoryResponse {
    
    @JsonProperty("data")
    private List<OrderDetails> data;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("count")
    private Integer count;
} 