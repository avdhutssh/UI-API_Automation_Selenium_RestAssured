package com.ecom.app.Tests;

import com.ecom.app.BaseComponents.BaseTest;
import com.ecom.app.utils.AllureReportUtils;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Epic("Cart Management Tests")
@Feature("Cart Functionality")
public class _03_Cart_Tests extends BaseTest {

    @Test(priority = 1)
    @Story("Cart Verification")
    @Description("Test verifying added items are present in cart")
    @Severity(SeverityLevel.CRITICAL)
    public void test_01_UI_verifyAddedItemsArePresentInCart() {
        AllureReportUtils.logStep("Testing cart items verification");

        loginPage.loginWithDefaultCredentials();
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        productPage.navigateToCart();

        cartPage.verifyProductInCart(PRODUCT_NAME);
        List<String> cartProducts = cartPage.getCartProductNames();

        Assert.assertTrue(cartProducts.contains(PRODUCT_NAME), "Product should be present in cart");
        Assert.assertEquals(cartProducts.size(), 1, "Cart should contain exactly 1 product");
    }

    @Test(priority = 2)
    @Story("Remove Item")
    @Description("Test removing item from cart")
    @Severity(SeverityLevel.NORMAL)
    public void test_02_UI_verifyRemoveCartItem() {
        AllureReportUtils.logStep("Testing cart item removal");

        loginPage.loginWithDefaultCredentials();
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        productPage.navigateToCart();

        cartPage.verifyProductInCart(PRODUCT_NAME);
        cartPage.removeProductFromCart(PRODUCT_NAME);

        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty after removing all items");
    }

    @Test(priority = 3)
    @Story("Cart Total")
    @Description("Test cart total price calculation")
    @Severity(SeverityLevel.NORMAL)
    public void test_03_UI_verifyCartTotalPrice() {
        AllureReportUtils.logStep("Testing cart total price");

        loginPage.loginWithDefaultCredentials();
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        productPage.navigateToCart();

        String totalPrice = cartPage.getCartTotalPrice();
        Assert.assertFalse(totalPrice.equals("$0"), "Cart total should not be $0 when items are present");
        Assert.assertTrue(totalPrice.startsWith("$"), "Total price should start with $ symbol");

        AllureReportUtils.logTestData("Cart Total", totalPrice);
    }

    @Test(priority = 4)
    @Story("Multiple Items")
    @Description("Test multiple items in cart verification")
    @Severity(SeverityLevel.NORMAL)
    public void test_04_UI_verifyMultipleItemsInCart() {
        AllureReportUtils.logStep("Testing multiple items in cart");

        loginPage.loginWithDefaultCredentials();
        List<String> productsToAdd = List.of(PRODUCT_NAME, "ADIDAS ORIGINAL");
        productPage.addMultipleProductsToCart(productsToAdd);
        productPage.navigateToCart();

        List<String> cartProducts = cartPage.getCartProductNames();
        Assert.assertEquals(cartProducts.size(), productsToAdd.size(), "Cart should contain all added products");

        for (String product : productsToAdd) {
            cartPage.verifyProductInCart(product);
        }

        AllureReportUtils.logTestData("Items in Cart", String.valueOf(cartProducts.size()));
    }

    @Test(priority = 5)
    @Story("Clear Cart")
    @Description("Test clearing all items from cart")
    @Severity(SeverityLevel.NORMAL)
    public void test_05_UI_verifyClearAllItemsFromCart() {
        AllureReportUtils.logStep("Testing clear all items from cart");

        loginPage.loginWithDefaultCredentials();
        List<String> productsToAdd = List.of(PRODUCT_NAME, "ADIDAS ORIGINAL");
        productPage.addMultipleProductsToCart(productsToAdd);
        productPage.navigateToCart();

        Assert.assertFalse(cartPage.isCartEmpty(), "Cart should not be empty before clearing");

        cartPage.clearAllItemsFromCart();
        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty after clearing all items");
    }

} 