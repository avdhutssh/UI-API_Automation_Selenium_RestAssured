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
public class OrderRequest {
    
    @JsonProperty("orders")
    private List<OrderItem> orders;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrderItem {
        
        @JsonProperty("country")
        private String country;
        
        @JsonProperty("productOrderedId")
        private String productOrderedId;
    }
} 