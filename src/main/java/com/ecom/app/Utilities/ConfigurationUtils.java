package com.ecom.app.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class ConfigurationUtils {

    private static final Logger log = Logger.getLogger(ConfigurationUtils.class.getName());
    private static final Properties prop = new Properties();
    private static final String CONFIG_FILE_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config.properties";
    private static ConfigurationUtils instance = null;

    private ConfigurationUtils() {
        loadProperties();
    }

    public static synchronized ConfigurationUtils getInstance() {
        if (instance == null) {
            instance = new ConfigurationUtils();
        }
        return instance;
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }

    private void loadProperties() {
        try (FileInputStream fileInput = new FileInputStream(CONFIG_FILE_PATH)) {
            prop.load(fileInput);
            log.info("Successfully loaded properties from: " + CONFIG_FILE_PATH);
        } catch (IOException e) {
            log.severe("Failed to load properties file: " + e.getMessage());
        }
    }

    public static String getPropertyStatic(String key) {
        return getInstance().getProperty(key);
    }

    public static String getPropertyStatic(String key, String defaultValue) {
        return getInstance().getProperty(key, defaultValue);
    }
}