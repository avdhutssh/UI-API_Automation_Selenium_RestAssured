package com.ecom.app.pojo.order;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {

    @JsonProperty("orders")
    private List<String> orders;

    @JsonProperty("productOrderId")
    private List<String> productOrderId;

    @JsonProperty("message")
    private String message;
}