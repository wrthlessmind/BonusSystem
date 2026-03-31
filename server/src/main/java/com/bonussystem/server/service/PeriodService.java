package com.bonussystem.server.service;

import com.bonussystem.common.model.Period;
import com.bonussystem.server.dao.DaoFactory;
import com.bonussystem.server.dao.PeriodDao;

import java.util.List;

public class PeriodService {

    private final PeriodDao periodDao = DaoFactory.getPeriodDao();

    public List<Period> getAllPeriods() { return periodDao.findAll(); }

    public Period getPeriodByYear(int year) { return periodDao.findByYear(year); }

    public void addPeriod(Period period) {
        if (periodDao.findByYear(period.getYear()) != null) {
            throw new RuntimeException("Период за " + period.getYear() + " год уже существует");
        }
        periodDao.insert(period);
    }

    public void updatePeriod(Period period) {
        if (periodDao.findByYear(period.getYear()) == null) {
            throw new RuntimeException("Период не найден");
        }
        periodDao.update(period);
    }

    public void deletePeriod(int year) {
        if (periodDao.findByYear(year) == null) {
            throw new RuntimeException("Период не найден");
        }
        periodDao.delete(year);
    }
}