package com.bonussystem.common.model;

import com.bonussystem.common.model.enums.CalculationStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class BonusCalculation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int calculationId;
    private int employeeId;
    private int year;
    private BigDecimal kpiScore;
    private BigDecimal bonusAmount;
    private CalculationStatus status;
    private String comment;

    public BonusCalculation() {}

    public BonusCalculation(int calculationId, int employeeId, int year, BigDecimal kpiScore, BigDecimal bonusAmount, CalculationStatus status, String comment) {
        this.calculationId = calculationId;
        this.employeeId = employeeId;
        this.year = year;
        this.kpiScore = kpiScore;
        this.bonusAmount = bonusAmount;
        this.status = status;
        this.comment = comment;
    }

    public int getCalculationId() { return calculationId; }
    public int getEmployeeId() { return employeeId; }
    public int getYear() { return year; }
    public BigDecimal getKpiScore() { return kpiScore; }
    public BigDecimal getBonusAmount() { return bonusAmount; }
    public CalculationStatus getStatus() { return status; }
    public String getComment() { return comment; }

    public void setCalculationId(int calculationId) { this.calculationId = calculationId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public void setYear(int year) { this.year = year; }
    public void setKpiScore(BigDecimal kpiScore) { this.kpiScore = kpiScore; }
    public void setBonusAmount(BigDecimal bonusAmount) { this.bonusAmount = bonusAmount; }
    public void setStatus(CalculationStatus status) { this.status = status; }
    public void setComment(String comment) { this.comment = comment; }

    @Override
    public String toString() {
        return "BonusCalculation{" +
                "calculationId=" + calculationId +
                ", employeeId=" + employeeId +
                ", year=" + year +
                ", kpiScore=" + kpiScore +
                ", bonusAmount=" + bonusAmount +
                ", status=" + status +
                '}';
    }
}