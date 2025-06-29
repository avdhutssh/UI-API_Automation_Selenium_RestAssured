package com.ecom.app;

import com.ecom.app.Utilities.CsvDataProviders;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.logging.Logger;

public class standAloneScripts {

    String BASE_URL = "https://rahulshettyacademy.com";
    String EMAIL = "av1234@gmail.com";
    String PASSWORD = "Bulbul@123";
    String PRODUCT_NAME = "ZARA COAT 3";
    String COUNTRY_NAME = "India";

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;
    TakesScreenshot ts;
    Logger logger;

    @BeforeMethod
    public void setup(Method method) {
        logger = Logger.getLogger(standAloneScripts.class.getName());
        String methodName = method.getName();
        if (methodName.contains("UI") || methodName.contains("web") || methodName.contains("e2e")) {
            logger.info("Setting up WebDriver for UI/e2e test: " + methodName);
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.get(BASE_URL + "/client");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        } else if (methodName.contains("API") || methodName.contains("Rest") || methodName.contains("Service")) {
            logger.info("Setting up REST Assured for API test: " + methodName);
            RestAssured.baseURI = BASE_URL;
        }
    }

    @AfterMethod
    public void tearDown(Method method) {
        String methodName = method.getName();
        if (methodName.contains("UI") || methodName.contains("web") || methodName.contains("e2e") && driver != null) {
            driver.quit();
            logger.info("Browser closed for UI test: " + methodName);
        } else if (methodName.contains("API")) {
            RestAssured.reset();
        }
    }

    @Test
    public void test_01_UI_verifyValidLogin() {
        logger.info("Starting test for valid login credentials.");
        loginToApplication(EMAIL, PASSWORD);
        WebElement signOutBtn = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//button[normalize-space(text())='Sign Out']"))));
        Assert.assertTrue(signOutBtn.isDisplayed());
        logger.info("Login successful with valid credentials.");
    }

    @Test
    public void test_02_UI_verifyInvalidLogin() {
        logger.info("Starting test for Invalid login credentials.");
        loginToApplication("invalidemail@gmail.com", "gjhjhkhkj");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[role='alert']")));
        String errorMessage = driver.findElement(By.cssSelector("[role='alert']")).getText();
        Assert.assertEquals(errorMessage, "Incorrect email or password.");
        logger.info("Login failed as expected with invalid credentials.");
    }

    @Test(dataProvider = "csvFileReader", dataProviderClass = CsvDataProviders.class)
    public void test_03_API_verifyMultipleInvalidLoginAttempts(Map<String, String> testData) {
        String testScenario = testData.get("testScenario");
        String invalidEmail = testData.get("userEmail");
        String invalidPassword = testData.get("userPassword");
        int statusCode = Integer.parseInt(testData.get("statusCode"));
        String errorMessage = testData.get("errorMsg");

        logger.info("Starting test for multiple invalid login attempts using API with scenario: " + testScenario);

        String requestBody = "{" +
                "\"userEmail\": \"" + invalidEmail + "\"," +
                "\"userPassword\": \"" + invalidPassword + "\"" +
                "}";
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .log().all()
                .when()
                .post("/api/ecom/auth/login")
                .then()
                .log().all()
                .extract().response();

        Assert.assertEquals(response.statusCode(), statusCode, "Expected status code 400 for invalid login.");
        Assert.assertEquals(response.jsonPath().getString("message"), errorMessage, "Expected error message for invalid login.");
        Assert.assertTrue(response.statusLine().contains("Bad Request"), "Expected status line to contain 'Bad Request'.");
        logger.info("API response validated for invalid login attempt: " + testScenario);
    }


    private void loginToApplication(String email, String password) {
        logger.info("Logging into the application.");
        driver.findElement(By.cssSelector("#userEmail")).sendKeys(email);
        driver.findElement(By.cssSelector("#userPassword")).sendKeys(password);
        driver.findElement(By.cssSelector("#login")).click();
    }
}
