package com.bonussystem.common.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class KpiIndicator implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int kpiId;
    private String name;
    private String unit;
    private BigDecimal weight;
    private BigDecimal targetValue;

    public KpiIndicator() {}

    public KpiIndicator(int kpiId, String name, String unit, BigDecimal weight, BigDecimal targetValue) {
        this.kpiId = kpiId;
        this.name = name;
        this.unit = unit;
        this.weight = weight;
        this.targetValue = targetValue;
    }

    public int getKpiId() { return kpiId; }
    public String getName() { return name; }
    public String getUnit() { return unit; }
    public BigDecimal getWeight() { return weight; }
    public BigDecimal getTargetValue() { return targetValue; }

    public void setKpiId(int kpiId) { this.kpiId = kpiId; }
    public void setName(String name) { this.name = name; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public void setTargetValue(BigDecimal targetValue) { this.targetValue = targetValue; }

    @Override
    public String toString() {
        return "KpiIndicator{" +
                "kpiId=" + kpiId +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", weight=" + weight +
                ", targetValue=" + targetValue +
                '}';
    }
}