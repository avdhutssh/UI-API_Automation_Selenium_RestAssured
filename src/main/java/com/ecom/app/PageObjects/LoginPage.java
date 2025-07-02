package com.ecom.app.PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.ecom.app.Utilities.ElementUtils;

public class LoginPage extends ElementUtils {

    private final By emailField = By.cssSelector("#userEmail");
    private final By passwordField = By.cssSelector("#userPassword");
    private final By loginButton = By.cssSelector("#login");
    private final By signOutButton = By.xpath("//button[normalize-space(text())='Sign Out']");
    private final By errorMessage = By.cssSelector("[role='alert']");
    private final By loginForm = By.cssSelector(".login-wrapper");

    public LoginPage(WebDriver driver) {
        super(driver);
        logger.info("LoginPage initialized");
    }

    public void navigateToLoginPage() {
        String baseUrl = getProperty("baseUrl") + "/client";
        driver.get(baseUrl);
        logger.info("Navigated to login page: " + baseUrl);
    }

    public void loginToApplication(String email, String password) {
        logger.info("Starting login process for user: " + email);
        navigateToLoginPage();
        enterText(emailField, email, true);
        enterText(passwordField, password, true);
        clickOnElement(loginButton);
    }

    public void loginWithDefaultCredentials() {
        String email = getProperty("email");
        String password = getProperty("password");
        logger.info("Using default credentials from config");
        loginToApplication(email, password);
        if (isUserLoggedIn()) {
			logger.info("Login successful for user: " + email);
		}
    }

    public boolean isUserLoggedIn() {
        boolean loggedIn = isElementDisplayed(signOutButton);
        logger.info("User login status: " + loggedIn);
        return loggedIn;
    }

    public void signOutFromApplication() {
        if (isUserLoggedIn()) {
            waitForElementToBeVisible(signOutButton);
            clickOnElement(signOutButton);
            logger.info("User signed out successfully");
        } else {
            logger.warning("Sign out attempted but user not logged in");
        }
    }

    public String getLoginErrorMessage() {
        waitForElementToBeVisible(errorMessage);
        String error = getText(errorMessage);
        logger.info("Retrieved login error message: " + error);
        return error;
    }

}