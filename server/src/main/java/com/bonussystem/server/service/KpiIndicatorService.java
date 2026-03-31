package com.bonussystem.server.service;

import com.bonussystem.common.model.KpiIndicator;
import com.bonussystem.server.dao.DaoFactory;
import com.bonussystem.server.dao.KpiIndicatorDao;

import java.util.List;

public class KpiIndicatorService {

    private final KpiIndicatorDao kpiIndicatorDao = DaoFactory.getKpiIndicatorDao();

    public List<KpiIndicator> getAllKpiIndicators() { return kpiIndicatorDao.findAll(); }

    public KpiIndicator getKpiIndicatorById(int id) { return kpiIndicatorDao.findById(id); }

    public int addKpiIndicator(KpiIndicator kpi) { return kpiIndicatorDao.insert(kpi); }

    public void updateKpiIndicator(KpiIndicator kpi) {
        if (kpiIndicatorDao.findById(kpi.getKpiId()) == null) {
            throw new RuntimeException("Показатель KPI не найден");
        }
        kpiIndicatorDao.update(kpi);
    }

    public void deleteKpiIndicator(int id) {
        if (kpiIndicatorDao.findById(id) == null) {
            throw new RuntimeException("Показатель KPI не найден");
        }
        kpiIndicatorDao.delete(id);
    }
}