package com.ecom.app.constants;

public class Endpoints {

    // Base URLs
    public static final String BASE_UI_URL = "https://rahulshettyacademy.com/client";
    public static final String BASE_API_URL = "https://rahulshettyacademy.com/api/ecom";

    // Authentication Endpoints
    public static final String LOGIN = "/auth/login";
    public static final String REGISTER = "/auth/register";

    // Product Endpoints
    public static final String GET_ALL_PRODUCTS = "/product/get-all-products";
    public static final String ADD_PRODUCT = "/product/add-product";
    public static final String DELETE_PRODUCT = "/product/delete-product/{productId}";

    // Cart Endpoints
    public static final String ADD_TO_CART = "/cart/add-to-cart";
    public static final String DELETE_FROM_CART = "/cart/remove-from-cart/{cartId}";

    // Order Endpoints
    public static final String CREATE_ORDER = "/order/create-order";
    public static final String GET_ORDER_DETAILS = "/order/get-orders-details/{orderId}";
    public static final String GET_ORDERS_FOR_CUSTOMER = "/order/get-orders-for-customer/{userId}";
    public static final String DELETE_ORDER = "/order/delete-order/{orderId}";

    // User Profile Endpoints
    public static final String GET_USER_PROFILE = "/user/get-profile";

    private Endpoints() {
        // Private constructor to prevent instantiation
    }
} 