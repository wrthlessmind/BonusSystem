package com.bonussystem.common.model.enums;

public enum CalculationStatus {
    CALCULATED("calculated"),
    APPROVED("approved"),
    REJECTED("rejected");

    private final String dbValue;

    CalculationStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static CalculationStatus fromDbValue(String value) {
        for (CalculationStatus status : values()) {
            if (status.dbValue.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Неизвестный статус расчёта: " + value);
    }
}