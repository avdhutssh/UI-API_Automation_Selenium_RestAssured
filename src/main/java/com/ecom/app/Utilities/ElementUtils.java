package com.ecom.app.Utilities;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class ElementUtils extends BasePage {
    
    protected Actions actions;

    protected ElementUtils(WebDriver driver) {
        super(driver);
        this.actions = new Actions(driver);
        logger.info("ElementUtils initialized with Actions");
    }

    protected void clickOnElement(By locator) {
        WebElement element = waitForElementToBeClickable(locator);
        try {
            element.click();
            logger.info("Clicked element: " + locator);
        } catch (Exception e) {
            logger.warning("Normal click failed, trying JS click: " + locator);
            clickOnElementJS(locator);
        }
    }

    protected void clickOnElementJS(By locator) {
        try {
            WebElement element = waitForElementToBePresent(locator);
            jsExecutor.executeScript("arguments[0].click()", element);
            logger.info("JS clicked element: " + locator);
        } catch (Exception e) {
            logger.severe("JS click failed: " + locator + " - " + e.getMessage());
        }
    }

    protected void clickOnElementAction(By locator) {
        WebElement element = waitForElementToBeVisible(locator);
        actions.moveToElement(element).click().build().perform();
        logger.info("Action clicked element: " + locator);
    }

    protected void enterText(By locator, String text) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            element.sendKeys(text);
            logger.info("Entered text in: " + locator);
        } catch (Exception e) {
            logger.severe("Failed to enter text in: " + locator + " - " + e.getMessage());
        }
    }

    protected void enterText(By locator, String text, boolean clear) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            if (clear) {
                element.clear();
                logger.info("Cleared text field: " + locator);
            }
            element.sendKeys(text);
            logger.info("Entered text in: " + locator + " (clear: " + clear + ")");
        } catch (Exception e) {
            logger.severe("Failed to enter text in: " + locator + " - " + e.getMessage());
        }
    }

    protected void enterText(By locator, String text, boolean clear, boolean pressEnter) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            if (clear) {
                element.clear();
            }
            element.sendKeys(text);
            if (pressEnter) {
                element.sendKeys(Keys.ENTER);
                logger.info("Entered text and pressed Enter: " + locator);
            } else {
                logger.info("Entered text: " + locator);
            }
        } catch (Exception e) {
            logger.severe("Failed to enter text: " + locator + " - " + e.getMessage());
        }
    }

    protected void enterCharacterByCharacter(By locator, String text, int delayMs) {
        WebElement element = waitForElementToBeVisible(locator);
        element.clear();
        
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warning("Sleep interrupted while typing slowly");
            }
        }
        logger.info("Entered text character by character: " + locator + " (delay: " + delayMs + "ms)");
    }

    protected void selectDropdownOption(By locator, String type, String selectValue) {
        WebElement element = waitForElementToBeVisible(locator);
        Select ddl = new Select(element);
        try {
            switch (type) {
                case "text":
                    ddl.selectByVisibleText(selectValue);
                    break;
                case "value":
                    ddl.selectByValue(selectValue);
                    break;
                case "index":
                    ddl.selectByIndex(Integer.parseInt(selectValue));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid selection type: " + type);
            }
            logger.info("Selected dropdown option: " + selectValue + " by " + type);
        } catch (Exception e) {
            logger.severe("Failed to select dropdown option: " + selectValue + " - " + e.getMessage());
        }
    }

    protected void pressKey(By locator, Keys key) {
        WebElement element = waitForElementToBeVisible(locator);
        element.sendKeys(key);
        logger.info("Pressed key " + key.name() + " on element: " + locator);
    }

    protected void pressKeyWithActions(Keys key) {
        actions.sendKeys(key).perform();
        logger.info("Pressed key using Actions: " + key.name());
    }

    protected void performDragAndDrop(By sourceLocator, By targetLocator) {
        WebElement source = waitForElementToBeVisible(sourceLocator);
        WebElement target = waitForElementToBeVisible(targetLocator);
        actions.dragAndDrop(source, target).perform();
        logger.info("Performed drag and drop from: " + sourceLocator + " to: " + targetLocator);
    }

    protected void hoverOverElement(By locator) {
        WebElement element = waitForElementToBeVisible(locator);
        actions.moveToElement(element).perform();
        logger.info("Hovered over element: " + locator);
    }

    protected void hoverOverElementJS(By locator) {
        WebElement element = waitForElementToBeVisible(locator);
        String script = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover', \n"
                + "\n"
                + "true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
        jsExecutor.executeScript(script, element);
        logger.info("JS hovered over element: " + locator);
    }

    protected void doubleClick(By locator) {
        WebElement element = waitForElementToBeClickable(locator);
        actions.doubleClick(element).perform();
        logger.info("Double clicked element: " + locator);
    }

    protected void rightClick(By locator) {
        WebElement element = waitForElementToBeClickable(locator);
        actions.contextClick(element).perform();
        logger.info("Right clicked element: " + locator);
    }

    protected String getText(By locator) {
        WebElement element = waitForElementToBeVisible(locator);
        String text = element.getText().trim();
        logger.info("Retrieved text from: " + locator + " -> '" + text + "'");
        return text;
    }

    protected String getAttribute(By locator, String attributeName) {
        WebElement element = waitForElementToBeVisible(locator);
        String attributeValue = element.getAttribute(attributeName);
        logger.info("Retrieved attribute '" + attributeName + "' from: " + locator + " -> '" + attributeValue + "'");
        return attributeValue;
    }

    protected boolean isElementDisplayed(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            boolean isDisplayed = element.isDisplayed();
            logger.info("Element displayed check: " + locator + " -> " + isDisplayed);
            return isDisplayed;
        } catch (NoSuchElementException e) {
            logger.info("Element not found: " + locator + " -> false");
            return false;
        }
    }

    protected boolean isElementEnabled(By locator) {
        WebElement element = waitForElementToBePresent(locator);
        boolean isEnabled = element.isEnabled();
        logger.info("Element enabled check: " + locator + " -> " + isEnabled);
        return isEnabled;
    }

    protected void clearText(By locator) {
        WebElement element = waitForElementToBeVisible(locator);
        element.clear();
        logger.info("Cleared text from: " + locator);
    }

    protected void scrollIntoView(By locator) {
        WebElement element = waitForElementToBePresent(locator);
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
        logger.info("Scrolled element into view: " + locator);
    }

    protected int getElementCount(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        int count = elements.size();
        logger.info("Element count for: " + locator + " -> " + count);
        return count;
    }

    protected List<WebElement> getElements(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        logger.info("Retrieved " + elements.size() + " elements for: " + locator);
        return elements;
    }
} 