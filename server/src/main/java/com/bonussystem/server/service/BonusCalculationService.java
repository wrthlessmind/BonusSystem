package com.bonussystem.server.service;

import com.bonussystem.common.model.*;
import com.bonussystem.common.model.enums.CalculationStatus;
import com.bonussystem.common.model.enums.PeriodStatus;
import com.bonussystem.server.dao.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BonusCalculationService {

    private final BonusCalculationDao bonusCalcDao = DaoFactory.getBonusCalculationDao();
    private final EmployeeDao employeeDao = DaoFactory.getEmployeeDao();
    private final KpiResultDao kpiResultDao = DaoFactory.getKpiResultDao();
    private final KpiIndicatorDao kpiIndicatorDao = DaoFactory.getKpiIndicatorDao();
    private final PeriodDao periodDao = DaoFactory.getPeriodDao();

    public void calculateBonuses(int year) {
        Period period = periodDao.findByYear(year);
        if (period == null) {
            throw new RuntimeException("Расчётный период не найден");
        }
        if (period.getStatus() == PeriodStatus.APPROVED) {
            throw new RuntimeException("Период уже утверждён, расчёт невозможен");
        }

        List<KpiIndicator> indicators = kpiIndicatorDao.findAll();
        if (indicators.isEmpty()) {
            throw new RuntimeException("Не заданы показатели KPI");
        }

        Map<Integer, KpiIndicator> indicatorMap = new HashMap<>();
        for (KpiIndicator ind : indicators) {
            indicatorMap.put(ind.getKpiId(), ind);
        }

        bonusCalcDao.deleteByYear(year);

        List<Employee> employees = employeeDao.findAll();
        for (Employee emp : employees) {
            List<KpiResult> results = kpiResultDao.findByEmployeeAndYear(emp.getEmployeeId(), year);
            if (results.isEmpty()) {
                continue;
            }

            BigDecimal kpiScore = BigDecimal.ZERO;
            for (KpiResult result : results) {
                KpiIndicator indicator = indicatorMap.get(result.getKpiId());
                if (indicator == null || indicator.getTargetValue().compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }

                BigDecimal achievement = result.getActualValue()
                        .divide(indicator.getTargetValue(), 4, RoundingMode.HALF_UP);
                kpiScore = kpiScore.add(achievement.multiply(indicator.getWeight()));
            }
            kpiScore = kpiScore.setScale(2, RoundingMode.HALF_UP);

            BigDecimal bonusAmount = emp.getBaseSalary()
                    .multiply(kpiScore)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BonusCalculation calc = new BonusCalculation();
            calc.setEmployeeId(emp.getEmployeeId());
            calc.setYear(year);
            calc.setKpiScore(kpiScore);
            calc.setBonusAmount(bonusAmount);
            calc.setStatus(CalculationStatus.CALCULATED);
            bonusCalcDao.insert(calc);
        }

        period.setStatus(PeriodStatus.CALCULATING);
        periodDao.update(period);
    }

    public List<BonusCalculation> getAllByYear(int year) { return bonusCalcDao.findByYear(year); }

    public List<BonusCalculation> getByDepartmentAndYear(int departmentId, int year) {
        return bonusCalcDao.findByDepartmentAndYear(departmentId, year);
    }

    public void approveCalculation(int calculationId) {
        BonusCalculation calc = bonusCalcDao.findById(calculationId);
        if (calc == null) throw new RuntimeException("Расчёт не найден");
        calc.setStatus(CalculationStatus.APPROVED);
        bonusCalcDao.update(calc);
    }

    public void rejectCalculation(int calculationId, String comment) {
        BonusCalculation calc = bonusCalcDao.findById(calculationId);
        if (calc == null) throw new RuntimeException("Расчёт не найден");
        calc.setStatus(CalculationStatus.REJECTED);
        calc.setComment(comment);
        bonusCalcDao.update(calc);
    }

    public Map<String, Object> getStatistics(int year) {
        List<BonusCalculation> calculations = bonusCalcDao.findByYear(year);
        Map<String, Object> stats = new HashMap<>();

        if (calculations.isEmpty()) {
            stats.put("totalBonuses", BigDecimal.ZERO);
            stats.put("avgBonus", BigDecimal.ZERO);
            stats.put("maxKpiScore", BigDecimal.ZERO);
            stats.put("minKpiScore", BigDecimal.ZERO);
            stats.put("achievedPercent", BigDecimal.ZERO);
            return stats;
        }

        BigDecimal totalBonuses = BigDecimal.ZERO;
        BigDecimal maxKpi = calculations.get(0).getKpiScore();
        BigDecimal minKpi = calculations.get(0).getKpiScore();
        int achievedCount = 0;

        for (BonusCalculation calc : calculations) {
            totalBonuses = totalBonuses.add(calc.getBonusAmount());
            if (calc.getKpiScore().compareTo(maxKpi) > 0) maxKpi = calc.getKpiScore();
            if (calc.getKpiScore().compareTo(minKpi) < 0) minKpi = calc.getKpiScore();
            if (calc.getKpiScore().compareTo(BigDecimal.valueOf(100)) >= 0) achievedCount++;
        }

        BigDecimal avgBonus = totalBonuses.divide(BigDecimal.valueOf(calculations.size()), 2, RoundingMode.HALF_UP);
        BigDecimal achievedPercent = BigDecimal.valueOf(achievedCount * 100.0 / calculations.size())
                .setScale(2, RoundingMode.HALF_UP);

        stats.put("totalBonuses", totalBonuses);
        stats.put("avgBonus", avgBonus);
        stats.put("maxKpiScore", maxKpi);
        stats.put("minKpiScore", minKpi);
        stats.put("achievedPercent", achievedPercent);
        return stats;
    }
}