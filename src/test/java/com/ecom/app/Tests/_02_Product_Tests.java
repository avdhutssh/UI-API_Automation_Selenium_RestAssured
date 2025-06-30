package com.ecom.app.Tests;

import com.ecom.app.BaseComponents.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class _02_Product_Tests extends BaseTest {

    @Test(priority = 1)
    public void test_01_UI_searchProductAndVerify() {
        logger.info("Starting test to search for a product and verify its presence");
        
        loginPage.loginWithDefaultCredentials();
        Assert.assertTrue(productPage.isProductDisplayed(PRODUCT_NAME), "Product should be displayed after search");
        
        logger.info("Product search and verification completed successfully");
    }

    @Test(priority = 2)
    public void test_02_UI_verifyAddProductToCart() {
        logger.info("Starting test to add product to cart and verify cart count");
        
        loginPage.loginWithDefaultCredentials();
        int initialCartCount = productPage.getCartCount();
        
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        
        int finalCartCount = productPage.getCartCount();
        Assert.assertEquals(finalCartCount, initialCartCount + 1, "Cart count should increase by 1 after adding product");
        
        logger.info("Product added to cart successfully and cart count verified");
    }

    @Test(priority = 3)
    public void test_03_UI_verifyMultipleProductsAddToCart() {
        logger.info("Starting test to add multiple products to cart");
        
        loginPage.loginWithDefaultCredentials();
        List<String> productsToAdd = List.of(PRODUCT_NAME, "ADIDAS ORIGINAL");
        int initialCartCount = productPage.getCartCount();
        
        productPage.addMultipleProductsToCart(productsToAdd);
        
        int finalCartCount = productPage.getCartCount();
        Assert.assertEquals(finalCartCount, initialCartCount + productsToAdd.size(), 
                "Cart count should increase by number of products added");
        
        logger.info("Multiple products added to cart successfully");
    }

    @Test(priority = 4)
    public void test_04_UI_verifyProductDisplayNames() {
        logger.info("Starting test to verify product names are displayed");
        
        loginPage.loginWithDefaultCredentials();
        List<String> displayedProducts = productPage.getDisplayedProductNames();
        
        Assert.assertTrue(displayedProducts.size() > 0, "At least one product should be displayed");
        Assert.assertTrue(displayedProducts.stream().anyMatch(name -> name.contains("ZARA") || name.contains("ADIDAS")), 
                "Expected products should be in the list");
        
        logger.info("Product display names verified successfully. Found " + displayedProducts.size() + " products");
    }

    @Test(priority = 5)
    public void test_05_UI_verifyProductSearchFunctionality() {
        logger.info("Starting test to verify product search functionality");
        
        loginPage.loginWithDefaultCredentials();
        
        productPage.searchAndAddProductToCart("ZARA");
        Assert.assertTrue(productPage.isProductDisplayed("ZARA"), "ZARA product should be displayed after search");
        
        logger.info("Product search functionality verified successfully");
    }
} 