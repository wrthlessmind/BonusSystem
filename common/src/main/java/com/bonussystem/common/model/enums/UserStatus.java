package com.bonussystem.common.model.enums;

public enum UserStatus {
    ACTIVE("active"),
    BLOCKED("blocked");

    private final String dbValue;

    UserStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static UserStatus fromDbValue(String value) {
        for (UserStatus status : values()) {
            if (status.dbValue.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Неизвестный статус пользователя: " + value);
    }
}