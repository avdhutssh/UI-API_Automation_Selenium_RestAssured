package com.ecom.app.generic;

import com.ecom.app.specs.RequestSpecificationBuilder;
import com.ecom.app.specs.ResponseSpecificationBuilder;
import com.ecom.app.utils.AllureReportUtils;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class RestClient {
    private static final Logger log = LogManager.getLogger(RestClient.class);

    private static ResponseSpecification respSpecGeneric = ResponseSpecificationBuilder.getGenericResponseSpec();
    private static ResponseSpecification respSpecNoContentGeneric = ResponseSpecificationBuilder.getGenericResponseNoContentSpec();

    private static RequestSpecification reqSpecAuth;

    @Step("Execute GET request on {endpoint} with path parameters and authorization token")
    public Response doGetRequest(String endpoint, Map<String, Object> pathParams, String authToken) {
        log.info("Executing GET request on endpoint: {} with path params: {} and token", endpoint, pathParams);
        reqSpecAuth = RequestSpecificationBuilder.getAuthenticatedRequestSpec(authToken);

        AllureReportUtils.logStep("GET request to: " + endpoint);
        AllureReportUtils.logRequest(endpoint, pathParams);

        Response response = RestAssured.given()
                .spec(reqSpecAuth)
                .pathParams(pathParams)
                .when()
                .get(endpoint)
                .then()
                .spec(respSpecGeneric)
                .extract()
                .response();

        AllureReportUtils.logResponse(response);
        return response;
    }

    @Step("Execute GET request on {endpoint} with authorization token")
    public Response doGetRequest(String endpoint, String authToken) {
        log.info("Executing GET request on endpoint: {} with token", endpoint);
        reqSpecAuth = RequestSpecificationBuilder.getAuthenticatedRequestSpec(authToken);

        AllureReportUtils.logStep("GET request to: " + endpoint);
        AllureReportUtils.logRequest(endpoint, null);

        Response response = RestAssured.given()
                .spec(reqSpecAuth)
                .when()
                .get(endpoint)
                .then()
                .spec(respSpecGeneric)
                .extract()
                .response();

        AllureReportUtils.logResponse(response);
        return response;
    }

    @Step("Execute POST request on {endpoint} with authorization token")
    public Response doPostRequest(String endpoint, Object requestBody, String authToken) {
        log.info("Executing POST request on endpoint: {} with token", endpoint);
        reqSpecAuth = RequestSpecificationBuilder.getAuthenticatedRequestSpec(authToken);

        AllureReportUtils.logStep("POST request to: " + endpoint);
        AllureReportUtils.logRequest(endpoint, requestBody);

        Response response = RestAssured.given()
                .spec(reqSpecAuth)
                .body(requestBody)
                .when()
                .post(endpoint)
                .then()
                .spec(respSpecGeneric)
                .extract()
                .response();

        AllureReportUtils.logResponse(response);
        return response;
    }

    @Step("Execute POST request on {endpoint} without authorization")
    public Response doPostRequest(String endpoint, Object requestBody) {
        log.info("Executing POST request on endpoint: {} without token", endpoint);
        RequestSpecification reqSpec = RequestSpecificationBuilder.getLoginRequestSpec();

        AllureReportUtils.logStep("POST request to: " + endpoint + " (no auth)");
        AllureReportUtils.logRequest(endpoint, requestBody);

        Response response = RestAssured.given()
                .spec(reqSpec)
                .body(requestBody)
                .when()
                .post(endpoint)
                .then()
                .spec(respSpecGeneric)
                .extract()
                .response();

        AllureReportUtils.logResponse(response);
        return response;
    }

    @Step("Execute POST request on {endpoint} with path parameters and authorization token")
    public Response doPostRequest(String endpoint, Object requestBody, Map<String, Object> pathParams, String authToken) {
        log.info("Executing POST request on endpoint: {} with path params: {} and token", endpoint, pathParams);
        reqSpecAuth = RequestSpecificationBuilder.getAuthenticatedRequestSpec(authToken);

        AllureReportUtils.logStep("POST request to: " + endpoint + " with path params: " + pathParams);
        AllureReportUtils.logRequest(endpoint, requestBody);

        Response response = RestAssured.given()
                .spec(reqSpecAuth)
                .body(requestBody)
                .pathParams(pathParams)
                .when()
                .post(endpoint)
                .then()
                .spec(respSpecGeneric)
                .extract()
                .response();

        AllureReportUtils.logResponse(response);
        return response;
    }

    @Step("Execute DELETE request on {endpoint} with path parameters and authorization token")
    public Response doDeleteRequest(String endpoint, Map<String, Object> pathParams, String authToken) {
        log.info("Executing DELETE request on endpoint: {} with path params: {} and token", endpoint, pathParams);
        reqSpecAuth = RequestSpecificationBuilder.getAuthenticatedRequestSpec(authToken);

        AllureReportUtils.logStep("DELETE request to: " + endpoint + " with path params: " + pathParams);
        AllureReportUtils.logRequest(endpoint, pathParams);

        Response response = RestAssured.given()
                .spec(reqSpecAuth)
                .pathParams(pathParams)
                .when()
                .delete(endpoint)
                .then()
                .spec(respSpecGeneric)
                .extract()
                .response();

        AllureReportUtils.logResponse(response);
        return response;
    }

    @Step("Execute DELETE request on {endpoint} with authorization token")
    public Response doDeleteRequest(String endpoint, String authToken) {
        log.info("Executing DELETE request on endpoint: {} with token", endpoint);
        reqSpecAuth = RequestSpecificationBuilder.getAuthenticatedRequestSpec(authToken);

        AllureReportUtils.logStep("DELETE request to: " + endpoint);
        AllureReportUtils.logRequest(endpoint, null);

        Response response = RestAssured.given()
                .spec(reqSpecAuth)
                .when()
                .delete(endpoint)
                .then()
                .spec(respSpecGeneric)
                .extract()
                .response();

        AllureReportUtils.logResponse(response);
        return response;
    }
} 