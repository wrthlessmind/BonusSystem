package com.bonussystem.server.service;

import com.bonussystem.common.model.Department;
import com.bonussystem.server.dao.DaoFactory;
import com.bonussystem.server.dao.DepartmentDao;

import java.util.List;

public class DepartmentService {

    private final DepartmentDao departmentDao = DaoFactory.getDepartmentDao();

    public List<Department> getAllDepartments() { return departmentDao.findAll(); }

    public Department getDepartmentById(int id) { return departmentDao.findById(id); }

    public Department getDepartmentByHeadUserId(int headUserId) { return departmentDao.findByHeadUserId(headUserId); }

    public int addDepartment(Department dept) { return departmentDao.insert(dept); }

    public void updateDepartment(Department dept) {
        if (departmentDao.findById(dept.getDepartmentId()) == null) {
            throw new RuntimeException("Отдел не найден");
        }
        departmentDao.update(dept);
    }

    public void deleteDepartment(int id) {
        if (departmentDao.findById(id) == null) {
            throw new RuntimeException("Отдел не найден");
        }
        departmentDao.delete(id);
    }
}