package com.ecom.app;

import com.ecom.app.Utilities.CsvDataProviders;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
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
    Actions act;
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
            js = (JavascriptExecutor) driver;
            act = new Actions(driver);
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

    @Test
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
    @Test
    public void test_05_UI_verifyAddedItemsArePresentInCart() {
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
        js.executeScript("window.scrollBy(0, -500);");
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

    @Test
    public void test_06_UI_verifyRemoveCartItem() {
        logger.info("Starting test to verify removal of cart item.");
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
