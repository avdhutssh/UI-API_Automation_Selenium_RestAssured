package com.ecom.app.Tests;

import com.ecom.app.BaseComponents.BaseTest;
import com.ecom.app.constants.StatusCode;
import com.ecom.app.utils.AllureReportUtils;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

@Epic("End-to-End Tests")
@Feature("Complete Order Flow")
public class _04_E2E_Tests extends BaseTest {

    private String createdOrderId;

    @Test(priority = 1, groups = {"smoke"})
    @Story("Complete Order Flow")
    @Description("Test complete end-to-end order placement flow")
    @Severity(SeverityLevel.BLOCKER)
    public void test_01_e2e_verifyCompleteOrderFlow() {
        AllureReportUtils.logStep("Starting complete E2E order flow test");

        loginPage.loginWithDefaultCredentials();
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        Assert.assertTrue(productPage.isProductDisplayed(PRODUCT_NAME), "Product should be displayed after search");
        productPage.navigateToCart();
        cartPage.verifyProductInCart(PRODUCT_NAME);
        cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPage.isCheckoutPageLoaded(), "Checkout page should be loaded");
        checkoutPage.completeCheckout(COUNTRY_NAME, "4242424242424242", "123", "Test User");
        
        createdOrderId = orderConfirmationPage.getOrderConfirmationDetails();
        Assert.assertFalse(createdOrderId.isEmpty(), "Order ID should not be empty");
        AllureReportUtils.logTestData("Order ID", createdOrderId);
        
        boolean orderInHistory = orderConfirmationPage.verifyOrderInHistory(createdOrderId);
        Assert.assertTrue(orderInHistory, "Order should be present in order history");
    }

    @Test(priority = 2, groups = {"smoke"}, dependsOnMethods = "test_01_e2e_verifyCompleteOrderFlow")
    @Story("API Order Verification")
    @Description("Test API order history verification for specific order created in test_01")
    @Severity(SeverityLevel.CRITICAL)
    public void test_02_API_verifyOrderHistory() {
        AllureReportUtils.logStep("Starting API order history verification for order: " + createdOrderId);

        if (getRequestFactory() == null || getUserId() == null) {
            AllureReportUtils.logStep("API components not available - skipping verification");
            return;
        }

        if (createdOrderId == null || createdOrderId.isEmpty()) {
            Assert.fail("Order ID from test_01 not available - test dependency issue");
        }

        AllureReportUtils.logTestData("Order ID to verify", createdOrderId);

        Response response = getRequestFactory().getOrdersForCustomer(getUserId());

        Assert.assertEquals(response.statusCode(), StatusCode.OK.getCode(), "Get orders API should return 200");
        Assert.assertEquals(response.jsonPath().getString("message"), "Orders fetched for customer Successfully");

        List<Map<String, Object>> orders = response.jsonPath().getList("data");
        Assert.assertTrue(orders.size() > 0, "Customer should have at least one order");

        boolean orderFound = orders.stream()
            .anyMatch(order -> createdOrderId.equals(order.get("_id")));

        Assert.assertTrue(orderFound, "Order created in test_01 should be present in API response: " + createdOrderId);

        Map<String, Object> targetOrder = orders.stream()
            .filter(order -> createdOrderId.equals(order.get("_id")))
            .findFirst()
            .orElse(null);

        Assert.assertNotNull(targetOrder, "Target order should be found");

        String actualOrderBy = (String) targetOrder.get("orderBy");
        String actualOrderById = (String) targetOrder.get("orderById");

        Assert.assertEquals(actualOrderBy, EMAIL, "Order should be placed by the correct user");
        Assert.assertEquals(actualOrderById, getUserId(), "Order should be placed by the correct user ID");

        AllureReportUtils.logTestData("Verified Order ID", createdOrderId);
        AllureReportUtils.logTestData("Total Orders Found", String.valueOf(orders.size()));
    }
} 