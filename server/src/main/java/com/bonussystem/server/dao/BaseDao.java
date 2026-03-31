package com.bonussystem.server.dao;

import com.bonussystem.server.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDao<T> {

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected List<T> executeQuery(String sql, Object... params) {
        List<T> result = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения запроса: " + sql, e);
        }
        return result;
    }

    protected T executeQuerySingle(String sql, Object... params) {
        List<T> list = executeQuery(sql, params);
        return list.isEmpty() ? null : list.get(0);
    }

    protected int executeUpdate(String sql, Object... params) {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            setParameters(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения обновления: " + sql, e);
        }
    }

    protected int executeInsertAndGetKey(String sql, Object... params) {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new RuntimeException("Не удалось получить сгенерированный ключ");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения вставки: " + sql, e);
        }
    }

    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                ps.setNull(i + 1, Types.NULL);
            } else {
                ps.setObject(i + 1, params[i]);
            }
        }
    }
}