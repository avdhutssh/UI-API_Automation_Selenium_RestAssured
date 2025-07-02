package com.ecom.app.Utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for reading JSON test data files
 */
public class JSONDataReader {

    private static final Logger logger = Logger.getLogger(JSONDataReader.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Read JSON file and return as Map
     * @param filePath Path to JSON file
     * @return Map containing JSON data
     */
    public static Map<String, Object> readJsonAsMap(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.severe("JSON file not found: " + filePath);
                return new HashMap<>();
            }

            Map<String, Object> jsonData = objectMapper.readValue(file, Map.class);
            logger.info("JSON file read successfully: " + filePath);
            return jsonData;
        } catch (IOException e) {
            logger.severe("Error reading JSON file: " + filePath + ", Error: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Read JSON file and return as List of Maps (for test data arrays)
     * @param filePath Path to JSON file
     * @return List of Maps containing JSON data
     */
    public static List<Map<String, Object>> readJsonAsListOfMaps(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.severe("JSON file not found: " + filePath);
                return new ArrayList<>();
            }

            List<Map<String, Object>> jsonData = objectMapper.readValue(file, List.class);
            logger.info("JSON file read successfully as list: " + filePath);
            return jsonData;
        } catch (IOException e) {
            logger.severe("Error reading JSON file as list: " + filePath + ", Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Read JSON file and return as specific POJO class
     * @param filePath Path to JSON file
     * @param clazz Target class type
     * @param <T> Generic type
     * @return Object of specified type
     */
    public static <T> T readJsonAsObject(String filePath, Class<T> clazz) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.severe("JSON file not found: " + filePath);
                return null;
            }

            T jsonObject = objectMapper.readValue(file, clazz);
            logger.info("JSON file read successfully as object: " + filePath);
            return jsonObject;
        } catch (IOException e) {
            logger.severe("Error reading JSON file as object: " + filePath + ", Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Read JSON file content as string
     * @param filePath Path to JSON file
     * @return JSON content as string
     */
    public static String readJsonAsString(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            logger.info("JSON file read as string: " + filePath);
            return content;
        } catch (IOException e) {
            logger.severe("Error reading JSON file as string: " + filePath + ", Error: " + e.getMessage());
            return "";
        }
    }

    /**
     * Get specific value from JSON file using JSON path
     * @param filePath Path to JSON file
     * @param jsonPath JSON path (e.g., "user.name")
     * @return Value at specified path
     */
    public static Object getValueFromJsonPath(String filePath, String jsonPath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.severe("JSON file not found: " + filePath);
                return null;
            }

            JsonNode rootNode = objectMapper.readTree(file);
            String[] pathParts = jsonPath.split("\\.");
            JsonNode currentNode = rootNode;

            for (String part : pathParts) {
                if (currentNode.isArray()) {
                    try {
                        int index = Integer.parseInt(part);
                        currentNode = currentNode.get(index);
                    } catch (NumberFormatException e) {
                        logger.warning("Invalid array index in path: " + part);
                        return null;
                    }
                } else {
                    currentNode = currentNode.get(part);
                }

                if (currentNode == null) {
                    logger.warning("Path not found in JSON: " + jsonPath);
                    return null;
                }
            }

            return currentNode.isTextual() ? currentNode.asText() : currentNode;
        } catch (IOException e) {
            logger.severe("Error reading JSON path: " + jsonPath + " from file: " + filePath + ", Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convert object to JSON string
     * @param object Object to convert
     * @return JSON string representation
     */
    public static String objectToJsonString(Object object) {
        try {
            String jsonString = objectMapper.writeValueAsString(object);
            logger.info("Object converted to JSON string successfully");
            return jsonString;
        } catch (IOException e) {
            logger.severe("Error converting object to JSON string: " + e.getMessage());
            return "";
        }
    }

    /**
     * Convert object to formatted JSON string
     * @param object Object to convert
     * @return Formatted JSON string representation
     */
    public static String objectToFormattedJsonString(Object object) {
        try {
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            logger.info("Object converted to formatted JSON string successfully");
            return jsonString;
        } catch (IOException e) {
            logger.severe("Error converting object to formatted JSON string: " + e.getMessage());
            return "";
        }
    }
}