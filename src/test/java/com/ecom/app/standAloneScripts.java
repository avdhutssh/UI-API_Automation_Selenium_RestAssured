package com.ecom.app;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.annotations.Ignore;

import com.ecom.app.Utilities.CsvDataProviders;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@Ignore("Standalone scripts - temporarily disabled")
public class standAloneScripts {

    String BASE_URL = "https://rahulshettyacademy.com";
    String EMAIL = "av1234@gmail.com";
    String PASSWORD = "Bulbul@123";
    String PRODUCT_NAME = "ZARA COAT 3";
    String COUNTRY_NAME = "India";
    String orderId;

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;
    Actions act;
    TakesScreenshot ts;
    Logger logger;

    @AfterSuite
    public void suiteCleanup() {
        logger.info("Starting test suite cleanup - deleting all orders created during test execution");
        try {
            // Login to get token and userId
            String requestBody = "{" +
                    "\"userEmail\": \"" + EMAIL + "\"," +
                    "\"userPassword\": \"" + PASSWORD + "\"" +
                    "}";

            Response loginResponse = RestAssured.given()
                    .contentType("application/json")
                    .body(requestBody)
                    .when()
                    .post("/api/ecom/auth/login")
                    .then()
                    .extract().response();

            if (loginResponse.statusCode() != 200) {
                logger.warning("Failed to login for cleanup. Status code: " + loginResponse.statusCode());
                return;
            }

            String apiToken = loginResponse.jsonPath().getString("token");
            String userId = loginResponse.jsonPath().getString("userId");
            logger.info("Successfully logged in for cleanup. User ID: " + userId);

            // Get all orders for the customer
            Response ordersResponse = RestAssured.given()
                    .header("Authorization", apiToken)
                    .header("Accept", "application/json, text/plain, */*")
                    .pathParam("userId", userId)
                    .when()
                    .get("/api/ecom/order/get-orders-for-customer/{userId}")
                    .then()
                    .extract().response();

            if (ordersResponse.statusCode() != 200) {
                logger.warning("Failed to fetch orders for cleanup. Status code: " + ordersResponse.statusCode());
                return;
            }

            List<Map<String, Object>> orders = ordersResponse.jsonPath().getList("data");
            if (orders == null || orders.isEmpty()) {
                logger.info("No orders found to cleanup");
                return;
            }

            logger.info("Found " + orders.size() + " orders to cleanup");

            // Delete each order
            int deletedCount = 0;
            for (Map<String, Object> order : orders) {
                String currentOrderId = (String) order.get("_id");
                try {
                    Response deleteResponse = RestAssured.given()
                            .header("Authorization", apiToken)
                            .pathParam("orderId", currentOrderId)
                            .when()
                            .delete("/api/ecom/order/delete-order/{orderId}")
                            .then()
                            .extract().response();

                    if (deleteResponse.statusCode() == 200) {
                        String message = deleteResponse.jsonPath().getString("message");
                        if ("Orders Deleted Successfully".equals(message)) {
                            logger.info("✅ Successfully deleted order: " + currentOrderId);
                            deletedCount++;
                        } else {
                            logger.warning("⚠️ Unexpected response for order deletion: " + currentOrderId + " - " + message);
                        }
                    } else {
                        logger.warning("❌ Failed to delete order: " + currentOrderId + " - Status: " + deleteResponse.statusCode());
                    }
                } catch (Exception e) {
                    logger.warning("❌ Exception while deleting order: " + currentOrderId + " - " + e.getMessage());
                }
            }

            logger.info("Cleanup completed. Successfully deleted " + deletedCount + " out of " + orders.size() + " orders");

        } catch (Exception e) {
            logger.warning("Failed to perform suite cleanup: " + e.getMessage());
        } finally {
            RestAssured.reset();
            logger.info("REST Assured configuration reset");
        }
    }

