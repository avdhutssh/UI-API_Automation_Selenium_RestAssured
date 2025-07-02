package com.ecom.app.pojo.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetails {

    @JsonProperty("_id")
    private String id;

    @JsonProperty("orderBy")
    private String orderBy;

    @JsonProperty("orderById")
    private String orderById;

    @JsonProperty("productOrderedId")
    private String productOrderedId;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("country")
    private String country;

    @JsonProperty("orderPrice")
    private String orderPrice;

    @JsonProperty("__v")
    private Integer version;
}