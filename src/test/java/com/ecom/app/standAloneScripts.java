package com.ecom.app;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.logging.Logger;

public class standAloneScripts {

    String BASE_URL = "https://rahulshettyacademy.com/client";
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
    public void setup() {
        logger = Logger.getLogger(standAloneScripts.class.getName());
        logger.info("Setting up the WebDriver and initializing variables.");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(BASE_URL);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            logger.info("Browser closed successfully.");
        }
    }

    @Test
    public void test_01_verifyValidLogin() {
        logger.info("Starting test for valid login credentials.");
        driver.findElement(By.cssSelector("#userEmail")).sendKeys(EMAIL);
        driver.findElement(By.cssSelector("#userPassword")).sendKeys(PASSWORD);
        driver.findElement(By.cssSelector("#login")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[normalize-space(text())='Sign Out']")));
        Assert.assertTrue(driver.findElement(By.xpath("//button[normalize-space(text())='Sign Out']")).isDisplayed());
        logger.info("Login successful with valid credentials.");
    }

    //TODO: Add DataProvider for invalid credentials using csv file
    @Test
    public void test_02_verifyInvalidLogin() {
        logger.info("Starting test for Invalid login credentials.");
        driver.findElement(By.cssSelector("#userEmail")).sendKeys("invalidemail@gmail.com");
        driver.findElement(By.cssSelector("#userPassword")).sendKeys("gjhjhkhkj");
        driver.findElement(By.cssSelector("#login")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[role='alert']")));
        String errorMessage = driver.findElement(By.cssSelector("[role='alert']")).getText();
        Assert.assertEquals(errorMessage, "Incorrect email or password.");
        logger.info("Login failed as expected with invalid credentials.");
    }

}
