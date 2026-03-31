package com.bonussystem.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerConfig {

    private static ServerConfig instance;
    private final Properties properties;

    private ServerConfig() {
        properties = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("server.properties")) {
            if (is == null) {
                throw new RuntimeException("Файл server.properties не найден в resources");
            }
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения server.properties", e);
        }
    }

    public static synchronized ServerConfig getInstance() {
        if (instance == null) {
            instance = new ServerConfig();
        }
        return instance;
    }

    public int getServerPort() { return Integer.parseInt(properties.getProperty("server.port", "5555")); }
    public String getDbUrl() { return properties.getProperty("db.url"); }
    public String getDbUser() { return properties.getProperty("db.user"); }
    public String getDbPassword() { return properties.getProperty("db.password"); }
}