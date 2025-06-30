package com.ecom.app.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for Allure reporting with ecom API testing
 */
public class AllureReportUtils {
    private static final Logger log = LogManager.getLogger(AllureReportUtils.class);

    @Step("Log API test step: {stepDescription}")
    public static void logStep(String stepDescription) {
        log.info("Test Step: {}", stepDescription);
        Allure.step(stepDescription);
    }

    @Step("Log API request details")
    public static void logRequest(String endpoint, Object requestBody) {
        String requestDetails = String.format("Endpoint: %s\nRequest Body: %s", endpoint, requestBody);
        log.info("API Request - {}", requestDetails);
        Allure.addAttachment("API Request", "text/plain", requestDetails);
    }

    @Step("Log API response details")
    public static void logResponse(Response response) {
        String responseDetails = String.format(
                "Status Code: %d\nStatus Line: %s\nResponse Time: %d ms\nResponse Body: %s",
                response.getStatusCode(),
                response.getStatusLine(),
                response.getTime(),
                response.getBody().asString()
        );
        log.info("API Response - Status: {}, Time: {} ms", response.getStatusCode(), response.getTime());
        Allure.addAttachment("API Response", "application/json", responseDetails);
    }

    @Step("Log test assertion: {assertion}")
    public static void logAssertion(String assertion, boolean result) {
        String assertionDetails = String.format("Assertion: %s\nResult: %s", assertion, result ? "PASSED" : "FAILED");
        log.info("Assertion - {}: {}", assertion, result ? "PASSED" : "FAILED");
        Allure.addAttachment("Assertion Result", "text/plain", assertionDetails);
    }

    @Step("Log test data: {testDataName}")
    public static void logTestData(String testDataName, Object testData) {
        String testDataDetails = String.format("Test Data Name: %s\nData: %s", testDataName, testData.toString());
        log.info("Test Data - {}: {}", testDataName, testData);
        Allure.addAttachment("Test Data", "text/plain", testDataDetails);
    }

    @Step("Log error details")
    public static void logError(String errorMessage, Exception exception) {
        String errorDetails = String.format("Error: %s\nException: %s\nStack Trace: %s", 
                errorMessage, 
                exception.getClass().getSimpleName(),
                exception.getMessage());
        log.error("Test Error - {}", errorMessage, exception);
        Allure.addAttachment("Error Details", "text/plain", errorDetails);
    }
} 