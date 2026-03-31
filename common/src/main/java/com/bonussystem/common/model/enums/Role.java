package com.bonussystem.common.model.enums;

public enum Role {
    ADMIN("admin"),
    HR_MANAGER("hr_manager"),
    LABOR_ECONOMIST("labor_economist"),
    DEPARTMENT_HEAD("department_head");

    private final String dbValue;

    Role(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static Role fromDbValue(String value) {
        for (Role role : values()) {
            if (role.dbValue.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Неизвестная роль: " + value);
    }
}