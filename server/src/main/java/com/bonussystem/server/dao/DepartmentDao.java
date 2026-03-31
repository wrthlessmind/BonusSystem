package com.bonussystem.server.dao;

import com.bonussystem.common.model.Department;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DepartmentDao extends BaseDao<Department> {

    @Override
    protected Department mapRow(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setDepartmentId(rs.getInt("department_id"));
        dept.setName(rs.getString("name"));
        int headUserId = rs.getInt("head_user_id");
        dept.setHeadUserId(rs.wasNull() ? null : headUserId);
        return dept;
    }

    public List<Department> findAll() {
        return executeQuery("SELECT * FROM departments");
    }

    public Department findById(int departmentId) {
        return executeQuerySingle("SELECT * FROM departments WHERE department_id = ?", departmentId);
    }

    public Department findByHeadUserId(int headUserId) {
        return executeQuerySingle("SELECT * FROM departments WHERE head_user_id = ?", headUserId);
    }

    public int insert(Department dept) {
        return executeInsertAndGetKey(
                "INSERT INTO departments (name, head_user_id) VALUES (?, ?)",
                dept.getName(), dept.getHeadUserId()
        );
    }

    public void update(Department dept) {
        executeUpdate(
                "UPDATE departments SET name = ?, head_user_id = ? WHERE department_id = ?",
                dept.getName(), dept.getHeadUserId(), dept.getDepartmentId()
        );
    }

    public void delete(int departmentId) {
        executeUpdate("DELETE FROM departments WHERE department_id = ?", departmentId);
    }
}