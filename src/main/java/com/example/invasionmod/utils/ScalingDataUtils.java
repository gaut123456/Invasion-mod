package com.example.invasionmod.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class ScalingDataUtils {
    private static final Logger LOGGER = LogManager.getLogger();

    private static String getFileName(String worldName) {
        return "scaling_data_" + worldName + ".properties";
    }

    public static void saveScalingData(String worldName, int mobsToSpawn) {
        Properties properties = new Properties();
        properties.setProperty("mobsToSpawn", String.valueOf(mobsToSpawn));

        try (OutputStream output = new FileOutputStream(getFileName(worldName))) {
            properties.store(output, null);
            LOGGER.info("Scaling data saved for world {}: mobsToSpawn = {}", worldName, mobsToSpawn);
        } catch (IOException io) {
            LOGGER.error("Error saving scaling data for world {}", worldName, io);
        }
    }

    public static int loadScalingData(String worldName) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(getFileName(worldName))) {
            properties.load(input);
            int mobsToSpawn = Integer.parseInt(properties.getProperty("mobsToSpawn", "1"));
            LOGGER.info("Scaling data loaded for world {}: mobsToSpawn = {}", worldName, mobsToSpawn);
            return mobsToSpawn;
        } catch (IOException io) {
            LOGGER.error("Error loading scaling data for world {}", worldName, io);
            return 1; // Default value if file not found or error occurs
        }
    }
}