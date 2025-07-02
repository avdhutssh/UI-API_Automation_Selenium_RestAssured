package com.ecom.app.PageObjects;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ecom.app.Utilities.ElementUtils;

public class ProductPage extends ElementUtils {

    private final By searchBox = By.xpath("(//input[@name='search'])[2]");
    private final By productCards = By.cssSelector(".card");
    private final By productTitles = By.cssSelector(".card-body h5");
    private final By cartIcon = By.cssSelector("[routerlink*='cart']");
    private final By cartCount = By.cssSelector("[routerlink*='cart'] label");
    private final By loadingSpinner = By.cssSelector(".spinner-border");
    private final By noProductsMessage = By.xpath("//*[contains(text(),'No Products Found')]");
    private final By productAddedToast = By.cssSelector("[aria-label='Product Added To Cart']");

    public ProductPage(WebDriver driver) {
        super(driver);
        logger.info("ProductPage initialized");
    }

    public void searchAndAddProductToCart(String productName) {
        logger.info("Searching and adding product to cart: " + productName);

        enterText(searchBox, productName, true, true);
        waitForProductsToLoad();

        if (isProductDisplayed(productName)) {
            addProductToCart(productName);
            logger.info("Successfully added product to cart: " + productName);
        } else {
            logger.warning("Product not found: " + productName);
        }
    }

    public void addMultipleProductsToCart(List<String> productNames) {
        logger.info("Adding multiple products to cart: " + productNames);

        for (String productName : productNames) {
            searchAndAddProductToCart(productName);
            clearText(searchBox);
        }

        logger.info("Completed adding " + productNames.size() + " products to cart");
    }

    public void addProductToCart(String productName) {
        By addToCartButton = By.xpath("//*[normalize-space(text())='" + productName + "']/../following-sibling::button[normalize-space(text())='Add To Cart']");
        scrollIntoView(addToCartButton);
        clickOnElement(addToCartButton);
        waitForProductAddedToast();
        logger.info("Added product to cart: " + productName);
    }

    public boolean isProductDisplayed(String productName) {
        By productLocator = By.xpath("//*[contains(text(),'" + productName + "')]");
        boolean displayed = isElementDisplayed(productLocator);
        logger.info("Product '" + productName + "' displayed: " + displayed);
        return displayed;
    }

    public void navigateToCart() {
        int currentCartCount = getCartCount();
        logger.info("Navigating to cart with " + currentCartCount + " items");
        clickOnElement(cartIcon);
    }

    public int getCartCount() {
        waitForElementToBeVisible(cartCount);
        if (isElementDisplayed(cartCount)) {
            String countText = getText(cartCount);
            int count = Integer.parseInt(countText);
            logger.info("Current cart count: " + count);
            return count;
        }
        logger.info("Cart count not displayed, returning 0");
        return 0;
    }

    public List<String> getDisplayedProductNames() {
        List<WebElement> titleElements = getElements(productTitles);
        List<String> titles = titleElements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        logger.info("Retrieved " + titles.size() + " product names: " + titles);
        return titles;
    }

    public void waitForProductsToLoad() {
        if (isElementDisplayed(loadingSpinner)) {
            waitForElementToDisappear(loadingSpinner);
        }
        try {
            waitForElementToBeVisible(productCards);
        } catch (Exception e) {
            if (!isElementDisplayed(noProductsMessage)) {
                logger.warning("Neither products nor 'no products' message displayed");
            }
        }
    }

    private void waitForProductAddedToast() {
        try {
            waitForElementToBeVisible(productAddedToast);
            waitForElementToDisappear(productAddedToast);
        } catch (Exception e) {
            logger.warning("Product added toast notification not found or timeout");
        }
    }
}