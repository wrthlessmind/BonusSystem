package com.bonussystem.common.model;

import com.bonussystem.common.model.enums.PeriodStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class Period implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int year;
    private BigDecimal bonusFund;
    private PeriodStatus status;

    public Period() {}

    public Period(int year, BigDecimal bonusFund, PeriodStatus status) {
        this.year = year;
        this.bonusFund = bonusFund;
        this.status = status;
    }

    public int getYear() { return year; }
    public BigDecimal getBonusFund() { return bonusFund; }
    public PeriodStatus getStatus() { return status; }

    public void setYear(int year) { this.year = year; }
    public void setBonusFund(BigDecimal bonusFund) { this.bonusFund = bonusFund; }
    public void setStatus(PeriodStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Period{" +
                "year=" + year +
                ", bonusFund=" + bonusFund +
                ", status=" + status +
                '}';
    }
}