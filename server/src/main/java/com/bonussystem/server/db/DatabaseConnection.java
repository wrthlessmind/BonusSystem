package com.bonussystem.server.db;

import com.bonussystem.server.config.ServerConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final ThreadLocal<Connection> threadLocal = ThreadLocal.withInitial(() -> {
        try {
            ServerConfig config = ServerConfig.getInstance();
            return DriverManager.getConnection(
                    config.getDbUrl(),
                    config.getDbUser(),
                    config.getDbPassword()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к базе данных", e);
        }
    });

    private DatabaseConnection() {}

    public static Connection getConnection() {
        return threadLocal.get();
    }

    public static void closeForCurrentThread() {
        Connection conn = threadLocal.get();
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка закрытия соединения", e);
        } finally {
            threadLocal.remove();
        }
    }
}