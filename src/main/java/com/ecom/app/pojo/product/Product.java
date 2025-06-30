package com.ecom.app.pojo.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {
    
    @JsonProperty("_id")
    private String id;
    
    @JsonProperty("productName")
    private String productName;
    
    @JsonProperty("productAddedBy")
    private String productAddedBy;
    
    @JsonProperty("productCategory")
    private String productCategory;
    
    @JsonProperty("productSubCategory")
    private String productSubCategory;
    
    @JsonProperty("productPrice")
    private Integer productPrice;
    
    @JsonProperty("productDescription")
    private String productDescription;
    
    @JsonProperty("productImage")
    private String productImage;
    
    @JsonProperty("productFor")
    private String productFor;
    
    @JsonProperty("productAddedByUserId")
    private String productAddedByUserId;
    
    @JsonProperty("__v")
    private Integer version;
} 