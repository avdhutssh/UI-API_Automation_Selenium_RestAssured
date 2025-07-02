package com.ecom.app.Utilities;

import java.time.Duration;
import java.util.Collections;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

/**
 * Factory class for creating and configuring WebDriver instances
 */
public class BrowserDriverFactory {

    private static final Logger logger = Logger.getLogger(BrowserDriverFactory.class.getName());
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Create WebDriver instance based on browser type
     *
     * @param browserName Browser name (chrome, firefox, edge, safari)
     * @param headless    Whether to run in headless mode
     * @return WebDriver instance
     */
    public static WebDriver createDriver(String browserName, boolean headless) {
        WebDriver driver = null;

        switch (browserName.toLowerCase()) {
            case "chrome":
                driver = createChromeDriver(headless);
                break;
            case "firefox":
                driver = createFirefoxDriver(headless);
                break;
            case "edge":
                driver = createEdgeDriver(headless);
                break;
            case "safari":
                driver = createSafariDriver();
                break;
            default:
                logger.warning("Browser '" + browserName + "' not supported. Using Chrome as default.");
                driver = createChromeDriver(headless);
        }

        if (driver != null) {
            configureDriver(driver);
            driverThreadLocal.set(driver);
            logger.info("Driver created successfully: " + browserName);
        }

        return driver;
    }

    /**
     * Create Chrome driver with options
     *
     * @param headless Whether to run in headless mode
     * @return ChromeDriver instance
     */
    private static WebDriver createChromeDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        // Performance and security options
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-autofill");
        options.addArguments("--disable-autofill-keyboard-accessory-view");
        options.addArguments("--disable-full-form-autofill-ios");
        options.addArguments("--disable-payments-autofill");
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // Disable autofill via preferences
        options.setExperimentalOption("prefs", Collections.singletonMap("autofill.credit_card_enabled", false));

        if (headless) {
            options.addArguments("--headless=new");
            logger.info("Chrome driver configured for headless mode");
        }

        return new ChromeDriver(options);
    }

    /**
     * Create Firefox driver with options
     *
     * @param headless Whether to run in headless mode
     * @return FirefoxDriver instance
     */
    private static WebDriver createFirefoxDriver(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("--headless");
            logger.info("Firefox driver configured for headless mode");
        }

        return new FirefoxDriver(options);
    }

    /**
     * Create Edge driver with options
     *
     * @param headless Whether to run in headless mode
     * @return EdgeDriver instance
     */
    private static WebDriver createEdgeDriver(boolean headless) {
        EdgeOptions options = new EdgeOptions();

        if (headless) {
            options.addArguments("--headless");
            logger.info("Edge driver configured for headless mode");
        }

        return new EdgeDriver(options);
    }

    /**
     * Create Safari driver (headless not supported)
     *
     * @return SafariDriver instance
     */
    private static WebDriver createSafariDriver() {
        logger.info("Safari driver does not support headless mode");
        return new SafariDriver();
    }

    /**
     * Configure common driver settings
     *
     * @param driver WebDriver instance to configure
     */
    private static void configureDriver(WebDriver driver) {
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().deleteAllCookies();
    }

    /**
     * Get the current thread's WebDriver instance
     *
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Close and quit the current thread's WebDriver instance
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("Driver quit successfully");
            } catch (Exception e) {
                logger.warning("Error while quitting driver: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Check if driver is initialized for current thread
     *
     * @return true if driver exists and is not null
     */
    public static boolean isDriverInitialized() {
        return driverThreadLocal.get() != null;
    }
}