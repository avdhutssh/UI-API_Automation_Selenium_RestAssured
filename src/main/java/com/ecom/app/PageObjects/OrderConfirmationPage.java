package com.ecom.app.PageObjects;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ecom.app.Utilities.ElementUtils;

public class OrderConfirmationPage extends ElementUtils {

    private final By confirmationHeader = By.tagName("h1");
    private final By orderIdLabel = By.xpath("//label[@class='ng-star-inserted']");
    private final By ordersHistoryButton = By.xpath("//*[normalize-space(text())='Orders History Page']");
    private final By orderHistoryHeader = By.xpath("//h1[contains(text(),'Your Orders')]");
    private final By orderRows = By.cssSelector("tbody tr");
    private final By orderIds = By.xpath("//th[@scope='row']");
    private final By deleteButtons = By.xpath("//button[contains(text(),'Delete')]");

    public OrderConfirmationPage(WebDriver driver) {
        super(driver);
        logger.info("OrderConfirmationPage initialized");
    }

    public String getOrderConfirmationDetails() {
        logger.info("Getting order confirmation details");

        String confirmationMessage = getText(confirmationHeader);
        String orderId = getOrderId();

        logger.info("Order confirmation - Message: " + confirmationMessage + ", Order ID: " + orderId);
        return orderId;
    }

    public String getOrderId() {
        if (isElementDisplayed(orderIdLabel)) {
            String orderText = getText(orderIdLabel);
            String orderId = orderText.replaceAll("[^a-zA-Z0-9]", "");
            logger.info("Extracted order ID: " + orderId);
            return orderId;
        }
        logger.warning("Order ID not found");
        return "";
    }

    public boolean verifyOrderInHistory(String expectedOrderId) {
        logger.info("Verifying order in history: " + expectedOrderId);

        navigateToOrderHistory();
        boolean orderExists = isOrderPresentInHistory(expectedOrderId);

        logger.info("Order verification result for " + expectedOrderId + ": " + orderExists);
        return orderExists;
    }

    public void navigateToOrderHistory() {
        logger.info("Navigating to order history");
        scrollToTop();
        clickOnElement(ordersHistoryButton);
        waitForElementToBeVisible(orderHistoryHeader);
        logger.info("Order history page loaded");
    }

    public boolean isOrderPresentInHistory(String orderId) {
        if (!isElementDisplayed(orderHistoryHeader)) {
            logger.warning("Order history page not loaded");
            return false;
        }

        List<WebElement> orderElements = getElements(orderIds);
        for (WebElement orderElement : orderElements) {
            String orderText = orderElement.getText().trim();
            if (orderText.equals(orderId)) {
                logger.info("Order found in history: " + orderId);
                return true;
            }
        }

        logger.warning("Order not found in history: " + orderId);
        return false;
    }

    public List<String> getAllOrderIds() {
        if (!isElementDisplayed(orderHistoryHeader)) {
            logger.warning("Order history page not loaded, returning empty list");
            return List.of();
        }

        List<WebElement> orderElements = getElements(orderIds);
        List<String> orderIds = orderElements.stream()
                .map(element -> element.getText().trim())
                .collect(Collectors.toList());

        logger.info("Retrieved " + orderIds.size() + " order IDs: " + orderIds);
        return orderIds;
    }

    public void deleteOrderFromHistory(String orderId) {
        logger.info("Deleting order from history: " + orderId);

        if (!isElementDisplayed(orderHistoryHeader)) {
            logger.warning("Order history page not loaded");
            return;
        }

        By deleteButton = By.xpath("//th[text()='" + orderId + "']/..//button[contains(text(),'Delete')]");
        if (isElementDisplayed(deleteButton)) {
            clickOnElement(deleteButton);
            logger.info("Successfully deleted order: " + orderId);
        } else {
            logger.warning("Delete button not found for order: " + orderId);
        }
    }
}