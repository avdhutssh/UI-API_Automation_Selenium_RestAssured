package com.ecom.app.specs;

import com.ecom.app.utils.AllureRestAssuredFilter;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestSpecificationBuilder {
    private static final Logger log = LogManager.getLogger(RequestSpecificationBuilder.class);

    static {
        // Set base URI for all requests
        RestAssured.baseURI = "https://rahulshettyacademy.com/api/ecom";
    }

    public static RequestSpecification getDefaultRequestSpec() {
        log.info("Building default request specification for ecom API");

        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addFilter(AllureRestAssuredFilter.getInstance())
                .log(LogDetail.ALL)
                .build();
    }

    public static RequestSpecification getAuthenticatedRequestSpec(String authToken) {
        log.info("Building authenticated request specification for ecom API");

        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", authToken)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();
    }

    public static RequestSpecification getLoginRequestSpec() {
        log.info("Building login request specification");

        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();
    }

    public static RequestSpecification getMultipartRequestSpec(String authToken) {
        log.info("Building multipart request specification for file uploads");

        return new RequestSpecBuilder()
                .setContentType(ContentType.MULTIPART)
                .addHeader("Authorization", authToken)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();
    }

} 