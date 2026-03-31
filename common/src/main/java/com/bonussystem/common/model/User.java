package com.bonussystem.common.model;

import com.bonussystem.common.model.enums.Role;
import com.bonussystem.common.model.enums.UserStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int userId;
    private String login;
    private String password;
    private Role role;
    private UserStatus status;
    private int failedAttempts;
    private LocalDateTime lastFailedTime;

    public User() {}

    public User(String login, String password, Role role) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.failedAttempts = 0;
    }

    public User(int userId, String login, String password, Role role, UserStatus status, int failedAttempts, LocalDateTime lastFailedTime) {
        this.userId = userId;
        this.login = login;
        this.password = password;
        this.role = role;
        this.status = status;
        this.failedAttempts = failedAttempts;
        this.lastFailedTime = lastFailedTime;
    }

    public int getUserId() { return userId; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public int getFailedAttempts() { return failedAttempts; }
    public LocalDateTime getLastFailedTime() { return lastFailedTime; }

    public void setUserId(int userId) { this.userId = userId; }
    public void setLogin(String login) { this.login = login; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
    public void setStatus(UserStatus status) { this.status = status; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }
    public void setLastFailedTime(LocalDateTime lastFailedTime) { this.lastFailedTime = lastFailedTime; }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", login='" + login + '\'' +
                ", role=" + role +
                ", status=" + status +
                '}';
    }
}