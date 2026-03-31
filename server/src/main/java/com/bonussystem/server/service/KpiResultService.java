package com.bonussystem.server.service;

import com.bonussystem.common.model.KpiResult;
import com.bonussystem.server.dao.DaoFactory;
import com.bonussystem.server.dao.KpiResultDao;

import java.util.List;

public class KpiResultService {

    private final KpiResultDao kpiResultDao = DaoFactory.getKpiResultDao();

    public List<KpiResult> getKpiResultsByDepartmentAndYear(int departmentId, int year) {
        return kpiResultDao.findByDepartmentAndYear(departmentId, year);
    }

    public List<KpiResult> getKpiResultsByEmployeeAndYear(int employeeId, int year) {
        return kpiResultDao.findByEmployeeAndYear(employeeId, year);
    }

    public int addKpiResult(KpiResult result) { return kpiResultDao.insert(result); }

    public void updateKpiResult(KpiResult result) {
        if (kpiResultDao.findById(result.getResultId()) == null) {
            throw new RuntimeException("Результат KPI не найден");
        }
        kpiResultDao.update(result);
    }

    public void deleteKpiResult(int id) {
        if (kpiResultDao.findById(id) == null) {
            throw new RuntimeException("Результат KPI не найден");
        }
        kpiResultDao.delete(id);
    }
}