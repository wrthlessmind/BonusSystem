package com.bonussystem.server.dao;

import com.bonussystem.common.model.User;
import com.bonussystem.common.model.enums.Role;
import com.bonussystem.common.model.enums.UserStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class UserDao extends BaseDao<User> {

    @Override
    protected User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        user.setRole(Role.fromDbValue(rs.getString("role")));
        user.setStatus(UserStatus.fromDbValue(rs.getString("status")));
        user.setFailedAttempts(rs.getInt("failed_attempts"));
        Timestamp ts = rs.getTimestamp("last_failed_time");
        user.setLastFailedTime(ts != null ? ts.toLocalDateTime() : null);
        return user;
    }

    public List<User> findAll() {
        return executeQuery("SELECT * FROM users");
    }

    public List<User> findByRole(Role role) {
        return executeQuery("SELECT * FROM users WHERE role = ?", role.getDbValue());
    }

    public User findById(int userId) {
        return executeQuerySingle("SELECT * FROM users WHERE user_id = ?", userId);
    }

    public User findByLogin(String login) {
        return executeQuerySingle("SELECT * FROM users WHERE login = ?", login);
    }

    public int insert(User user) {
        return executeInsertAndGetKey(
                "INSERT INTO users (login, password, role, status, failed_attempts, last_failed_time) VALUES (?, ?, ?, ?, ?, ?)",
                user.getLogin(), user.getPassword(), user.getRole().getDbValue(),
                user.getStatus().getDbValue(), user.getFailedAttempts(),
                user.getLastFailedTime() != null ? Timestamp.valueOf(user.getLastFailedTime()) : null
        );
    }

    public void update(User user) {
        executeUpdate(
                "UPDATE users SET login = ?, password = ?, role = ?, status = ?, failed_attempts = ?, last_failed_time = ? WHERE user_id = ?",
                user.getLogin(), user.getPassword(), user.getRole().getDbValue(),
                user.getStatus().getDbValue(), user.getFailedAttempts(),
                user.getLastFailedTime() != null ? Timestamp.valueOf(user.getLastFailedTime()) : null,
                user.getUserId()
        );
    }

    public void delete(int userId) {
        executeUpdate("DELETE FROM users WHERE user_id = ?", userId);
    }
}