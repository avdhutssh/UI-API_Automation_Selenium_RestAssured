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

    @Test(priority = 7)
    @Story("Complete Order Flow")
    @Description("Test complete end-to-end order placement flow")
    @Severity(SeverityLevel.BLOCKER)
    public void test_07_e2e_verifyCompleteOrderFlow() {
        AllureReportUtils.logStep("Starting complete E2E order flow test");

        loginPage.loginWithDefaultCredentials();
        productPage.searchAndAddProductToCart(PRODUCT_NAME);
        Assert.assertTrue(productPage.isProductDisplayed(PRODUCT_NAME), "Product should be displayed after search");
        productPage.navigateToCart();
        cartPage.verifyProductInCart(PRODUCT_NAME);
        cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPage.isCheckoutPageLoaded(), "Checkout page should be loaded");
        checkoutPage.completeCheckout(COUNTRY_NAME, "4242424242424242", "123", "Test User");
        String orderIdFromConfirmation = orderConfirmationPage.getOrderConfirmationDetails();
        Assert.assertFalse(orderIdFromConfirmation.isEmpty(), "Order ID should not be empty");
        setOrderId(orderIdFromConfirmation);
        AllureReportUtils.logTestData("Order ID", orderIdFromConfirmation);
        boolean orderInHistory = orderConfirmationPage.verifyOrderInHistory(orderIdFromConfirmation);
        Assert.assertTrue(orderInHistory, "Order should be present in order history");
    }

    @Test(dependsOnMethods = "test_07_e2e_verifyCompleteOrderFlow", priority = 8)
    @Story("API Order Verification")
    @Description("Test API order history verification")
    @Severity(SeverityLevel.CRITICAL)
    public void test_08_API_verifyOrderHistory() {
        AllureReportUtils.logStep("Starting API order history verification");

        String orderIdToVerify = getOrderId();
        if (orderIdToVerify == null || orderIdToVerify.isEmpty()) {
            AllureReportUtils.logStep("Order ID not available - skipping verification");
            return;
        }

        AllureReportUtils.logTestData("Order ID to verify", orderIdToVerify);

        Response response = getRequestFactory().getOrdersForCustomer(getUserId());

        Assert.assertEquals(response.statusCode(), StatusCode.OK.getCode(), "Get orders API should return 200");
        Assert.assertEquals(response.jsonPath().getString("message"), "Orders fetched for customer Successfully");

        List<Map<String, Object>> orders = response.jsonPath().getList("data");
        Assert.assertTrue(orders.size() > 0, "Customer should have at least one order");

        Map<String, Object> targetOrder = null;
        boolean orderFound = false;

        for (Map<String, Object> order : orders) {
            String currentOrderId = (String) order.get("_id");
            if (orderIdToVerify.equals(currentOrderId)) {
                targetOrder = order;
                orderFound = true;
                break;
            }
        }

        Assert.assertTrue(orderFound, "Order ID " + orderIdToVerify + " should be present in the order history");
        Assert.assertNotNull(targetOrder, "Target order should not be null");

        String actualOrderBy = (String) targetOrder.get("orderBy");
        String actualProductName = (String) targetOrder.get("productName");
        String actualCountry = (String) targetOrder.get("country");
        String actualOrderById = (String) targetOrder.get("orderById");

        Assert.assertEquals(actualOrderBy, EMAIL, "Order should be placed by the correct user");
        Assert.assertEquals(actualProductName, PRODUCT_NAME, "Product name should match");
        Assert.assertEquals(actualCountry, COUNTRY_NAME, "Country should match");
        Assert.assertEquals(actualOrderById, getUserId(), "Order should be placed by the correct user ID");

        String orderPrice = (String) targetOrder.get("orderPrice");
        Assert.assertNotNull(orderPrice, "Order should have orderPrice");

        AllureReportUtils.logTestData("Order Price", orderPrice);
        AllureReportUtils.logTestData("Product Name", actualProductName);
    }
} 