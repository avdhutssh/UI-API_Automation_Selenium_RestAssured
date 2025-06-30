package com.ecom.app.Tests;

import com.ecom.app.BaseComponents.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class _04_E2E_Tests extends BaseTest {

    @Test(priority = 1)
    public void test_01_e2e_verifyCompleteOrderFlow() {
        logger.info("Starting complete E2E order flow test");
        
        loginPage.loginWithDefaultCredentials();
        
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        Assert.assertTrue(productPage.isProductDisplayed(PRODUCT_NAME), "Product should be displayed after search");
        
        productPage.navigateToCart();
        cartPage.verifyProductInCart(PRODUCT_NAME);
        Assert.assertFalse(cartPage.isCartEmpty(), "Cart should not be empty");
        
        cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPage.isCheckoutPageLoaded(), "Checkout page should be loaded");
        
        checkoutPage.completeCheckout(COUNTRY_NAME, "4242424242424242", "123", "Test User");
        
        String orderIdFromConfirmation = orderConfirmationPage.getOrderConfirmationDetails();
        Assert.assertFalse(orderIdFromConfirmation.isEmpty(), "Order ID should not be empty");
        
        setOrderId(orderIdFromConfirmation);
        
        boolean orderInHistory = orderConfirmationPage.verifyOrderInHistory(orderIdFromConfirmation);
        Assert.assertTrue(orderInHistory, "Order should be present in order history");
        
        logger.info("E2E Complete Order Flow Test Completed Successfully! Order ID: " + orderIdFromConfirmation);
    }

    @Test(priority = 2)
    public void test_02_e2e_verifyMultipleProductsOrderFlow() {
        logger.info("Starting E2E test with multiple products");
        
        loginPage.loginWithDefaultCredentials();
        
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        productPage.searchAndAddProductToCart("ADIDAS ORIGINAL");
        
        productPage.navigateToCart();
        Assert.assertEquals(cartPage.getCartProductNames().size(), 2, "Cart should contain 2 products");
        
        cartPage.proceedToCheckout();
        checkoutPage.completeCheckout(COUNTRY_NAME, "4242424242424242", "123", "Test User");
        
        String orderIdFromConfirmation = orderConfirmationPage.getOrderConfirmationDetails();
        Assert.assertFalse(orderIdFromConfirmation.isEmpty(), "Order ID should not be empty");
        
        logger.info("Multiple products E2E test completed successfully! Order ID: " + orderIdFromConfirmation);
    }

    @Test(priority = 3)
    public void test_03_e2e_verifyCheckoutWithDifferentPaymentDetails() {
        logger.info("Starting E2E test with different payment details");
        
        loginPage.loginWithDefaultCredentials();
        
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        productPage.navigateToCart();
        cartPage.proceedToCheckout();
        
        checkoutPage.completeCheckout("Australia", "5555555555554444", "456", "John Doe");
        
        String orderIdFromConfirmation = orderConfirmationPage.getOrderConfirmationDetails();
        Assert.assertFalse(orderIdFromConfirmation.isEmpty(), "Order ID should not be empty");
        
        logger.info("E2E test with different payment details completed! Order ID: " + orderIdFromConfirmation);
    }

    @Test(priority = 4)
    public void test_04_e2e_verifyOrderHistoryNavigation() {
        logger.info("Starting E2E test for order history navigation");
        
        loginPage.loginWithDefaultCredentials();
        
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        productPage.navigateToCart();
        cartPage.proceedToCheckout();
        checkoutPage.completeCheckout(COUNTRY_NAME, "4242424242424242", "123", "Test User");
        
        String orderIdFromConfirmation = orderConfirmationPage.getOrderConfirmationDetails();
        
        orderConfirmationPage.navigateToOrderHistory();
        Assert.assertTrue(orderConfirmationPage.isOrderPresentInHistory(orderIdFromConfirmation), 
                "Order should be present in history");
        
        logger.info("Order history navigation test completed successfully");
    }

    @Test(priority = 5)
    public void test_05_e2e_verifyCompleteFlowWithOrderDeletion() {
        logger.info("Starting E2E test with order deletion");
        
        loginPage.loginWithDefaultCredentials();
        
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        productPage.navigateToCart();
        cartPage.proceedToCheckout();
        checkoutPage.completeCheckout(COUNTRY_NAME, "4242424242424242", "123", "Test User");
        
        String orderIdFromConfirmation = orderConfirmationPage.getOrderConfirmationDetails();
        
        orderConfirmationPage.navigateToOrderHistory();
        Assert.assertTrue(orderConfirmationPage.isOrderPresentInHistory(orderIdFromConfirmation), 
                "Order should be present before deletion");
        
        orderConfirmationPage.deleteOrderFromHistory(orderIdFromConfirmation);
        
        logger.info("E2E test with order deletion completed successfully");
    }
} 