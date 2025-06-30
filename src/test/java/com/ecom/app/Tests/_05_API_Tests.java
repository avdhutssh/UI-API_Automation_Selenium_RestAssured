package com.ecom.app.Tests;

import com.ecom.app.BaseComponents.BaseTest;
import com.ecom.app.utils.RestClient;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class _05_API_Tests extends BaseTest {

    private String apiToken;
    private String userId;

    @Test(priority = 1)
    public void test_01_API_verifyLoginAndGetToken() {
        logger.info("Starting API test to verify login and get token");
        
        Response response = RestClient.login(EMAIL, PASSWORD);
        
        Assert.assertEquals(response.statusCode(), 200, "Login should be successful");
        
        apiToken = response.jsonPath().getString("token");
        userId = response.jsonPath().getString("userId");
        
        Assert.assertNotNull(apiToken, "API token should not be null");
        Assert.assertNotNull(userId, "User ID should not be null");
        Assert.assertEquals(response.jsonPath().getString("message"), "Login Successfully", "Login message should be correct");
        
        logger.info("API login successful. Token obtained: " + apiToken.substring(0, 10) + "...");
    }

    @Test(dependsOnMethods = "test_01_API_verifyLoginAndGetToken", priority = 2)
    public void test_02_API_verifyGetAllProducts() {
        logger.info("Starting API test to get all products");
        
        Response response = RestClient.getAllProducts();
        
        Assert.assertEquals(response.statusCode(), 200, "Get products should be successful");
        
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        Assert.assertTrue(products.size() > 0, "Should have at least one product");
        
        boolean targetProductFound = false;
        for (Map<String, Object> product : products) {
            String productName = (String) product.get("productName");
            if (productName.contains("ZARA")) {
                targetProductFound = true;
                logger.info("Target product found: " + productName);
                break;
            }
        }
        
        Assert.assertTrue(targetProductFound, "Target product should be found in products list");
        logger.info("Get all products API test completed successfully. Found " + products.size() + " products");
    }

    @Test(dependsOnMethods = "test_01_API_verifyLoginAndGetToken", priority = 3)
    public void test_03_API_verifyOrderHistory() {
        logger.info("Starting API test to verify order history");
        
        Response response = RestClient.getOrdersForCustomer(apiToken, userId);
        
        Assert.assertEquals(response.statusCode(), 200, "Get orders should be successful");
        Assert.assertEquals(response.jsonPath().getString("message"), "Orders fetched for customer Successfully", 
                "Success message should be correct");
        
        List<Map<String, Object>> orders = response.jsonPath().getList("data");
        logger.info("Found " + orders.size() + " orders in history");
        
        if (orders.size() > 0) {
            Map<String, Object> firstOrder = orders.get(0);
            Assert.assertNotNull(firstOrder.get("_id"), "Order should have ID");
            Assert.assertNotNull(firstOrder.get("orderBy"), "Order should have orderBy field");
            Assert.assertNotNull(firstOrder.get("productName"), "Order should have product name");
            Assert.assertNotNull(firstOrder.get("country"), "Order should have country");
            
            logger.info("Order validation successful for order: " + firstOrder.get("_id"));
        }
        
        logger.info("API order history verification completed successfully");
    }

    @Test(dependsOnMethods = "test_01_API_verifyLoginAndGetToken", priority = 4)
    public void test_04_API_verifyCreateOrder() {
        logger.info("Starting API test to create order");
        
        Response productsResponse = RestClient.getAllProducts();
        List<Map<String, Object>> products = productsResponse.jsonPath().getList("data");
        
        String productId = null;
        for (Map<String, Object> product : products) {
            String productName = (String) product.get("productName");
            if (productName.contains("ZARA")) {
                productId = (String) product.get("_id");
                break;
            }
        }
        
        Assert.assertNotNull(productId, "Product ID should be found");
        
        Response orderResponse = RestClient.createOrder(apiToken, productId, COUNTRY_NAME);
        
        Assert.assertEquals(orderResponse.statusCode(), 201, "Order creation should be successful");
        Assert.assertEquals(orderResponse.jsonPath().getString("message"), "Order Placed Successfully", 
                "Success message should be correct");
        
        List<String> orderIds = orderResponse.jsonPath().getList("orders");
        Assert.assertTrue(orderIds.size() > 0, "Should have at least one order ID");
        
        String createdOrderId = orderIds.get(0);
        setOrderId(createdOrderId);
        
        logger.info("API order creation successful. Order ID: " + createdOrderId);
    }

    @Test(dependsOnMethods = "test_04_API_verifyCreateOrder", priority = 5)
    public void test_05_API_verifyOrderCreatedInHistory() {
        logger.info("Starting API test to verify created order in history");
        
        String createdOrderId = getOrderId();
        Assert.assertNotNull(createdOrderId, "Created order ID should not be null");
        
        Response response = RestClient.getOrdersForCustomer(apiToken, userId);
        
        Assert.assertEquals(response.statusCode(), 200, "Get orders should be successful");
        
        List<Map<String, Object>> orders = response.jsonPath().getList("data");
        boolean orderFound = false;
        
        for (Map<String, Object> order : orders) {
            String orderId = (String) order.get("_id");
            if (orderId.equals(createdOrderId)) {
                orderFound = true;
                
                Assert.assertEquals(order.get("orderBy"), EMAIL, "Order should be placed by correct user");
                Assert.assertTrue(((String) order.get("productName")).contains("ZARA"), "Product name should contain ZARA");
                Assert.assertEquals(order.get("country"), COUNTRY_NAME, "Country should match");
                Assert.assertEquals(order.get("orderById"), userId, "Order should be placed by correct user ID");
                
                logger.info("Order found and validated: " + orderId);
                break;
            }
        }
        
        Assert.assertTrue(orderFound, "Created order should be found in order history");
        logger.info("API order verification in history completed successfully");
    }

    @Test(dependsOnMethods = "test_05_API_verifyOrderCreatedInHistory", priority = 6)
    public void test_06_API_verifyDeleteOrder() {
        logger.info("Starting API test to delete order");
        
        String orderIdToDelete = getOrderId();
        Assert.assertNotNull(orderIdToDelete, "Order ID to delete should not be null");
        
        Response deleteResponse = RestClient.deleteOrder(apiToken, orderIdToDelete);
        
        Assert.assertEquals(deleteResponse.statusCode(), 200, "Order deletion should be successful");
        Assert.assertEquals(deleteResponse.jsonPath().getString("message"), "Orders Deleted Successfully", 
                "Success message should be correct");
        
        logger.info("API order deletion successful for order: " + orderIdToDelete);
    }
} 