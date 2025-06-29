package com.ecom.app.Utilities;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

public class CsvDataProviders {

    static Logger logger = Logger.getLogger(CsvDataProviders.class.getName());

    @DataProvider(name = "csvFileReader")
    public static Iterator<Object[]> csvReader(Method method) {
        logger.info("Reading data from CSV file for method: " + method.getName());
        List<Object[]> list = new ArrayList<Object[]>();
        String pathName = "src" + File.separator + "test" + File.separator + "resources" + File.separator
                + "TestData" + File.separator + method.getDeclaringClass().getSimpleName() + File.separator
                + method.getName() + ".csv";
        File file = new File(pathName);
        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            String[] keys = reader.readNext();
            if (keys != null) {
                String[] dataParts;
                while ((dataParts = reader.readNext()) != null) {
                    Map<String, String> testData = new HashMap<>();
                    for (int i = 0; i < keys.length; i++) {
                        testData.put(keys[i], dataParts[i]);
                    }
                    list.add(new Object[]{testData});
                }
            }
            reader.close();
            logger.info("CSV file read successfully: " + pathName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File " + pathName + " was not found.\n" + e.getStackTrace().toString());
        } catch (IOException e) {
            throw new RuntimeException("Could not read " + pathName + " file.\n" + e.getStackTrace().toString());
        } catch (CsvValidationException e) {
            throw new RuntimeException(
                    "Could not validate CSV file " + pathName + " file.\n" + e.getStackTrace().toString());
        }
        return list.iterator();
    }
}