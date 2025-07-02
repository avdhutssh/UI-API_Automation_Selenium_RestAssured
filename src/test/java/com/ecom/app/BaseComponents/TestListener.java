package com.ecom.app.BaseComponents;

import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.ecom.app.Utilities.BrowserDriverFactory;
import com.ecom.app.Utilities.ScreenshotUtils;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;

public class TestListener implements ITestListener {

    private static final Logger logger = Logger.getLogger(TestListener.class.getName());


    @Override
    public void onTestStart(ITestResult result) {
        logger.info("🧪 TEST STARTED: " + getTestName(result));

        Allure.description("Test: " + getTestName(result));
        Allure.label("testClass", result.getTestClass().getName());
        Allure.label("testMethod", result.getMethod().getMethodName());
        Object[] parameters = result.getParameters();
        if (parameters.length > 0) {
            StringBuilder paramStr = new StringBuilder();
            for (int i = 0; i < parameters.length; i++) {
                paramStr.append("Param").append(i + 1).append(": ").append(parameters[i]);
                if (i < parameters.length - 1) {
                    paramStr.append(", ");
                }
            }
            Allure.parameter("Test Parameters", paramStr.toString());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("✅ TEST PASSED: " + getTestName(result));

        if (isUITest(result)) {
            takeScreenshotOnSuccess(result);
        }

        Allure.step("Test completed successfully");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.severe("❌ TEST FAILED: " + getTestName(result));
        logger.severe("Failure reason: " + result.getThrowable().getMessage());

        if (isUITest(result)) {
            takeScreenshotOnFailure(result);
        }

        Allure.step("Test failed with error: " + result.getThrowable().getMessage());
        attachFailureDetails(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warning("⏭️ TEST SKIPPED: " + getTestName(result));

        if (result.getThrowable() != null) {
            logger.warning("Skip reason: " + result.getThrowable().getMessage());
            Allure.step("Test skipped: " + result.getThrowable().getMessage());
        } else {
            Allure.step("Test skipped");
        }
    }

    private void takeScreenshotOnSuccess(ITestResult result) {
        try {
            WebDriver driver = BrowserDriverFactory.getDriver();

            if (driver != null) {
                byte[] screenshot = ScreenshotUtils.takeScreenshot(driver);

                if (screenshot.length > 0) {
                    attachScreenshotToAllure(screenshot, "Success Screenshot");

                    String testName = getTestName(result);
                    String screenshotPath = ScreenshotUtils.takeScreenshotAndSave(driver, testName);

                    if (!screenshotPath.isEmpty()) {
                        logger.info("Success screenshot saved: " + screenshotPath);
                    }
                }
            } else {
                logger.warning("WebDriver not available for success screenshot");
            }
        } catch (Exception e) {
            logger.warning("Failed to take screenshot on success: " + e.getMessage());
        }
    }

    private void takeScreenshotOnFailure(ITestResult result) {
        try {
            WebDriver driver = BrowserDriverFactory.getDriver();

            if (driver != null) {
                byte[] screenshot = ScreenshotUtils.takeScreenshot(driver);

                if (screenshot.length > 0) {
                    attachScreenshotToAllure(screenshot, "Failure Screenshot");

                    String testName = getTestName(result);
                    String screenshotPath = ScreenshotUtils.takeScreenshotOnFailure(driver, testName);

                    if (!screenshotPath.isEmpty()) {
                        logger.info("Failure screenshot saved: " + screenshotPath);
                    }
                }
            } else {
                logger.warning("WebDriver not available for screenshot");
            }
        } catch (Exception e) {
            logger.severe("Failed to take screenshot on failure: " + e.getMessage());
        }
    }

    @Attachment(value = "{attachmentName}", type = "image/png")
    private byte[] attachScreenshotToAllure(byte[] screenshot, String attachmentName) {
        return screenshot;
    }

    private void attachFailureDetails(ITestResult result) {
        Throwable throwable = result.getThrowable();

        if (throwable != null) {
            attachTextToAllure(getStackTrace(throwable), "Stack Trace");

            Object[] parameters = result.getParameters();
            if (parameters.length > 0) {
                StringBuilder paramStr = new StringBuilder();
                for (int i = 0; i < parameters.length; i++) {
                    paramStr.append("Parameter ").append(i + 1).append(": ");
                    paramStr.append(parameters[i] != null ? parameters[i].toString() : "null");
                    paramStr.append("\n");
                }
                attachTextToAllure(paramStr.toString(), "Test Parameters");
            }
        }
    }

    @Attachment(value = "{attachmentName}", type = "text/plain")
    private String attachTextToAllure(String content, String attachmentName) {
        return content;
    }

    private String getTestName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }

    private boolean isUITest(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        return methodName.contains("UI") || methodName.contains("web") || methodName.contains("e2e");
    }

    private String getStackTrace(Throwable throwable) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}