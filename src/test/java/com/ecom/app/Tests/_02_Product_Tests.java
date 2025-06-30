package com.ecom.app.Tests;

import com.ecom.app.BaseComponents.BaseTest;
import com.ecom.app.utils.AllureReportUtils;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Epic("Product Management Tests")
@Feature("Product Functionality")
public class _02_Product_Tests extends BaseTest {

    @Test(priority = 1)
    @Story("Product Search")
    @Description("Test product search and verification")
    @Severity(SeverityLevel.NORMAL)
    public void test_01_UI_searchProductAndVerify() {
        AllureReportUtils.logStep("Testing product search functionality");

        loginPage.loginWithDefaultCredentials();
        Assert.assertTrue(productPage.isProductDisplayed(PRODUCT_NAME), "Product should be displayed after search");
    }

    @Test(priority = 2)
    @Story("Add to Cart")
    @Description("Test adding single product to cart")
    @Severity(SeverityLevel.CRITICAL)
    public void test_02_UI_verifyAddProductToCart() {
        AllureReportUtils.logStep("Testing add product to cart functionality");

        loginPage.loginWithDefaultCredentials();
        productPage.searchAndAddProductToCart(PRODUCT_NAME);

        int finalCartCount = productPage.getCartCount();
        Assert.assertEquals(finalCartCount, 1, "Cart count should increase by 1 after adding product");
    }

    @Test(priority = 3)
    @Story("Add Multiple Products")
    @Description("Test adding multiple products to cart")
    @Severity(SeverityLevel.NORMAL)
    public void test_03_UI_verifyMultipleProductsAddToCart() {
        AllureReportUtils.logStep("Testing multiple products add to cart");

        loginPage.loginWithDefaultCredentials();
        List<String> productsToAdd = List.of(PRODUCT_NAME, "ADIDAS ORIGINAL");

        productPage.addMultipleProductsToCart(productsToAdd);

        int finalCartCount = productPage.getCartCount();
        Assert.assertEquals(finalCartCount, productsToAdd.size(),
                "Cart count should increase by number of products added");

        AllureReportUtils.logTestData("Products Added", String.valueOf(productsToAdd.size()));
    }

    @Test(priority = 4)
    @Story("Product Display")
    @Description("Test product display names verification")
    @Severity(SeverityLevel.MINOR)
    public void test_04_UI_verifyProductDisplayNames() {
        AllureReportUtils.logStep("Testing product display names");

        loginPage.loginWithDefaultCredentials();
        List<String> displayedProducts = productPage.getDisplayedProductNames();

        Assert.assertTrue(displayedProducts.size() > 0, "At least one product should be displayed");
        Assert.assertTrue(displayedProducts.stream().anyMatch(name -> name.contains("ZARA") || name.contains("ADIDAS")),
                "Expected products should be in the list");

        AllureReportUtils.logTestData("Products Found", String.valueOf(displayedProducts.size()));
    }

    @Test(priority = 5)
    @Story("Search Functionality")
    @Description("Test product search functionality validation")
    @Severity(SeverityLevel.NORMAL)
    public void test_05_UI_verifyProductSearchFunctionality() {
        AllureReportUtils.logStep("Testing product search functionality");

        loginPage.loginWithDefaultCredentials();

        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        Assert.assertTrue(productPage.isProductDisplayed("ZARA"), "ZARA product should be displayed after search");
    }
} 