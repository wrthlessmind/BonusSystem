package com.bonussystem.common.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class Employee implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int employeeId;
    private String firstName;
    private String lastName;
    private String position;
    private BigDecimal baseSalary;
    private Integer departmentId;
    private Integer userId;

    public Employee() {}

    public Employee(int employeeId, String firstName, String lastName, String position,
                    BigDecimal baseSalary, Integer departmentId, Integer userId) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.baseSalary = baseSalary;
        this.departmentId = departmentId;
        this.userId = userId;
    }

    public int getEmployeeId() { return employeeId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPosition() { return position; }
    public BigDecimal getBaseSalary() { return baseSalary; }
    public Integer getDepartmentId() { return departmentId; }
    public Integer getUserId() { return userId; }

    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPosition(String position) { this.position = position; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", position='" + position + '\'' +
                ", baseSalary=" + baseSalary +
                ", departmentId=" + departmentId +
                ", userId=" + userId +
                '}';
    }
}