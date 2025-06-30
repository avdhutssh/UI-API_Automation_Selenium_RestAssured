package com.ecom.app.pojo.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRequest {
    
    @JsonProperty("userEmail")
    private String userEmail;
    
    @JsonProperty("userPassword")
    private String userPassword;
} 