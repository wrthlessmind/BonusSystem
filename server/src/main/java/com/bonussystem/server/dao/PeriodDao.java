package com.bonussystem.server.dao;

import com.bonussystem.common.model.Period;
import com.bonussystem.common.model.enums.PeriodStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PeriodDao extends BaseDao<Period> {

    @Override
    protected Period mapRow(ResultSet rs) throws SQLException {
        Period period = new Period();
        period.setYear(rs.getInt("year"));
        period.setBonusFund(rs.getBigDecimal("bonus_fund"));
        period.setStatus(PeriodStatus.fromDbValue(rs.getString("status")));
        return period;
    }

    public List<Period> findAll() {
        return executeQuery("SELECT * FROM periods");
    }

    public Period findByYear(int year) {
        return executeQuerySingle("SELECT * FROM periods WHERE year = ?", year);
    }

    public void insert(Period period) {
        executeUpdate(
                "INSERT INTO periods (year, bonus_fund, status) VALUES (?, ?, ?)",
                period.getYear(), period.getBonusFund(), period.getStatus().getDbValue()
        );
    }

    public void update(Period period) {
        executeUpdate(
                "UPDATE periods SET bonus_fund = ?, status = ? WHERE year = ?",
                period.getBonusFund(), period.getStatus().getDbValue(), period.getYear()
        );
    }

    public void delete(int year) {
        executeUpdate("DELETE FROM periods WHERE year = ?", year);
    }
}