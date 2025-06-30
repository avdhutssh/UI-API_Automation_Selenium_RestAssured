package com.ecom.app.Tests;

import com.ecom.app.BaseComponents.BaseTest;
import com.ecom.app.Utilities.CsvDataProviders;
import com.ecom.app.utils.RestClient;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class _01_Login_Tests extends BaseTest {

    @Test(priority = 1)
    public void test_01_UI_verifyValidLoginAndLogout() {
        logger.info("Starting test for valid login credentials");
        loginPage.loginWithDefaultCredentials();
        Assert.assertTrue(loginPage.isUserLoggedIn(), "User should be logged in with valid credentials");
        loginPage.signOutFromApplication();
        Assert.assertFalse(loginPage.isUserLoggedIn(), "User should be logged out after sign out");
        logger.info("Login successful with valid credentials and logout successful after sign out");
    }

    @Test(priority = 2)
    public void test_02_UI_verifyInvalidLogin() {
        logger.info("Starting test for invalid login credentials");
        loginPage.loginToApplication("invalidemail@gmail.com", "wrongpassword");
        String errorMessage = loginPage.getLoginErrorMessage();
        Assert.assertEquals(errorMessage, "Incorrect email or password.", "Expected error message for invalid login");
        logger.info("Login failed as expected with invalid credentials");
    }

    @Test(dataProvider = "csvFileReader", dataProviderClass = CsvDataProviders.class, priority = 3)
    public void test_03_API_verifyMultipleInvalidLoginAttempts(Map<String, String> testData) {
        String testScenario = testData.get("testScenario");
        String invalidEmail = testData.get("userEmail");
        String invalidPassword = testData.get("userPassword");
        int expectedStatusCode = Integer.parseInt(testData.get("statusCode"));
        String expectedErrorMessage = testData.get("errorMsg");

        logger.info("Starting API test for invalid login scenario: " + testScenario);

        Response response = RestClient.login(invalidEmail, invalidPassword);

        Assert.assertEquals(response.statusCode(), expectedStatusCode, "Expected status code for invalid login");
        Assert.assertEquals(response.jsonPath().getString("message"), expectedErrorMessage, "Expected error message for invalid login");
        Assert.assertTrue(response.statusLine().contains("Bad Request"), "Expected status line to contain 'Bad Request'");

        logger.info("API response validated for invalid login scenario: " + testScenario);
    }

    @Test(priority = 4)
    public void test_04_API_verifyValidLogin() {
        logger.info("Starting API test for valid login");

        Response response = RestClient.login(EMAIL, PASSWORD);

        Assert.assertEquals(response.statusCode(), 200, "Expected status code 200 for valid login");
        Assert.assertNotNull(response.jsonPath().getString("token"), "Token should be present in response");
        Assert.assertNotNull(response.jsonPath().getString("userId"), "User ID should be present in response");
        Assert.assertEquals(response.jsonPath().getString("message"), "Login Successfully", "Expected success message");

        logger.info("API login validation completed successfully");
    }
} 