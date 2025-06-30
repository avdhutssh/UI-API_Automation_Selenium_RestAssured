package com.ecom.app.Tests;

import com.ecom.app.BaseComponents.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class _03_Cart_Tests extends BaseTest {

    @Test(priority = 1)
    public void test_01_UI_verifyAddedItemsArePresentInCart() {
        logger.info("Starting test to verify added items are present in the cart");
        
        loginPage.loginWithDefaultCredentials();
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        productPage.navigateToCart();
        
        cartPage.verifyProductInCart(PRODUCT_NAME);
        List<String> cartProducts = cartPage.getCartProductNames();
        
        Assert.assertTrue(cartProducts.contains(PRODUCT_NAME), "Product should be present in cart");
        Assert.assertEquals(cartProducts.size(), 1, "Cart should contain exactly 1 product");
        
        logger.info("Items in the cart verified successfully");
    }

    @Test(priority = 2)
    public void test_02_UI_verifyRemoveCartItem() {
        logger.info("Starting test to verify removal of cart item");
        
        loginPage.loginWithDefaultCredentials();
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        productPage.navigateToCart();
        
        cartPage.verifyProductInCart(PRODUCT_NAME);
        cartPage.removeProductFromCart(PRODUCT_NAME);
        
        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty after removing all items");
        
        logger.info("Cart item removal verified successfully");
    }

    @Test(priority = 3)
    public void test_03_UI_verifyCartTotalPrice() {
        logger.info("Starting test to verify cart total price");
        
        loginPage.loginWithDefaultCredentials();
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        productPage.navigateToCart();
        
        String totalPrice = cartPage.getCartTotalPrice();
        Assert.assertFalse(totalPrice.equals("$0"), "Cart total should not be $0 when items are present");
        Assert.assertTrue(totalPrice.startsWith("$"), "Total price should start with $ symbol");
        
        logger.info("Cart total price verified: " + totalPrice);
    }

    @Test(priority = 4)
    public void test_04_UI_verifyMultipleItemsInCart() {
        logger.info("Starting test to verify multiple items in cart");
        
        loginPage.loginWithDefaultCredentials();
        List<String> productsToAdd = List.of(PRODUCT_NAME, "ADIDAS ORIGINAL");
        productPage.addMultipleProductsToCart(productsToAdd);
        productPage.navigateToCart();
        
        List<String> cartProducts = cartPage.getCartProductNames();
        Assert.assertEquals(cartProducts.size(), productsToAdd.size(), "Cart should contain all added products");
        
        for (String product : productsToAdd) {
            cartPage.verifyProductInCart(product);
        }
        
        logger.info("Multiple items in cart verified successfully");
    }

    @Test(priority = 5)
    public void test_05_UI_verifyClearAllItemsFromCart() {
        logger.info("Starting test to clear all items from cart");
        
        loginPage.loginWithDefaultCredentials();
        List<String> productsToAdd = List.of(PRODUCT_NAME, "ADIDAS ORIGINAL");
        productPage.addMultipleProductsToCart(productsToAdd);
        productPage.navigateToCart();
        
        Assert.assertFalse(cartPage.isCartEmpty(), "Cart should not be empty before clearing");
        
        cartPage.clearAllItemsFromCart();
        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty after clearing all items");
        
        logger.info("All items cleared from cart successfully");
    }

    @Test(priority = 6)
    public void test_06_UI_verifyProceedToCheckout() {
        logger.info("Starting test to verify proceed to checkout functionality");
        
        loginPage.loginWithDefaultCredentials();
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        productPage.navigateToCart();
        
        cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPage.isCheckoutPageLoaded(), "Checkout page should be loaded after clicking checkout");
        
        logger.info("Proceed to checkout functionality verified successfully");
    }
} 