    @BeforeMethod
    public void setup(Method method) {
        logger = Logger.getLogger(standAloneScripts.class.getName());
        String methodName = method.getName();
        if (methodName.contains("UI") || methodName.contains("web") || methodName.contains("e2e")) {
            logger.info("Setting up WebDriver for UI/e2e test: " + methodName);

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-save-password-bubble");
            options.addArguments("--disable-autofill");
            options.addArguments("--disable-autofill-keyboard-accessory-view");
            options.addArguments("--disable-full-form-autofill-ios");
            options.addArguments("--disable-payments-autofill");
            options.addArguments("--disable-web-security");
            options.addArguments("--disable-features=VizDisplayCompositor");
            options.addArguments("--disable-popup-blocking");
            options.setExperimentalOption("prefs", Collections.singletonMap("autofill.credit_card_enabled", false));

            driver = new ChromeDriver(options);
            driver.manage().window().maximize();
            driver.get(BASE_URL + "/client");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            js = (JavascriptExecutor) driver;
            act = new Actions(driver);
        } else if (methodName.contains("API") || methodName.contains("Rest") || methodName.contains("Service")) {
            logger.info("Setting up REST Assured base URI for the test suite");
            RestAssured.baseURI = BASE_URL;
        }
    }

    @AfterMethod
    public void tearDown(Method method) {
        String methodName = method.getName();
        if (methodName.contains("UI") || methodName.contains("web") || methodName.contains("e2e") && driver != null) {
            driver.quit();
            logger.info("Browser closed for UI test: " + methodName);
        }
    }

    @Test(priority = 1, enabled = false)
    public void test_01_UI_verifyValidLogin() {
        logger.info("Starting test for valid login credentials.");
        loginToApplication(EMAIL, PASSWORD);
        WebElement signOutBtn = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//button[normalize-space(text())='Sign Out']"))));
        Assert.assertTrue(signOutBtn.isDisplayed());
        logger.info("Login successful with valid credentials.");
    }

    @Test(priority = 2, enabled = false)
    public void test_02_UI_verifyInvalidLogin() {
        logger.info("Starting test for Invalid login credentials.");
        loginToApplication("invalidemail@gmail.com", "gjhjhkhkj");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[role='alert']")));
        String errorMessage = driver.findElement(By.cssSelector("[role='alert']")).getText();
        Assert.assertEquals(errorMessage, "Incorrect email or password.");
        logger.info("Login failed as expected with invalid credentials.");
    }

    @Test(dataProvider = "csvFileReader", dataProviderClass = CsvDataProviders.class, priority = 3, enabled = false)
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

