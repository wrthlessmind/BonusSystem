package com.bonussystem.common.model.enums;

public enum PeriodStatus {
    OPEN("open"),
    CALCULATING("calculating"),
    APPROVED("approved");

    private final String dbValue;

    PeriodStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static PeriodStatus fromDbValue(String value) {
        for (PeriodStatus status : values()) {
            if (status.dbValue.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Неизвестный статус периода: " + value);
    }
}