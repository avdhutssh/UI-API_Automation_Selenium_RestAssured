package com.ecom.app.Utilities;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.logging.Logger;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Logger logger;
    protected JavascriptExecutor jsExecutor;
    private static final ConfigurationUtils configUtils = ConfigurationUtils.getInstance();

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(configUtils.getProperty("explicitWait", "10"))));
        this.jsExecutor = (JavascriptExecutor) driver;
        this.logger = Logger.getLogger(this.getClass().getName());
        logger.info("BasePage initialized for: " + this.getClass().getSimpleName());
    }

    protected WebElement waitForElementToBeVisible(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.severe("Element not visible within timeout: " + locator);
            throw e;
        }
    }

    protected WebElement waitForElementToBeClickable(By locator) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            logger.severe("Element not clickable within timeout: " + locator);
            throw e;
        }
    }

    protected WebElement waitForElementToBePresent(By locator) {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.severe("Element not present within timeout: " + locator);
            throw e;
        }
    }

    protected void waitForElementToDisappear(By locator) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            logger.info("Element disappeared successfully: " + locator);
        } catch (TimeoutException e) {
            logger.warning("Element did not disappear within timeout: " + locator);
        }
    }

    protected void waitForTextToBePresentInElement(By locator, String text) {
        try {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
            logger.info("Text found in element: " + text);
        } catch (TimeoutException e) {
            logger.severe("Text not found in element within timeout: " + locator + ", Expected text: " + text);
            throw e;
        }
    }

    protected void waitForChildWindow(int windowNumber) {
        try {
            wait.until(ExpectedConditions.numberOfWindowsToBe(windowNumber));
            logger.info("Window count reached: " + windowNumber);
        } catch (TimeoutException e) {
            logger.severe("Timeout waiting for window count: " + windowNumber);
        }
    }

    protected void waitForAlert() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            logger.info("Alert is present");
        } catch (TimeoutException e) {
            logger.severe("Alert not present within timeout");
        }
    }

    protected void switchToWindow(int windowNumber) {
        waitForChildWindow(windowNumber);
        String[] windowHandles = driver.getWindowHandles().stream().toArray(String[]::new);
        try {
            if (windowNumber > windowHandles.length) {
                throw new RuntimeException(String.format(
                        "Window number (%s) is greater than available windows (%s).",
                        windowNumber, windowHandles.length));
            }
            driver.switchTo().window(windowHandles[windowNumber - 1]);
            logger.info("Switched to window number: " + windowNumber);
        } catch (Exception ex) {
            logger.severe("Failed to switch to window: " + windowNumber);
            throw new RuntimeException(
                    String.format("Failed to switch to window number %s. Total windows: %s.",
                            windowNumber, windowHandles.length), ex);
        }
    }

    protected void switchToWindow(String expectedTitle) {
        String parentWindow = driver.getWindowHandle();
        boolean windowFound = false;
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(parentWindow)) {
                driver.switchTo().window(windowHandle);
                if (driver.getTitle().equals(expectedTitle)) {
                    windowFound = true;
                    logger.info("Switched to window with title: " + expectedTitle);
                    break;
                }
            }
        }
        if (!windowFound) {
            logger.warning("Window with title not found: " + expectedTitle);
        }
    }

    protected void switchToFrame(By locator) {
        WebElement frameElement = waitForElementToBePresent(locator);
        driver.switchTo().frame(frameElement);
        logger.info("Switched to frame by locator: " + locator);
    }

    protected void switchToFrame(int frameIndex) {
        driver.switchTo().frame(frameIndex);
        logger.info("Switched to frame by index: " + frameIndex);
    }

    protected void switchToFrame(String frameNameOrId) {
        driver.switchTo().frame(frameNameOrId);
        logger.info("Switched to frame: " + frameNameOrId);
    }

    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
        logger.info("Switched to default content");
    }

    protected Alert switchToAlert() {
        waitForAlert();
        return driver.switchTo().alert();
    }

    protected void acceptAlert() {
        switchToAlert().accept();
        logger.info("Alert accepted");
    }

    protected void dismissAlert() {
        switchToAlert().dismiss();
        logger.info("Alert dismissed");
    }

    protected String getAlertText() {
        String alertText = switchToAlert().getText();
        logger.info("Alert text retrieved: " + alertText);
        return alertText;
    }

    protected void typeTextInAlert(String text) {
        switchToAlert().sendKeys(text);
        logger.info("Text typed in alert: " + text);
    }

    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected String getPageTitle() {
        return driver.getTitle();
    }

    protected String getPageSource() {
        return driver.getPageSource();
    }

    protected void refreshPage() {
        driver.navigate().refresh();
        logger.info("Page refreshed");
    }

    protected void navigateBack() {
        driver.navigate().back();
        logger.info("Navigated back");
    }

    protected void navigateForward() {
        driver.navigate().forward();
        logger.info("Navigated forward");
    }

    protected void scrollToTop() {
        jsExecutor.executeScript("window.scrollTo(0, 0);");
        logger.info("Scrolled to top");
    }

    protected void scrollToBottom() {
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        logger.info("Scrolled to bottom");
    }

    protected int getWindowCount() {
        return driver.getWindowHandles().size();
    }

    protected void closeCurrentWindow() {
        driver.close();
        logger.info("Current window closed");
    }

    protected byte[] takeScreenshot() {
        byte[] screenshot = ScreenshotUtils.takeScreenshot(driver);
        logger.info("Screenshot captured");
        return screenshot;
    }

    protected String getProperty(String key) {
        return configUtils.getProperty(key);
    }

    protected String getProperty(String key, String defaultValue) {
        return configUtils.getProperty(key, defaultValue);
    }
} 