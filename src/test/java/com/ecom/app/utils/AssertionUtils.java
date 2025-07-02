package com.ecom.app.utils;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import io.qameta.allure.Step;

/**
 * Utility class for assertions with Allure integration
 */
public class AssertionUtils {

    private static final Logger logger = Logger.getLogger(AssertionUtils.class.getName());
    private static final ThreadLocal<SoftAssert> softAssert = new ThreadLocal<>();

    /**
     * Initialize soft assert for current thread
     */
    public static void initializeSoftAssert() {
        softAssert.set(new SoftAssert());
        logger.info("Soft assert initialized");
    }

    /**
     * Assert all soft assertions and clean up
     */
    public static void assertAll() {
        SoftAssert sa = softAssert.get();
        if (sa != null) {
            try {
                sa.assertAll();
                logger.info("All soft assertions passed");
            } finally {
                softAssert.remove();
            }
        }
    }

    private static SoftAssert getSoftAssert() {
        SoftAssert sa = softAssert.get();
        if (sa == null) {
            initializeSoftAssert();
            sa = softAssert.get();
        }
        return sa;
    }

    // Hard Assertions
    @Step("Assert equals: {expected}")
    public static void assertEquals(Object actual, Object expected, String message) {
        logger.info("Assert - Expected: " + expected + ", Actual: " + actual);
        Assert.assertEquals(actual, expected, message);
    }

    @Step("Assert true: {message}")
    public static void assertTrue(boolean condition, String message) {
        logger.info("Assert true: " + message);
        Assert.assertTrue(condition, message);
    }

    @Step("Assert false: {message}")
    public static void assertFalse(boolean condition, String message) {
        logger.info("Assert false: " + message);
        Assert.assertFalse(condition, message);
    }

    // Soft Assertions
    @Step("Soft assert equals: {expected}")
    public static void softAssertEquals(Object actual, Object expected, String message) {
        logger.info("Soft Assert - Expected: " + expected + ", Actual: " + actual);
        getSoftAssert().assertEquals(actual, expected, message);
    }

    @Step("Soft assert true: {message}")
    public static void softAssertTrue(boolean condition, String message) {
        logger.info("Soft Assert true: " + message);
        getSoftAssert().assertTrue(condition, message);
    }

    @Step("Soft assert false: {message}")
    public static void softAssertFalse(boolean condition, String message) {
        logger.info("Soft Assert false: " + message);
        getSoftAssert().assertFalse(condition, message);
    }

    // Custom verifications
    @Step("Verify text contains: {expectedSubstring}")
    public static void verifyTextContains(String actualText, String expectedSubstring, String message) {
        boolean contains = actualText != null && actualText.contains(expectedSubstring);
        getSoftAssert().assertTrue(contains, message + " - Expected text to contain: " + expectedSubstring);
    }

    @Step("Verify URL contains path: {expectedPath}")
    public static void verifyUrlContainsPath(String actualUrl, String expectedPath, String message) {
        boolean contains = actualUrl != null && actualUrl.contains(expectedPath);
        getSoftAssert().assertTrue(contains, message + " - Expected URL to contain: " + expectedPath);
    }
}