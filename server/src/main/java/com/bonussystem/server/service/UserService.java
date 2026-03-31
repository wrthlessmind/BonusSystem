package com.bonussystem.server.service;

import com.bonussystem.common.model.Employee;
import com.bonussystem.common.model.User;
import com.bonussystem.common.model.enums.Role;
import com.bonussystem.common.model.enums.UserStatus;
import com.bonussystem.common.util.HashUtil;
import com.bonussystem.server.dao.DaoFactory;
import com.bonussystem.server.dao.EmployeeDao;
import com.bonussystem.server.dao.UserDao;
import com.bonussystem.server.db.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class UserService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserDao userDao = DaoFactory.getUserDao();
    private final EmployeeDao employeeDao = DaoFactory.getEmployeeDao();

    public User login(String login, String password) {
        User user = userDao.findByLogin(login);
        if (user == null) {
            throw new RuntimeException("Пользователь с таким логином не найден");
        }
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new RuntimeException("Учётная запись заблокирована. Обратитесь к администратору");
        }
        if (!HashUtil.verify(user.getPassword(), password)) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            user.setLastFailedTime(LocalDateTime.now());
            if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setStatus(UserStatus.BLOCKED);
            }
            userDao.update(user);
            throw new RuntimeException("Неверный пароль");
        }
        if (user.getFailedAttempts() > 0) {
            user.setFailedAttempts(0);
            user.setLastFailedTime(null);
            userDao.update(user);
        }
        return user;
    }

    public User register(String login, String password, Role role, String firstName, String lastName) {
        if (userDao.findByLogin(login) != null) {
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }

        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            User user = new User();
            user.setLogin(login);
            user.setPassword(HashUtil.hash(password));
            user.setRole(role);
            user.setStatus(UserStatus.ACTIVE);
            user.setFailedAttempts(0);
            int userId = userDao.insert(user);
            user.setUserId(userId);

            Employee employee = new Employee();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setPosition(mapRoleToPosition(role));
            employee.setBaseSalary(mapRoleToBaseSalary(role));
            employee.setDepartmentId(null);
            employee.setUserId(userId);
            employeeDao.insert(employee);

            conn.commit();
            return user;
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Ошибка отката транзакции при регистрации", ex);
            }
            throw new RuntimeException("Ошибка регистрации: " + e.getMessage(), e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка восстановления autoCommit", e);
            }
        }
    }

    private String mapRoleToPosition(Role role) {
        switch (role) {
            case HR_MANAGER: return "HR-менеджер";
            case LABOR_ECONOMIST: return "Экономист по труду";
            case DEPARTMENT_HEAD: return "Руководитель отдела";
            default: return "Сотрудник";
        }
    }

    private BigDecimal mapRoleToBaseSalary(Role role) {
        switch (role) {
            case HR_MANAGER: return new BigDecimal("1800.00");
            case LABOR_ECONOMIST: return new BigDecimal("2000.00");
            case DEPARTMENT_HEAD: return new BigDecimal("2500.00");
            default: return new BigDecimal("1500.00");
        }
    }

    public List<User> getAllUsers() { return userDao.findAll(); }

    public List<User> getUsersByRole(Role role) { return userDao.findByRole(role); }

    public User getUserById(int userId) { return userDao.findById(userId); }

    public void updateUser(User user) { userDao.update(user); }

    public void blockUser(int userId) {
        User user = userDao.findById(userId);
        if (user == null) throw new RuntimeException("Пользователь не найден");
        user.setStatus(UserStatus.BLOCKED);
        userDao.update(user);
    }

    public void unblockUser(int userId) {
        User user = userDao.findById(userId);
        if (user == null) throw new RuntimeException("Пользователь не найден");
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedAttempts(0);
        user.setLastFailedTime(null);
        userDao.update(user);
    }

    public void deleteUser(int userId) { userDao.delete(userId); }

    public void updateProfile(int userId, String newLogin, String newPassword) {
        User user = userDao.findById(userId);
        if (user == null) throw new RuntimeException("Пользователь не найден");
        if (newLogin != null && !newLogin.isEmpty() && !newLogin.equals(user.getLogin())) {
            if (userDao.findByLogin(newLogin) != null) {
                throw new RuntimeException("Логин уже занят");
            }
            user.setLogin(newLogin);
        }
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(HashUtil.hash(newPassword));
        }
        userDao.update(user);
    }
}