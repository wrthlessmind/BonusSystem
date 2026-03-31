package com.bonussystem.common.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class KpiResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int resultId;
    private int employeeId;
    private int kpiId;
    private int year;
    private BigDecimal actualValue;
    private String note;

    public KpiResult() {
    }

    public KpiResult(int resultId, int employeeId, int kpiId, int year, BigDecimal actualValue, String note) {
        this.resultId = resultId;
        this.employeeId = employeeId;
        this.kpiId = kpiId;
        this.year = year;
        this.actualValue = actualValue;
        this.note = note;
    }

    public int getResultId() { return resultId; }
    public int getEmployeeId() { return employeeId; }
    public int getKpiId() { return kpiId; }
    public int getYear() { return year; }
    public BigDecimal getActualValue() { return actualValue; }
    public String getNote() { return note; }

    public void setResultId(int resultId) { this.resultId = resultId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public void setKpiId(int kpiId) { this.kpiId = kpiId; }
    public void setYear(int year) { this.year = year; }
    public void setActualValue(BigDecimal actualValue) { this.actualValue = actualValue; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return "KpiResult{" +
                "resultId=" + resultId +
                ", employeeId=" + employeeId +
                ", kpiId=" + kpiId +
                ", year=" + year +
                ", actualValue=" + actualValue +
                '}';
    }
}