    @Test(priority = 4)
    public void test_04_UI_searchProductAndVerify() {
        logger.info("Starting test to search for a product and verify its presence.");
        loginToApplication(EMAIL, PASSWORD);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@name='search'])[2]"))).sendKeys(PRODUCT_NAME);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@name='search'])[2]"))).sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".card-img-top")));
        Assert.assertTrue(driver.findElement(By.xpath("//*[contains(text(),'ZARA')]")).isDisplayed());
        logger.info("Product search and verification completed successfully.");
    }

    //TODO: Add Multiple product search and add combinations and verify count number, multiple product in cart
    @Test(priority = 5)
    public void test_05_UI_verifyAddedItemsArePresentInCart() throws InterruptedException {
        logger.info("Starting test to verify added items are present in the cart.");
        loginToApplication(EMAIL, PASSWORD);
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[normalize-space(text())='" + PRODUCT_NAME + "']/../following-sibling::button[normalize-space(text())='Add To Cart']")));
        js.executeScript("arguments[0].scrollIntoView(true);", addToCartBtn);
        js.executeScript("window.scrollBy(0, 500);");
        try {
            addToCartBtn.click();
        } catch (Exception e) {
            jsClick(addToCartBtn);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[aria-label='Product Added To Cart']")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[aria-label='Product Added To Cart']")));
        js.executeScript("window.scrollBy(0, -1500);");
        Thread.sleep(500);
        WebElement cartBtn = driver.findElement(By.cssSelector("[routerlink*='cart']"));
        Assert.assertEquals(cartBtn.findElement(By.cssSelector("label")).getText(), "1");
        cartBtn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[contains(text(),'" + PRODUCT_NAME + "')]")));
        Assert.assertTrue(driver.findElement(By.xpath("//h3[contains(text(),'" + PRODUCT_NAME + "')]")).isDisplayed(),
                "Product " + PRODUCT_NAME + " is not present in the cart.");
        int productCount = driver.findElements(By.xpath("//h3[contains(text(),'" + PRODUCT_NAME + "')]")).size();
        Assert.assertTrue(productCount == 1, "No products found in the cart.");
        logger.info("Items in the cart verified successfully.");
    }

    @Test(priority = 6)
    public void test_06_UI_verifyRemoveCartItem() throws InterruptedException {
        logger.info("Starting test to verify removal of cart item.");
        loginToApplication(EMAIL, PASSWORD);
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[normalize-space(text())='" + PRODUCT_NAME + "']/../following-sibling::button[normalize-space(text())='Add To Cart']")));
        js.executeScript("arguments[0].scrollIntoView(true);", addToCartBtn);
        js.executeScript("window.scrollBy(0, -1500);");
        Thread.sleep(1000);
        try {
            addToCartBtn.click();
        } catch (Exception e) {
            jsClick(addToCartBtn);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[aria-label='Product Added To Cart']")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[aria-label='Product Added To Cart']")));
        js.executeScript("window.scrollBy(0, -500);");
        WebElement cartBtn = driver.findElement(By.cssSelector("[routerlink*='cart']"));
        Assert.assertEquals(cartBtn.findElement(By.cssSelector("label")).getText(), "1");
        cartBtn.click();
        List<WebElement> items = driver.findElements(By.xpath("//h3[contains(text(),'" + PRODUCT_NAME + "')]"));
        wait.until(ExpectedConditions.visibilityOf(items.get(0)));
        Assert.assertTrue(items.get(0).isDisplayed(),
                "Product " + PRODUCT_NAME + " is not present in the cart.");
        WebElement removeBtn = driver.findElement(By.xpath("//button[contains(@class,'btn btn-danger')]"));
        removeBtn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@aria-label='No Product in Your Cart']")));
        Assert.assertTrue(driver.findElement(By.xpath("//*[@aria-label='No Product in Your Cart']")).isDisplayed(),
                "Product " + PRODUCT_NAME + " is still present in the cart after removal.");
        wait.until(ExpectedConditions.invisibilityOf(items.get(0)));
        Assert.assertEquals(driver.findElements(By.xpath("//h3[contains(text(),'" + PRODUCT_NAME + "')]")).size(), 0,
                "Product " + PRODUCT_NAME + " is still present in the cart after removal.");

        logger.info("Cart item removal verified successfully.");
    }

    @Test(priority = 7)
    public void test_07_e2e_verifyCompleteOrderFlow() throws InterruptedException {
        logger.info("Starting complete E2E order flow test.");
        loginToApplication(EMAIL, PASSWORD);
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@name='search'])[2]")));
        searchBox.sendKeys(PRODUCT_NAME);
        searchBox.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".card-img-top")));
        Assert.assertTrue(driver.findElement(By.xpath("//*[contains(text(),'ZARA')]")).isDisplayed());
        Thread.sleep(500);
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[normalize-space(text())='" + PRODUCT_NAME + "']/../following-sibling::button[normalize-space(text())='Add To Cart']")));
        addToCartBtn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[aria-label='Product Added To Cart']")));
        WebElement cartBtn = driver.findElement(By.cssSelector("[routerlink*='cart']"));
        cartBtn.click();
        Thread.sleep(500);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[contains(text(),'" + PRODUCT_NAME + "')]")));
        Assert.assertTrue(driver.findElement(By.xpath("//h3[contains(text(),'" + PRODUCT_NAME + "')]")).isDisplayed(),
                "Product " + PRODUCT_NAME + " should be present in cart");
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        WebElement checkoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Checkout')]")));
        checkoutBtn.click();
        Thread.sleep(500);
        WebElement countryField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[placeholder*='Country']")));
        for (char c : COUNTRY_NAME.toCharArray()) {
            countryField.sendKeys(String.valueOf(c));
            Thread.sleep(50);
        }
        WebElement countryOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[normalize-space(text())='" + COUNTRY_NAME + "']")));
        countryOption.click();
        WebElement cardNumberField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//input[@type='text'])[1]")));
        cardNumberField.clear();
        cardNumberField.sendKeys("4242424242424242");
        WebElement cvvField = driver.findElement(By.xpath("(//input[@type='text'])[2]"));
        cvvField.clear();
        cvvField.sendKeys("123");

        WebElement nameField = driver.findElement(By.xpath("(//input[@type='text'])[3]"));
        nameField.clear();
        nameField.sendKeys("Test User");
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(500);
        WebElement placeOrderBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[normalize-space(text())='Place Order']")));

        placeOrderBtn.click();
        Thread.sleep(500);
        WebElement confirmationElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.tagName("h1")));
        Assert.assertEquals(confirmationElement.getText().trim(), "THANKYOU FOR THE ORDER.", "Order confirmation message should be displayed");

        String orderText = driver.findElement(By.xpath("//label[@class='ng-star-inserted']")).getText().trim();
        orderId = orderText.replaceAll("[^a-zA-Z0-9]", "");
        logger.info("Order ID: " + orderId);
        js.executeScript("window.scrollTo(0, -500);");
        WebElement orderBtn = driver.findElement(By.xpath("//*[normalize-space(text())='Orders History Page']"));
        orderBtn.click();
        Thread.sleep(500);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Your Orders')]")));
        List<WebElement> orderList = driver.findElements(By.xpath("//th[@scope='row']"));
        boolean orderFound = false;
        for (WebElement order : orderList) {
            System.out.println("Order ID in history: " + order.getText());
            if (order.getText().equals(orderId)) {
                orderFound = true;
                break;
            }
        }
        Assert.assertTrue(orderFound, "Order ID " + orderId + " should be present in the order history.");
        logger.info("E2E Complete Order Flow Test Completed Successfully!");
    }

    @Test(dependsOnMethods = "test_07_e2e_verifyCompleteOrderFlow", priority = 8)
    public void test_08_API_verifyOrderHistory() {
        logger.info("Starting API test to verify order history.");
        if (orderId == null || orderId.isEmpty()) {
            logger.warning("Order ID is not set. Skipping order history verification.");
            return;
        }

        String requestBody = "{" +
                "\"userEmail\": \"" + EMAIL + "\"," +
                "\"userPassword\": \"" + PASSWORD + "\"" +
                "}";
        Response loginResponse = RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .log().all()
                .when()
                .post("/api/ecom/auth/login")
                .then()
                .log().all()
                .extract().response();

        String apiToken = loginResponse.jsonPath().getString("token");
        String userId = loginResponse.jsonPath().getString("userId");
        logger.info("API Token obtained for order verification");
        logger.info("User ID: " + userId);

        Response response = RestAssured.given()
                .header("Authorization", apiToken)
                .header("Accept", "application/json, text/plain, */*")
                .pathParam("userId", userId)
                .log().all()
                .when()
                .get("/api/ecom/order/get-orders-for-customer/{userId}")
                .then()
                .log().all()
                .extract().response();

        Assert.assertEquals(response.jsonPath().getString("message"), "Orders fetched for customer Successfully");

        List<Map<String, Object>> orders = response.jsonPath().getList("data");
        Assert.assertTrue(orders.size() > 0, "Customer should have at least one order");
        Map<String, Object> targetOrder = null;
        boolean orderFound = false;

        for (Map<String, Object> order : orders) {
            String currentOrderId = (String) order.get("_id");
            logger.info("Checking order ID: " + currentOrderId);

            if (currentOrderId.equals(orderId)) {
                targetOrder = order;
                orderFound = true;
                logger.info("✅ Found matching order: " + orderId);
                break;
            }
        }

        Assert.assertTrue(orderFound, "Order ID " + orderId + " should be present in the order history.");
        Assert.assertNotNull(targetOrder, "Target order should not be null");

        String actualOrderBy = (String) targetOrder.get("orderBy");
        String actualProductName = (String) targetOrder.get("productName");
        String actualCountry = (String) targetOrder.get("country");
        String actualOrderById = (String) targetOrder.get("orderById");

        Assert.assertEquals(actualOrderBy, EMAIL, "Order should be placed by the correct user");
        Assert.assertEquals(actualProductName, PRODUCT_NAME, "Product name should match");
        Assert.assertEquals(actualCountry, COUNTRY_NAME, "Country should match");
        Assert.assertEquals(actualOrderById, userId, "Order should be placed by the correct user ID");

        String orderPrice = (String) targetOrder.get("orderPrice");
        Assert.assertNotNull(orderPrice, "Order should have orderPrice");

        logger.info("API order history verification completed successfully!");
    }

    private void loginToApplication(String email, String password) {
        logger.info("Logging into the application.");
        driver.findElement(By.cssSelector("#userEmail")).sendKeys(email);
        driver.findElement(By.cssSelector("#userPassword")).sendKeys(password);
        driver.findElement(By.cssSelector("#login")).click();
    }

    private void jsClick(WebElement element) {
        logger.info("Performing JavaScript click on element: " + element);
        js.executeScript("arguments[0].click();", element);
    }

}
