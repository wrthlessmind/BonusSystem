package com.bonussystem.server.dao;

import com.bonussystem.common.model.KpiIndicator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class KpiIndicatorDao extends BaseDao<KpiIndicator> {

    @Override
    protected KpiIndicator mapRow(ResultSet rs) throws SQLException {
        KpiIndicator kpi = new KpiIndicator();
        kpi.setKpiId(rs.getInt("kpi_id"));
        kpi.setName(rs.getString("name"));
        kpi.setUnit(rs.getString("unit"));
        kpi.setWeight(rs.getBigDecimal("weight"));
        kpi.setTargetValue(rs.getBigDecimal("target_value"));
        return kpi;
    }

    public List<KpiIndicator> findAll() {
        return executeQuery("SELECT * FROM kpi_indicators");
    }

    public KpiIndicator findById(int kpiId) {
        return executeQuerySingle("SELECT * FROM kpi_indicators WHERE kpi_id = ?", kpiId);
    }

    public int insert(KpiIndicator kpi) {
        return executeInsertAndGetKey(
                "INSERT INTO kpi_indicators (name, unit, weight, target_value) VALUES (?, ?, ?, ?)",
                kpi.getName(), kpi.getUnit(), kpi.getWeight(), kpi.getTargetValue()
        );
    }

    public void update(KpiIndicator kpi) {
        executeUpdate(
                "UPDATE kpi_indicators SET name = ?, unit = ?, weight = ?, target_value = ? WHERE kpi_id = ?",
                kpi.getName(), kpi.getUnit(), kpi.getWeight(), kpi.getTargetValue(), kpi.getKpiId()
        );
    }

    public void delete(int kpiId) {
        executeUpdate("DELETE FROM kpi_indicators WHERE kpi_id = ?", kpiId);
    }
}