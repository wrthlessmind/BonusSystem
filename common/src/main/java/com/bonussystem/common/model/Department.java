package com.bonussystem.common.model;

import java.io.Serial;
import java.io.Serializable;

public class Department implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int departmentId;
    private String name;
    private Integer headUserId;

    public Department() {}

    public Department(int departmentId, String name, Integer headUserId) {
        this.departmentId = departmentId;
        this.name = name;
        this.headUserId = headUserId;
    }

    public int getDepartmentId() { return departmentId; }
    public String getName() { return name; }
    public Integer getHeadUserId() { return headUserId; }

    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public void setName(String name) { this.name = name; }
    public void setHeadUserId(Integer headUserId) { this.headUserId = headUserId; }

    @Override
    public String toString() {
        return "Department{" +
                "departmentId=" + departmentId +
                ", name='" + name + '\'' +
                ", headUserId=" + headUserId +
                '}';
    }
}