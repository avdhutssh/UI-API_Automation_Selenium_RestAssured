package com.ecom.app.PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.ecom.app.Utilities.ElementUtils;

public class CheckoutPage extends ElementUtils {

    private final By countryField = By.cssSelector("[placeholder*='Country']");
    private final By cardNumberField = By.xpath("(//input[@type='text'])[1]");
    private final By cvvField = By.xpath("(//input[@type='text'])[2]");
    private final By nameOnCardField = By.xpath("(//input[@type='text'])[3]");
    private final By placeOrderButton = By.xpath("//*[normalize-space(text())='Place Order']");
    private final By checkoutHeader = By.cssSelector("div.payment");

    public CheckoutPage(WebDriver driver) {
        super(driver);
        logger.info("CheckoutPage initialized");
    }

    public void enterShippingCountry(String countryName) {
        logger.info("Entering shipping country: " + countryName);

        enterCharacterByCharacter(countryField, countryName, 50);

        By countryOption = By.xpath("//span[normalize-space(text())='" + countryName + "']");
        waitForElementToBeVisible(countryOption);
        clickOnElement(countryOption);

        logger.info("Selected shipping country: " + countryName);
    }

    public void fillPaymentDetails(String countryName, String cardNumber, String cvv, String nameOnCard) {
        logger.info("Filling complete payment details");

        enterShippingCountry(countryName);
        enterText(cardNumberField, cardNumber, true);
        enterText(cvvField, cvv, true);
        enterText(nameOnCardField, nameOnCard, true);

        logger.info("All payment details filled successfully");
    }

    public void completeCheckout(String countryName, String cardNumber, String cvv, String nameOnCard) {
        logger.info("Starting complete checkout process");

        waitForElementToBeVisible(checkoutHeader);
        fillPaymentDetails(countryName, cardNumber, cvv, nameOnCard);

        scrollToBottom();
        clickOnElement(placeOrderButton);

        logger.info("Checkout process completed - order placed");
    }

    public boolean isCheckoutPageLoaded() {
        waitForElementToBeVisible(checkoutHeader);
        boolean isLoaded = isElementDisplayed(checkoutHeader);
        logger.info("Checkout page loaded: " + isLoaded);
        return isLoaded;
    }
}