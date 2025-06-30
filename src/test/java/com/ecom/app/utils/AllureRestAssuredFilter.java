package com.ecom.app.utils;

import io.qameta.allure.restassured.AllureRestAssured;

public class AllureRestAssuredFilter {
    private static AllureRestAssured instance;

    public static AllureRestAssured getInstance() {
        if (instance == null) {
            instance = new AllureRestAssured();
        }
        return instance;
    }
} 