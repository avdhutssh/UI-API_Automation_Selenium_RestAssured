package com.ecom.app.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;

import java.util.concurrent.TimeUnit;

public class ResponseSpecificationBuilder {
    private static final Logger log = LogManager.getLogger(ResponseSpecificationBuilder.class);

    public static ResponseSpecification getGenericResponseSpec() {
        log.info("Building generic response specification for ecom API");

        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(15000L), TimeUnit.MILLISECONDS)
                .build();
    }

    public static ResponseSpecification getGenericResponseNoContentSpec() {
        log.info("Building generic response specification for non-content responses");

        return new ResponseSpecBuilder()
                .expectResponseTime(Matchers.lessThan(15000L), TimeUnit.MILLISECONDS)
                .build();
    }

    public static ResponseSpecification getSuccessResponseSpec() {
        log.info("Building success response specification");

        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(15000L), TimeUnit.MILLISECONDS)
                .build();
    }

    public static ResponseSpecification getCreatedResponseSpec() {
        log.info("Building created response specification");

        return new ResponseSpecBuilder()
                .expectStatusCode(201)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(15000L), TimeUnit.MILLISECONDS)
                .build();
    }

    public static ResponseSpecification getLoginSuccessSpec() {
        log.info("Building login success response specification");

        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectBody("token", Matchers.notNullValue())
                .expectBody("userId", Matchers.notNullValue())
                .expectBody("message", Matchers.equalTo("Login Successfully"))
                .expectResponseTime(Matchers.lessThan(15000L), TimeUnit.MILLISECONDS)
                .build();
    }

    public static ResponseSpecification getLoginFailureSpec() {
        log.info("Building login failure response specification");

        return new ResponseSpecBuilder()
                .expectStatusCode(400)
                .expectContentType(ContentType.JSON)
                .expectBody("message", Matchers.notNullValue())
                .expectResponseTime(Matchers.lessThan(15000L), TimeUnit.MILLISECONDS)
                .build();
    }

    public static ResponseSpecification getOrderCreationSuccessSpec() {
        log.info("Building order creation success response specification");

        return new ResponseSpecBuilder()
                .expectStatusCode(201)
                .expectContentType(ContentType.JSON)
                .expectBody("orders", Matchers.notNullValue())
                .expectBody("message", Matchers.notNullValue())
                .expectResponseTime(Matchers.lessThan(15000L), TimeUnit.MILLISECONDS)
                .build();
    }

    public static ResponseSpecification getProductsListSuccessSpec() {
        log.info("Building products list success response specification");

        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectBody("data", Matchers.notNullValue())
                .expectResponseTime(Matchers.lessThan(15000L), TimeUnit.MILLISECONDS)
                .build();
    }

    public static ResponseSpecification getResponseSpecWithStatus(int statusCode) {
        log.info("Building response specification with status code: {}", statusCode);

        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(15000L), TimeUnit.MILLISECONDS)
                .build();
    }

    public static ResponseSpecification getDeleteSuccessSpec() {
        log.info("Building delete success response specification");

        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectBody("message", Matchers.notNullValue())
                .expectResponseTime(Matchers.lessThan(15000L), TimeUnit.MILLISECONDS)
                .build();
    }
} 