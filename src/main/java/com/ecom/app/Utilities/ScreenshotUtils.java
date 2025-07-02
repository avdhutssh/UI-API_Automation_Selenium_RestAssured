package com.ecom.app.Utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Utility class for taking and managing screenshots
 */
public class ScreenshotUtils {

    private static final Logger logger = Logger.getLogger(ScreenshotUtils.class.getName());
    private static final String SCREENSHOT_DIR = "screenshots";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    static {
        createScreenshotDirectory();
    }

    /**
     * Create screenshot directory if it doesn't exist
     */
    private static void createScreenshotDirectory() {
        try {
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotPath)) {
                Files.createDirectories(screenshotPath);
                logger.info("Created screenshot directory: " + SCREENSHOT_DIR);
            }
        } catch (IOException e) {
            logger.severe("Failed to create screenshot directory: " + e.getMessage());
        }
    }

    /**
     * Take screenshot and return as byte array
     * @param driver WebDriver instance
     * @return Screenshot as byte array
     */
    public static byte[] takeScreenshot(WebDriver driver) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshot = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            logger.info("Screenshot taken successfully");
            return screenshot;
        } catch (Exception e) {
            logger.severe("Failed to take screenshot: " + e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Take screenshot and save to file
     * @param driver WebDriver instance
     * @param testName Test name for filename
     * @return Path to saved screenshot file
     */
    public static String takeScreenshotAndSave(WebDriver driver, String testName) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String fileName = String.format("%s_%s.png", testName, timestamp);
            Path destinationPath = Paths.get(SCREENSHOT_DIR, fileName);

            Files.copy(sourceFile.toPath(), destinationPath);

            String screenshotPath = destinationPath.toString();
            logger.info("Screenshot saved: " + screenshotPath);
            return screenshotPath;
        } catch (Exception e) {
            logger.severe("Failed to save screenshot: " + e.getMessage());
            return "";
        }
    }

    /**
     * Take screenshot on failure and save to file
     * @param driver WebDriver instance
     * @param testName Test name for filename
     * @return Path to saved screenshot file
     */
    public static String takeScreenshotOnFailure(WebDriver driver, String testName) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String fileName = String.format("FAILED_%s_%s.png", testName, timestamp);
            Path destinationPath = Paths.get(SCREENSHOT_DIR, fileName);

            Files.copy(sourceFile.toPath(), destinationPath);

            String screenshotPath = destinationPath.toString();
            logger.info("Failure screenshot saved: " + screenshotPath);
            return screenshotPath;
        } catch (Exception e) {
            logger.severe("Failed to save failure screenshot: " + e.getMessage());
            return "";
        }
    }

    /**
     * Get screenshot directory path
     * @return Screenshot directory path
     */
    public static String getScreenshotDirectory() {
        return SCREENSHOT_DIR;
    }

    /**
     * Clean up old screenshots (older than specified days)
     * @param daysToKeep Number of days to keep screenshots
     */
    public static void cleanupOldScreenshots(int daysToKeep) {
        try {
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotPath)) {
                return;
            }

            long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L);

            Files.list(screenshotPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".png"))
                    .filter(path -> {
                        try {
                            return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                        } catch (IOException e) {
                            logger.warning("Failed to get last modified time for file: " + path);
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            logger.info("Deleted old screenshot: " + path);
                        } catch (IOException e) {
                            logger.warning("Failed to delete old screenshot: " + path + ", Error: " + e.getMessage());
                        }
                    });

            logger.info("Completed cleanup of screenshots older than " + daysToKeep + " days");
        } catch (IOException e) {
            logger.severe("Failed to cleanup old screenshots: " + e.getMessage());
        }
    }
}