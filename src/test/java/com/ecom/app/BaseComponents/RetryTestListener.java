package com.ecom.app.BaseComponents;

import java.util.logging.Logger;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryTestListener implements IRetryAnalyzer {

    private static final Logger logger = Logger.getLogger(RetryTestListener.class.getName());
    private static final int MAX_RETRY_COUNT = 2;
    private static final ThreadLocal<Integer> retryCount = new ThreadLocal<>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };


    @Override
    public boolean retry(ITestResult result) {
        int currentRetryCount = retryCount.get();

        // Check if we haven't exceeded max retry attempts
        if (currentRetryCount < MAX_RETRY_COUNT) {
            // Increment retry count
            retryCount.set(currentRetryCount + 1);

            String testName = getTestName(result);
            String failureReason = result.getThrowable() != null ?
                    result.getThrowable().getMessage() : "Unknown error";

            logger.warning("🔄 RETRYING TEST: " + testName +
                          " (Attempt " + (currentRetryCount + 2) + "/" + (MAX_RETRY_COUNT + 1) + ")");
            logger.warning("Previous failure reason: " + failureReason);

            // Check if this test should be retried based on failure type
            if (shouldRetryBasedOnFailure(result)) {
                logger.info("Test qualified for retry: " + testName);
                return true;
            } else {
                logger.info("Test not qualified for retry: " + testName);
                // Reset retry count since we're not retrying
                retryCount.remove();
                return false;
            }
        } else {
            // Max retry attempts reached
            String testName = getTestName(result);
            logger.severe("❌ MAX RETRIES EXCEEDED: " + testName +
                         " (Failed after " + (MAX_RETRY_COUNT + 1) + " attempts)");

            // Reset retry count for next test
            retryCount.remove();
            return false;
        }
    }

    /**
     * Determine if test should be retried based on the type of failure
     * @param result TestNG test result
     * @return true if test should be retried
     */
    private boolean shouldRetryBasedOnFailure(ITestResult result) {
        if (result.getThrowable() == null) {
            return false;
        }

        String failureMessage = result.getThrowable().getMessage();
        String failureClass = result.getThrowable().getClass().getSimpleName();

        // Retry for common transient failures
        String[] retryableFailures = {
            "TimeoutException",
            "StaleElementReferenceException",
            "ElementClickInterceptedException",
            "ElementNotInteractableException",
            "NoSuchElementException",
            "WebDriverException",
            "SocketException",
            "ConnectException",
            "Connection refused",
            "500 Internal Server Error"
        };

        // Check if failure is retryable
        for (String retryableFailure : retryableFailures) {
            if (failureClass.contains(retryableFailure) ||
                (failureMessage != null && failureMessage.contains(retryableFailure))) {
                logger.info("Retryable failure detected: " + retryableFailure);
                return true;
            }
        }

        // Don't retry for assertion failures (test logic issues)
        String[] nonRetryableFailures = {
            "AssertionError",
            "AssertionFailedError",
            "ComparisonFailure"
        };

        for (String nonRetryableFailure : nonRetryableFailures) {
            if (failureClass.contains(nonRetryableFailure)) {
                logger.info("Non-retryable failure detected: " + nonRetryableFailure);
                return false;
            }
        }

        // Default: retry unknown failures
        logger.info("Unknown failure type, will retry: " + failureClass);
        return true;
    }

    /**
     * Get test name from result
     * @param result TestNG test result
     * @return formatted test name
     */
    private String getTestName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }

    /**
     * Get current retry count for the current thread
     * @return current retry count
     */
    public static int getCurrentRetryCount() {
        return retryCount.get();
    }

    /**
     * Reset retry count for current thread
     */
    public static void resetRetryCount() {
        retryCount.remove();
    }

    /**
     * Check if test is currently being retried
     * @return true if this is a retry attempt
     */
    public static boolean isRetryAttempt() {
        return retryCount.get() > 0;
    }
}