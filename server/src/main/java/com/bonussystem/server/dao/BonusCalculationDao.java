package com.bonussystem.server.dao;

import com.bonussystem.common.model.BonusCalculation;
import com.bonussystem.common.model.enums.CalculationStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BonusCalculationDao extends BaseDao<BonusCalculation> {

    @Override
    protected BonusCalculation mapRow(ResultSet rs) throws SQLException {
        BonusCalculation calc = new BonusCalculation();
        calc.setCalculationId(rs.getInt("calculation_id"));
        calc.setEmployeeId(rs.getInt("employee_id"));
        calc.setYear(rs.getInt("year"));
        calc.setKpiScore(rs.getBigDecimal("kpi_score"));
        calc.setBonusAmount(rs.getBigDecimal("bonus_amount"));
        calc.setStatus(CalculationStatus.fromDbValue(rs.getString("status")));
        calc.setComment(rs.getString("comment"));
        return calc;
    }

    public List<BonusCalculation> findAll() {
        return executeQuery("SELECT * FROM bonus_calculations");
    }

    public BonusCalculation findById(int calculationId) {
        return executeQuerySingle("SELECT * FROM bonus_calculations WHERE calculation_id = ?", calculationId);
    }

    public List<BonusCalculation> findByYear(int year) {
        return executeQuery("SELECT * FROM bonus_calculations WHERE year = ?", year);
    }

    public BonusCalculation findByEmployeeAndYear(int employeeId, int year) {
        return executeQuerySingle(
                "SELECT * FROM bonus_calculations WHERE employee_id = ? AND year = ?",
                employeeId, year
        );
    }

    public List<BonusCalculation> findByDepartmentAndYear(int departmentId, int year) {
        return executeQuery(
                "SELECT bc.* FROM bonus_calculations bc " +
                        "JOIN employees e ON bc.employee_id = e.employee_id " +
                        "WHERE e.department_id = ? AND bc.year = ?",
                departmentId, year
        );
    }

    public int insert(BonusCalculation calc) {
        return executeInsertAndGetKey(
                "INSERT INTO bonus_calculations (employee_id, year, kpi_score, bonus_amount, status, comment) VALUES (?, ?, ?, ?, ?, ?)",
                calc.getEmployeeId(), calc.getYear(), calc.getKpiScore(),
                calc.getBonusAmount(), calc.getStatus().getDbValue(), calc.getComment()
        );
    }

    public void update(BonusCalculation calc) {
        executeUpdate(
                "UPDATE bonus_calculations SET kpi_score = ?, bonus_amount = ?, status = ?, comment = ? WHERE calculation_id = ?",
                calc.getKpiScore(), calc.getBonusAmount(),
                calc.getStatus().getDbValue(), calc.getComment(), calc.getCalculationId()
        );
    }

    public void deleteByYear(int year) {
        executeUpdate("DELETE FROM bonus_calculations WHERE year = ?", year);
    }

    public void delete(int calculationId) {
        executeUpdate("DELETE FROM bonus_calculations WHERE calculation_id = ?", calculationId);
    }
}