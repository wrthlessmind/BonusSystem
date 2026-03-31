package com.bonussystem.server.service;

import com.bonussystem.common.model.Employee;
import com.bonussystem.server.dao.DaoFactory;
import com.bonussystem.server.dao.EmployeeDao;

import java.util.List;

public class EmployeeService {

    private final EmployeeDao employeeDao = DaoFactory.getEmployeeDao();

    public List<Employee> getAllEmployees() { return employeeDao.findAll(); }

    public Employee getEmployeeById(int id) { return employeeDao.findById(id); }

    public Employee getEmployeeByUserId(int userId) { return employeeDao.findByUserId(userId); }

    public List<Employee> getEmployeesByDepartment(int departmentId) { return employeeDao.findByDepartmentId(departmentId); }

    public int addEmployee(Employee emp) { return employeeDao.insert(emp); }

    public void updateEmployee(Employee emp) {
        if (employeeDao.findById(emp.getEmployeeId()) == null) {
            throw new RuntimeException("Сотрудник не найден");
        }
        employeeDao.update(emp);
    }

    public void deleteEmployee(int id) {
        if (employeeDao.findById(id) == null) {
            throw new RuntimeException("Сотрудник не найден");
        }
        employeeDao.delete(id);
    }
}