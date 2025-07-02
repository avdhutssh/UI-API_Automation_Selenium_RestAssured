package com.ecom.app.BaseComponents;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.ecom.app.PageObjects.CartPage;
import com.ecom.app.PageObjects.CheckoutPage;
import com.ecom.app.PageObjects.LoginPage;
import com.ecom.app.PageObjects.OrderConfirmationPage;
import com.ecom.app.PageObjects.ProductPage;
import com.ecom.app.Utilities.BrowserDriverFactory;
import com.ecom.app.Utilities.ConfigurationUtils;
import com.ecom.app.generic.RequestFactory;
import com.ecom.app.utils.AllureReportUtils;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class BaseTest {

    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    protected WebDriver driver;

    protected LoginPage loginPage;
    protected ProductPage productPage;
    protected CartPage cartPage;
    protected CheckoutPage checkoutPage;
    protected OrderConfirmationPage orderConfirmationPage;

    protected RequestFactory requestFactory;
    protected String authToken;
    protected String userId;

    protected final String BASE_URL = ConfigurationUtils.getInstance().getProperty("baseUrl");
    protected final String API_BASE_URL = ConfigurationUtils.getInstance().getProperty("baseApiUrl");
    protected final String EMAIL = ConfigurationUtils.getInstance().getProperty("email");
    protected final String PASSWORD = ConfigurationUtils.getInstance().getProperty("password");
    protected final String PRODUCT_NAME = ConfigurationUtils.getInstance().getProperty("productName", "ZARA COAT 3");
    protected final String COUNTRY_NAME = ConfigurationUtils.getInstance().getProperty("countryName", "India");

    @BeforeSuite
    @Step("🚀 Test Suite Setup")
    public void suiteSetup() {
        logger.info("=== Test Suite Setup Started ===");
        AllureReportUtils.logStep("🚀 Initializing Test Suite");

        RestAssured.baseURI = API_BASE_URL;
        AllureReportUtils.logTestData("API Base URL", API_BASE_URL);

        setupEnhancedAPIComponents();

        logger.info("=== Test Suite Setup Completed ===");
        AllureReportUtils.logStep("✅ Test Suite Setup Completed Successfully");
    }

    @Step("🔐 Setting up API Authentication")
    private void setupEnhancedAPIComponents() {
        try {
            logger.info("Setting up enhanced API components");
            AllureReportUtils.logStep("Authenticating user for API access");

            RequestFactory tempFactory = new RequestFactory(null, null);
            Response loginResponse = tempFactory.login(EMAIL, PASSWORD);

            if (loginResponse.statusCode() == 200) {
                authToken = loginResponse.jsonPath().getString("token");
                userId = loginResponse.jsonPath().getString("userId");

                requestFactory = new RequestFactory(authToken, userId);

                logger.info("Enhanced API components setup completed");
                AllureReportUtils.logStep("✅ API authentication setup completed");
                AllureReportUtils.logTestData("User ID", userId);
                AllureReportUtils.logTestData("Authentication Status", "Success");
            } else {
                logger.warning("Failed to authenticate for API setup. Status: " + loginResponse.statusCode());
                AllureReportUtils.logStep("❌ API Authentication Failed - Status: " + loginResponse.statusCode());
                throw new RuntimeException("API authentication failed during suite setup");
            }
        } catch (Exception e) {
            logger.severe("Error setting up enhanced API components: " + e.getMessage());
            AllureReportUtils.logError("API Setup Error", e);
            throw new RuntimeException("Enhanced API setup failed", e);
        }
    }

    @BeforeMethod
    @Step("⚙️ Test Method Setup")
    public void setup(Method method) {
        String methodName = method.getName();
        logger.info("=== Starting Test: " + methodName + " ===");
        AllureReportUtils.logStep("⚙️ Setting up test: " + methodName);

        if (methodName.contains("UI") || methodName.contains("web") || methodName.contains("e2e")) {
            setupUITest(methodName);
        } else if (methodName.contains("API") || methodName.contains("api")) {
            setupAPITest(methodName);
        }
    }

    @Step("🌐 Setting up UI Test Environment")
    private void setupUITest(String methodName) {
        logger.info("Setting up UI test: " + methodName);
        AllureReportUtils.logStep("Initializing WebDriver for UI test");

        String browser = ConfigurationUtils.getInstance().getProperty("browser", "chrome");
        AllureReportUtils.logTestData("Browser", browser);

        driver = BrowserDriverFactory.createDriver(browser, false);

        if (driver != null) {
            AllureReportUtils.logStep("Navigating to application URL");
            driver.get(BASE_URL + "/client");
            AllureReportUtils.logTestData("Application URL", BASE_URL + "/client");

            loginPage = new LoginPage(driver);
            productPage = new ProductPage(driver);
            cartPage = new CartPage(driver);
            checkoutPage = new CheckoutPage(driver);
            orderConfirmationPage = new OrderConfirmationPage(driver);

            logger.info("UI test setup completed for: " + methodName);
            AllureReportUtils.logStep("✅ UI test environment ready");
        } else {
            AllureReportUtils.logStep("❌ WebDriver Initialization Failed - Could not create WebDriver instance");
            throw new RuntimeException("WebDriver initialization failed");
        }
    }

    @Step("🔌 Setting up API Test Environment")
    private void setupAPITest(String methodName) {
        logger.info("Setting up API test: " + methodName);
        AllureReportUtils.logStep("✅ API test environment ready - using existing authentication");
        AllureReportUtils.logTestData("Test Method", methodName);
    }

    @AfterMethod
    @Step("🧹 Test Method Cleanup")
    public void tearDown(Method method) {
        String methodName = method.getName();
        AllureReportUtils.logStep("🧹 Cleaning up after test: " + methodName);

        if ((methodName.contains("UI") || methodName.contains("web") || methodName.contains("e2e")) && driver != null) {
            AllureReportUtils.logStep("Closing WebDriver session");
            BrowserDriverFactory.quitDriver();
            logger.info("WebDriver closed for: " + methodName);
        }

        logger.info("=== Completed Test: " + methodName + " ===");
        AllureReportUtils.logStep("✅ Test cleanup completed");
    }

    @AfterSuite
    @Step("🏁 Test Suite Teardown")
    public void suiteTearDown() {
        logger.info("=== Test Suite Teardown Started ===");
        AllureReportUtils.logStep("🏁 Starting test suite cleanup");

        if (requestFactory != null) {
            AllureReportUtils.logStep("🗑️ Cleaning up test data");
            requestFactory.cleanupAllOrdersForCustomer();
        }

        AllureReportUtils.logStep("Resetting REST Assured configuration");
        RestAssured.reset();

        logger.info("=== Test Suite Teardown Completed ===");
        AllureReportUtils.logStep("✅ Test Suite Teardown Completed Successfully");
    }

    protected RequestFactory getRequestFactory() {
        return requestFactory;
    }

    protected String getUserId() {
        return userId;
    }
}