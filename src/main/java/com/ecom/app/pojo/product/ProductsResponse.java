package com.ecom.app.pojo.product;

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
public class ProductsResponse {

    @JsonProperty("data")
    private List<Product> data;

    @JsonProperty("message")
    private String message;

    @JsonProperty("count")
    private Integer count;
}