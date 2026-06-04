package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {

    private static final String CONFIG_FILE = "/application.properties";
    private static final Properties PROPERTIES = loadProperties();

    private AppConfig() {
    }

    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream input = AppConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load " + CONFIG_FILE, e);
        }

        return properties;
    }
}
