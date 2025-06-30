package com.ecom.app.utils;

import com.ecom.app.constants.Endpoints;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;
import java.util.logging.Logger;

/**
 * REST Client utility for API operations
 */
public class RestClient {
    
    private static final Logger logger = Logger.getLogger(RestClient.class.getName());
    
    /**
     * Perform login and get authentication token
     */
    @Step("Login via API with email: {email}")
    public static Response login(String email, String password) {
        logger.info("Performing API login for: " + email);
        
        String requestBody = "{" +
                "\"userEmail\": \"" + email + "\"," +
                "\"userPassword\": \"" + password + "\"" +
                "}";
        
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .log().all()
                .when()
                .post(Endpoints.LOGIN)
                .then()
                .log().all()
                .extract().response();
        
        logger.info("Login API response status: " + response.statusCode());
        return response;
    }
    
    /**
     * Get all orders for a customer
     */
    @Step("Get orders for customer: {userId}")
    public static Response getOrdersForCustomer(String token, String userId) {
        logger.info("Getting orders for customer: " + userId);
        
        Response response = RestAssured.given()
                .header("Authorization", token)
                .header("Accept", "application/json, text/plain, */*")
                .pathParam("userId", userId)
                .log().all()
                .when()
                .get(Endpoints.GET_ORDERS_FOR_CUSTOMER)
                .then()
                .log().all()
                .extract().response();
        
        logger.info("Get orders API response status: " + response.statusCode());
        return response;
    }
    
    /**
     * Delete an order
     */
    @Step("Delete order: {orderId}")
    public static Response deleteOrder(String token, String orderId) {
        logger.info("Deleting order: " + orderId);
        
        Response response = RestAssured.given()
                .header("Authorization", token)
                .pathParam("orderId", orderId)
                .log().all()
                .when()
                .delete(Endpoints.DELETE_ORDER)
                .then()
                .log().all()
                .extract().response();
        
        logger.info("Delete order API response status: " + response.statusCode());
        return response;
    }
    
    /**
     * Create a new order
     */
    @Step("Create order with product: {productId} and country: {country}")
    public static Response createOrder(String token, String productId, String country) {
        logger.info("Creating order for product: " + productId + ", country: " + country);
        
        String requestBody = "{" +
                "\"orders\": [{" +
                "\"country\": \"" + country + "\"," +
                "\"productOrderedId\": \"" + productId + "\"" +
                "}]" +
                "}";
        
        Response response = RestAssured.given()
                .header("Authorization", token)
                .contentType("application/json")
                .body(requestBody)
                .log().all()
                .when()
                .post(Endpoints.CREATE_ORDER)
                .then()
                .log().all()
                .extract().response();
        
        logger.info("Create order API response status: " + response.statusCode());
        return response;
    }
    
    /**
     * Get all products
     */
    @Step("Get all products")
    public static Response getAllProducts() {
        logger.info("Getting all products");
        
        Response response = RestAssured.given()
                .log().all()
                .when()
                .get(Endpoints.GET_ALL_PRODUCTS)
                .then()
                .log().all()
                .extract().response();
        
        logger.info("Get products API response status: " + response.statusCode());
        return response;
    }
    
    /**
     * Generic GET request
     */
    @Step("GET request to: {endpoint}")
    public static Response get(String endpoint, Map<String, String> headers, Map<String, Object> queryParams) {
        RequestSpecification request = RestAssured.given();
        
        if (headers != null) {
            request.headers(headers);
        }
        
        if (queryParams != null) {
            request.queryParams(queryParams);
        }
        
        return request.log().all()
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .extract().response();
    }
    
    /**
     * Generic POST request
     */
    @Step("POST request to: {endpoint}")
    public static Response post(String endpoint, Object body, Map<String, String> headers) {
        RequestSpecification request = RestAssured.given()
                .contentType("application/json");
        
        if (headers != null) {
            request.headers(headers);
        }
        
        if (body != null) {
            request.body(body);
        }
        
        return request.log().all()
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
    }
    
    /**
     * Verify response status code
     */
    @Step("Verify response status code is: {expectedStatusCode}")
    public static void verifyStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.statusCode();
        logger.info("Expected status code: " + expectedStatusCode + ", Actual: " + actualStatusCode);
        AssertionUtils.assertEquals(actualStatusCode, expectedStatusCode, 
                "Status code mismatch");
    }
    
    /**
     * Verify response contains specific field
     */
    @Step("Verify response contains field: {fieldPath}")
    public static void verifyResponseContainsField(Response response, String fieldPath) {
        Object fieldValue = response.jsonPath().get(fieldPath);
        logger.info("Field '" + fieldPath + "' value: " + fieldValue);
        AssertionUtils.assertTrue(fieldValue != null, 
                "Response should contain field: " + fieldPath);
    }
} 