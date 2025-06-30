package com.ecom.app.generic;

import com.ecom.app.constants.Endpoints;
import com.ecom.app.utils.AllureReportUtils;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class RequestFactory {
    private static final Logger log = LogManager.getLogger(RequestFactory.class);
    private RestClient restClient = new RestClient();
    private String authToken;
    private String userId;

    public RequestFactory(String authToken, String userId) {
        this.authToken = authToken;
        this.userId = userId;
    }

    @Step("Login to ecom application")
    public Response login(String email, String password) {
        log.info("Performing login for user: {}", email);
        AllureReportUtils.logStep("Login to ecom application");

        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("userEmail", email);
        loginPayload.put("userPassword", password);

        return restClient.doPostRequest(Endpoints.LOGIN, loginPayload);
    }

    @Step("Register new user")
    public Response registerUser(String firstName, String lastName, String email, String password, String phone) {
        log.info("Registering new user: {}", email);
        AllureReportUtils.logStep("Register new user");

        Map<String, String> registerPayload = new HashMap<>();
        registerPayload.put("firstName", firstName);
        registerPayload.put("lastName", lastName);
        registerPayload.put("userEmail", email);
        registerPayload.put("userPassword", password);
        registerPayload.put("userMobile", phone);

        return restClient.doPostRequest(Endpoints.REGISTER, registerPayload);
    }

    @Step("Get all products")
    public Response getAllProducts() {
        log.info("Getting all products");
        AllureReportUtils.logStep("Get all products");

        return restClient.doGetRequest(Endpoints.GET_ALL_PRODUCTS, authToken);
    }

    @Step("Create new order")
    public Response createOrder(String productId, String country) {
        log.info("Creating order for product: {} in country: {}", productId, country);
        AllureReportUtils.logStep("Create new order");

        Map<String, Object> orderPayload = new HashMap<>();
        Map<String, String> orderItem = new HashMap<>();
        orderItem.put("country", country);
        orderItem.put("productOrderedId", productId);
        
        orderPayload.put("orders", new Map[]{orderItem});

        return restClient.doPostRequest(Endpoints.CREATE_ORDER, orderPayload, authToken);
    }

    @Step("Get orders for customer")
    public Response getOrdersForCustomer(String userId) {
        log.info("Getting orders for customer: {}", userId);
        AllureReportUtils.logStep("Get orders for customer");

        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("userId", userId);

        return restClient.doGetRequest(Endpoints.GET_ORDERS_FOR_CUSTOMER, pathParams, authToken);
    }

    @Step("Get order details for order ID: {orderId}")
    public Response getOrderDetails(String orderId) {
        log.info("Getting order details for ID: {}", orderId);
        AllureReportUtils.logStep("Getting order details for ID: " + orderId);

        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("orderId", orderId);

        return restClient.doGetRequest(Endpoints.GET_ORDER_DETAILS, pathParams, authToken);
    }

    @Step("Delete order with ID: {orderId}")
    public Response deleteOrder(String orderId) {
        log.info("Deleting order with ID: {}", orderId);
        AllureReportUtils.logStep("Deleting order with ID: " + orderId);

        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("orderId", orderId);

        return restClient.doDeleteRequest(Endpoints.DELETE_ORDER, pathParams, authToken);
    }

    @Step("Add product to cart")
    public Response addToCart(String productId) {
        log.info("Adding product to cart: {}", productId);
        AllureReportUtils.logStep("Add product to cart");

        Map<String, String> cartPayload = new HashMap<>();
        cartPayload.put("productId", productId);

        return restClient.doPostRequest(Endpoints.ADD_TO_CART, cartPayload, authToken);
    }

    @Step("Remove product from cart")
    public Response removeFromCart(String cartId) {
        log.info("Removing product from cart: {}", cartId);
        AllureReportUtils.logStep("Remove product from cart");

        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("cartId", cartId);

        return restClient.doDeleteRequest(Endpoints.DELETE_FROM_CART, pathParams, authToken);
    }

    @Step("Delete product")
    public Response deleteProduct(String productId) {
        log.info("Deleting product: {}", productId);
        AllureReportUtils.logStep("Delete product");

        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("productId", productId);

        return restClient.doDeleteRequest(Endpoints.DELETE_PRODUCT, pathParams, authToken);
    }

    @Step("Get user profile")
    public Response getUserProfile() {
        log.info("Getting user profile");
        AllureReportUtils.logStep("Get user profile");

        return restClient.doGetRequest(Endpoints.GET_USER_PROFILE, authToken);
    }
} 