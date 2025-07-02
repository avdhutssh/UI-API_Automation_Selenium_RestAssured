package com.ecom.app.Tests;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ecom.app.BaseComponents.BaseTest;
import com.ecom.app.Utilities.CsvDataProviders;
import com.ecom.app.constants.StatusCode;
import com.ecom.app.utils.AllureReportUtils;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;

@Epic("Authentication Tests")
@Feature("Login Functionality")
public class _01_Login_Tests extends BaseTest {

    @Test(priority = 1, groups = {"smoke"})
    @Story("Valid UI Login")
    @Description("Test successful UI login and logout flow")
    @Severity(SeverityLevel.CRITICAL)
    public void test_01_UI_verifyValidLoginAndLogout() {
        AllureReportUtils.logStep("Testing valid UI login and logout flow");
        loginPage.loginWithDefaultCredentials();
        Assert.assertTrue(loginPage.isUserLoggedIn(), "User should be logged in with valid credentials");
        loginPage.signOutFromApplication();
        Assert.assertFalse(loginPage.isUserLoggedIn(), "User should be logged out after sign out");
    }

    @Test(priority = 2, groups = {"smoke"})
    @Story("Invalid UI Login")
    @Description("Test UI login failure with invalid credentials")
    @Severity(SeverityLevel.NORMAL)
    public void test_02_UI_verifyInvalidLogin() {
        AllureReportUtils.logStep("Testing invalid UI login");
        loginPage.loginToApplication("invalidemail@gmail.com", "wrongpassword");
        String errorMessage = loginPage.getLoginErrorMessage();
        Assert.assertEquals(errorMessage, "Incorrect email or password.", "Expected error message for invalid login");
        AllureReportUtils.logTestData("Error Message", errorMessage);
    }

    @Test(dataProvider = "csvFileReader", dataProviderClass = CsvDataProviders.class, priority = 3, groups = {"regression"})
    @Story("Data-driven API Login")
    @Description("Test API login failure with multiple invalid credential combinations")
    @Severity(SeverityLevel.NORMAL)
    public void test_03_API_verifyMultipleInvalidLoginAttempts(Map<String, String> testData) {
        String testScenario = testData.get("testScenario");
        String invalidEmail = testData.get("userEmail");
        String invalidPassword = testData.get("userPassword");
        int expectedStatusCode = Integer.parseInt(testData.get("statusCode"));
        String expectedErrorMessage = testData.get("errorMsg");

        AllureReportUtils.logStep("Testing API login scenario: " + testScenario);
        AllureReportUtils.logTestData("Test Email", invalidEmail);

        Response response = getRequestFactory().login(invalidEmail, invalidPassword);

        Assert.assertEquals(response.statusCode(), expectedStatusCode, "Expected status code for invalid login");
        Assert.assertEquals(response.jsonPath().getString("message"), expectedErrorMessage, "Expected error message for invalid login");
        Assert.assertTrue(response.statusLine().contains("Bad Request"), "Expected status line to contain 'Bad Request'");
    }

    @Test(priority = 4, groups = {"regression"})
    @Story("Valid API Login")
    @Description("Test successful API login and response validation")
    @Severity(SeverityLevel.CRITICAL)
    public void test_04_API_verifyValidLogin() {
        AllureReportUtils.logStep("Testing valid API login");

        Response response = getRequestFactory().login(EMAIL, PASSWORD);

        Assert.assertEquals(response.statusCode(), StatusCode.OK.getCode(), "Expected status code 200 for valid login");
        Assert.assertNotNull(response.jsonPath().getString("token"), "Token should be present in response");
        Assert.assertNotNull(response.jsonPath().getString("userId"), "User ID should be present in response");
        Assert.assertEquals(response.jsonPath().getString("message"), "Login Successfully", "Expected success message");
    }

    @Test(priority = 5, groups = {"regression"})
    @Story("API Response Time")
    @Description("Test API login response time validation")
    @Severity(SeverityLevel.MINOR)
    public void test_05_API_verifyLoginResponseTime() {
        AllureReportUtils.logStep("Testing API login response time");
        Response response = getRequestFactory().login(EMAIL, PASSWORD);
        Assert.assertTrue(response.time() < 5000, "API login response time should be less than 5 seconds");
        Assert.assertEquals(response.statusCode(), StatusCode.OK.getCode(), "Login should be successful");
    }
}
