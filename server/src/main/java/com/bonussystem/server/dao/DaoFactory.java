package com.bonussystem.server.dao;

public class DaoFactory {

    private static final UserDao userDao = new UserDao();
    private static final DepartmentDao departmentDao = new DepartmentDao();
    private static final EmployeeDao employeeDao = new EmployeeDao();
    private static final PeriodDao periodDao = new PeriodDao();
    private static final KpiIndicatorDao kpiIndicatorDao = new KpiIndicatorDao();
    private static final KpiResultDao kpiResultDao = new KpiResultDao();
    private static final BonusCalculationDao bonusCalculationDao = new BonusCalculationDao();

    public static UserDao getUserDao() { return userDao; }
    public static DepartmentDao getDepartmentDao() { return departmentDao; }
    public static EmployeeDao getEmployeeDao() { return employeeDao; }
    public static PeriodDao getPeriodDao() { return periodDao; }
    public static KpiIndicatorDao getKpiIndicatorDao() { return kpiIndicatorDao; }
    public static KpiResultDao getKpiResultDao() { return kpiResultDao; }
    public static BonusCalculationDao getBonusCalculationDao() { return bonusCalculationDao; }
}