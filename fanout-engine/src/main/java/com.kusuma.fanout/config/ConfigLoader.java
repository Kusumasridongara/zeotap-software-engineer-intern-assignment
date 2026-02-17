package com.kusuma.fanout.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class ConfigLoader {

    public static AppConfig load() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream input = ConfigLoader.class
                    .getClassLoader()
                    .getResourceAsStream("config.json");

            return mapper.readValue(input, AppConfig.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }
}
