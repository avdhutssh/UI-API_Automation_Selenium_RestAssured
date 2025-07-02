package com.ecom.app.PageObjects;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ecom.app.Utilities.ElementUtils;

public class CartPage extends ElementUtils {

    private final By cartItems = By.cssSelector(".cartSection");
    private final By cartItemTitles = By.cssSelector(".cartSection h3");
    private final By removeButtons = By.xpath("//button[contains(@class,'btn btn-danger')]");
    private final By checkoutButton = By.xpath("//button[contains(text(), 'Checkout')]");
    private final By emptyCartMessage = By.xpath("//*[contains(text(),'No Product')]");
    private final By cartHeader = By.xpath("//h1[contains(text(),'My Cart')]");
    private final By totalPrice = By.cssSelector(".totalRow .value");

    public CartPage(WebDriver driver) {
        super(driver);
        logger.info("CartPage initialized");
    }

    public List<String> getCartProductNames() {
        if (isCartEmpty()) {
            logger.info("Cart is empty, returning empty list");
            return List.of();
        }

        List<WebElement> titleElements = getElements(cartItemTitles);
        List<String> titles = titleElements.stream()
                .map(element -> element.getText().trim())
                .collect(Collectors.toList());
        logger.info("Retrieved " + titles.size() + " products from cart: " + titles);
        return titles;
    }

    public void verifyProductInCart(String productName) {
        By productLocator = By.xpath("//h3[contains(text(),'" + productName + "')]");
        if (isElementDisplayed(productLocator)) {
            logger.info("Product verified in cart: " + productName);
        } else {
            logger.warning("Product not found in cart: " + productName);
        }
    }

    public void removeProductFromCart(String productName) {
        logger.info("Removing product from cart: " + productName);
        By removeButton = By.xpath("//h3[contains(text(),'" + productName + "')]/../..//button[contains(@class,'btn btn-danger')]");

        if (isElementDisplayed(removeButton)) {
            clickOnElement(removeButton);
            logger.info("Successfully removed product: " + productName);
        } else {
            logger.warning("Remove button not found for product: " + productName);
        }
    }

    public void proceedToCheckout() {
        if (isCartEmpty()) {
            logger.warning("Cannot proceed to checkout - cart is empty");
            return;
        }

        int itemCount = getElementCount(cartItems);
        logger.info("Proceeding to checkout with " + itemCount + " items");

        scrollToBottom();
        clickOnElement(checkoutButton);
        logger.info("Clicked checkout button");
    }

    public String getCartTotalPrice() {
        if (isElementDisplayed(totalPrice)) {
            String total = getText(totalPrice);
            logger.info("Cart total price: " + total);
            return total;
        }

        logger.warning("Total price not displayed");
        return "";
    }

    public boolean isCartEmpty() {
        boolean isEmpty = isElementDisplayed(emptyCartMessage);
        logger.info("Cart empty status: " + isEmpty);
        return isEmpty;
    }

    public void clearAllItemsFromCart() {
        if (isCartEmpty()) {
            logger.info("Cart is already empty");
            return;
        }

        int initialCount = getElementCount(cartItems);
        logger.info("Clearing all items from cart. Initial count: " + initialCount);

        List<WebElement> removeButtonElements = getElements(removeButtons);

        for (int i = removeButtonElements.size() - 1; i >= 0; i--) {
            try {
                WebElement removeButton = getElements(removeButtons).get(i);
                jsExecutor.executeScript("arguments[0].click();", removeButton);
                Thread.sleep(500);
            } catch (Exception e) {
                logger.warning("Error removing item at index " + i + ": " + e.getMessage());
            }
        }

        logger.info("All items cleared from cart");
    }
}