package com.bonussystem.server.dao;

import com.bonussystem.common.model.Employee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EmployeeDao extends BaseDao<Employee> {

    @Override
    protected Employee mapRow(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setEmployeeId(rs.getInt("employee_id"));
        emp.setFirstName(rs.getString("first_name"));
        emp.setLastName(rs.getString("last_name"));
        emp.setPosition(rs.getString("position"));
        emp.setBaseSalary(rs.getBigDecimal("base_salary"));
        int deptId = rs.getInt("department_id");
        emp.setDepartmentId(rs.wasNull() ? null : deptId);
        int userId = rs.getInt("user_id");
        emp.setUserId(rs.wasNull() ? null : userId);
        return emp;
    }

    public List<Employee> findAll() {
        return executeQuery("SELECT * FROM employees");
    }

    public Employee findById(int employeeId) {
        return executeQuerySingle("SELECT * FROM employees WHERE employee_id = ?", employeeId);
    }

    public Employee findByUserId(int userId) {
        return executeQuerySingle("SELECT * FROM employees WHERE user_id = ?", userId);
    }

    public List<Employee> findByDepartmentId(int departmentId) {
        return executeQuery("SELECT * FROM employees WHERE department_id = ?", departmentId);
    }

    public int insert(Employee emp) {
        return executeInsertAndGetKey(
                "INSERT INTO employees (first_name, last_name, position, base_salary, department_id, user_id) VALUES (?, ?, ?, ?, ?, ?)",
                emp.getFirstName(), emp.getLastName(), emp.getPosition(),
                emp.getBaseSalary(), emp.getDepartmentId(), emp.getUserId()
        );
    }

    public void update(Employee emp) {
        executeUpdate(
                "UPDATE employees SET first_name = ?, last_name = ?, position = ?, base_salary = ?, department_id = ?, user_id = ? WHERE employee_id = ?",
                emp.getFirstName(), emp.getLastName(), emp.getPosition(),
                emp.getBaseSalary(), emp.getDepartmentId(), emp.getUserId(), emp.getEmployeeId()
        );
    }

    public void delete(int employeeId) {
        executeUpdate("DELETE FROM employees WHERE employee_id = ?", employeeId);
    }
}