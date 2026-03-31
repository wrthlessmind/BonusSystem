package com.bonussystem.server.dao;

import com.bonussystem.common.model.KpiResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class KpiResultDao extends BaseDao<KpiResult> {

    @Override
    protected KpiResult mapRow(ResultSet rs) throws SQLException {
        KpiResult result = new KpiResult();
        result.setResultId(rs.getInt("result_id"));
        result.setEmployeeId(rs.getInt("employee_id"));
        result.setKpiId(rs.getInt("kpi_id"));
        result.setYear(rs.getInt("year"));
        result.setActualValue(rs.getBigDecimal("actual_value"));
        result.setNote(rs.getString("note"));
        return result;
    }

    public List<KpiResult> findAll() {
        return executeQuery("SELECT * FROM kpi_results");
    }

    public KpiResult findById(int resultId) {
        return executeQuerySingle("SELECT * FROM kpi_results WHERE result_id = ?", resultId);
    }

    public List<KpiResult> findByDepartmentAndYear(int departmentId, int year) {
        return executeQuery(
                "SELECT kr.* FROM kpi_results kr " +
                        "JOIN employees e ON kr.employee_id = e.employee_id " +
                        "WHERE e.department_id = ? AND kr.year = ?",
                departmentId, year
        );
    }

    public List<KpiResult> findByEmployeeAndYear(int employeeId, int year) {
        return executeQuery(
                "SELECT * FROM kpi_results WHERE employee_id = ? AND year = ?",
                employeeId, year
        );
    }

    public int insert(KpiResult result) {
        return executeInsertAndGetKey(
                "INSERT INTO kpi_results (employee_id, kpi_id, year, actual_value, note) VALUES (?, ?, ?, ?, ?)",
                result.getEmployeeId(), result.getKpiId(), result.getYear(),
                result.getActualValue(), result.getNote()
        );
    }

    public void update(KpiResult result) {
        executeUpdate(
                "UPDATE kpi_results SET employee_id = ?, kpi_id = ?, year = ?, actual_value = ?, note = ? WHERE result_id = ?",
                result.getEmployeeId(), result.getKpiId(), result.getYear(),
                result.getActualValue(), result.getNote(), result.getResultId()
        );
    }

    public void delete(int resultId) {
        executeUpdate("DELETE FROM kpi_results WHERE result_id = ?", resultId);
    }
}