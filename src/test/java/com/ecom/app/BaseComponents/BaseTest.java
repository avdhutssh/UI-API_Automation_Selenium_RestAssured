package com.ecom.app.BaseComponents;

import com.ecom.app.PageObjects.*;
import com.ecom.app.Utilities.BrowserDriverFactory;
import com.ecom.app.Utilities.ConfigurationUtils;
import com.ecom.app.utils.RestClient;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BaseTest {

    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    protected WebDriver driver;

    protected LoginPage loginPage;
    protected ProductPage productPage;
    protected CartPage cartPage;
    protected CheckoutPage checkoutPage;
    protected OrderConfirmationPage orderConfirmationPage;

    protected final String BASE_URL = ConfigurationUtils.getInstance().getProperty("baseUrl");
    protected final String API_BASE_URL = ConfigurationUtils.getInstance().getProperty("baseApiUrl");
    protected final String EMAIL = ConfigurationUtils.getInstance().getProperty("email");
    protected final String PASSWORD = ConfigurationUtils.getInstance().getProperty("password");
    protected final String PRODUCT_NAME = ConfigurationUtils.getInstance().getProperty("productName", "ZARA COAT 3");
    protected final String COUNTRY_NAME = ConfigurationUtils.getInstance().getProperty("countryName", "India");

    protected String orderId;

    @BeforeSuite
    public void suiteSetup() {
        logger.info("=== Test Suite Setup Started ===");
        RestAssured.baseURI = API_BASE_URL;
        logger.info("=== Test Suite Setup Completed ===");
    }

    @BeforeMethod
    public void setup(Method method) {
        String methodName = method.getName();
        logger.info("=== Starting Test: " + methodName + " ===");

        if (methodName.contains("UI") || methodName.contains("web") || methodName.contains("e2e")) {
            logger.info("Setting up UI test: " + methodName);
            String browser = ConfigurationUtils.getInstance().getProperty("browser", "chrome");
            driver = BrowserDriverFactory.createDriver(browser, false);

            if (driver != null) {
                driver.get(BASE_URL + "/client");
                loginPage = new LoginPage(driver);
                productPage = new ProductPage(driver);
                cartPage = new CartPage(driver);
                checkoutPage = new CheckoutPage(driver);
                orderConfirmationPage = new OrderConfirmationPage(driver);
                logger.info("UI test setup completed for: " + methodName);
            } else {
                throw new RuntimeException("WebDriver initialization failed");
            }
        } else if (methodName.contains("API") || methodName.contains("api")) {
            logger.info("Setting up API test: " + methodName);
        }
    }

    @AfterMethod
    public void tearDown(Method method) {
        String methodName = method.getName();

        if ((methodName.contains("UI") || methodName.contains("web") || methodName.contains("e2e")) && driver != null) {
            BrowserDriverFactory.quitDriver();
            logger.info("WebDriver closed for: " + methodName);
        }

        logger.info("=== Completed Test: " + methodName + " ===");
    }

    @AfterSuite
    public void suiteTearDown() {
        logger.info("=== Test Suite Teardown Started ===");

        try {
            Response loginResponse = RestClient.login(EMAIL, PASSWORD);

            if (loginResponse.statusCode() == 200) {
                String apiToken = loginResponse.jsonPath().getString("token");
                String userId = loginResponse.jsonPath().getString("userId");

                Response ordersResponse = RestClient.getOrdersForCustomer(apiToken, userId);

                if (ordersResponse.statusCode() == 200) {
                    List<Map<String, Object>> orders = ordersResponse.jsonPath().getList("data");

                    if (orders != null && !orders.isEmpty()) {
                        logger.info("Found " + orders.size() + " orders to cleanup");

                        for (Map<String, Object> order : orders) {
                            String orderIdToDelete = (String) order.get("_id");
                            RestClient.deleteOrder(apiToken, orderIdToDelete);
                            logger.info("Deleted order: " + orderIdToDelete);
                        }
                    }
                }
            }

            logger.info("Test data cleanup completed");
        } catch (Exception e) {
            logger.warning("Error during test data cleanup: " + e.getMessage());
        }

        RestAssured.reset();
        logger.info("=== Test Suite Teardown Completed ===");
    }

    protected void setOrderId(String orderId) {
        this.orderId = orderId;
        logger.info("Order ID set: " + orderId);
    }

    protected String getOrderId() {
        return this.orderId;
    }
} 