package com.bonussystem.server.tcp;

import com.bonussystem.common.model.*;
import com.bonussystem.common.model.enums.Role;
import com.bonussystem.common.tcp.Request;
import com.bonussystem.common.tcp.Response;
import com.bonussystem.common.tcp.enums.RequestType;
import com.bonussystem.common.util.GsonProvider;
import com.bonussystem.server.service.*;
import com.bonussystem.server.util.FileLogger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class RequestRouter {

    private final Gson gson = GsonProvider.get();
    private final UserService userService = new UserService();
    private final DepartmentService departmentService = new DepartmentService();
    private final EmployeeService employeeService = new EmployeeService();
    private final PeriodService periodService = new PeriodService();
    private final KpiIndicatorService kpiIndicatorService = new KpiIndicatorService();
    private final KpiResultService kpiResultService = new KpiResultService();
    private final BonusCalculationService bonusCalcService = new BonusCalculationService();

    public Response route(Request request) {
        try {
            RequestType type = request.getRequestType();
            String body = request.getBody();

            switch (type) {
                case LOGIN: return handleLogin(body);
                case REGISTER: return handleRegister(body);

                case GET_ALL_USERS: return handleGetAllUsers();
                case GET_USERS_BY_ROLE: return handleGetUsersByRole(body);
                case UPDATE_USER: return handleUpdateUser(body);
                case BLOCK_USER: return handleBlockUser(body);
                case UNBLOCK_USER: return handleUnblockUser(body);
                case DELETE_USER: return handleDeleteUser(body);

                case GET_ALL_EMPLOYEES: return handleGetAllEmployees();
                case ADD_EMPLOYEE: return handleAddEmployee(body);
                case UPDATE_EMPLOYEE: return handleUpdateEmployee(body);
                case DELETE_EMPLOYEE: return handleDeleteEmployee(body);
                case GET_EMPLOYEE_BY_USER_ID: return handleGetEmployeeByUserId(body);

                case GET_ALL_DEPARTMENTS: return handleGetAllDepartments();
                case ADD_DEPARTMENT: return handleAddDepartment(body);
                case UPDATE_DEPARTMENT: return handleUpdateDepartment(body);
                case DELETE_DEPARTMENT: return handleDeleteDepartment(body);

                case GET_ALL_KPI_INDICATORS: return handleGetAllKpiIndicators();
                case ADD_KPI_INDICATOR: return handleAddKpiIndicator(body);
                case UPDATE_KPI_INDICATOR: return handleUpdateKpiIndicator(body);
                case DELETE_KPI_INDICATOR: return handleDeleteKpiIndicator(body);

                case GET_ALL_PERIODS: return handleGetAllPeriods();
                case ADD_PERIOD: return handleAddPeriod(body);
                case UPDATE_PERIOD: return handleUpdatePeriod(body);
                case DELETE_PERIOD: return handleDeletePeriod(body);

                case GET_KPI_RESULTS_BY_DEPARTMENT: return handleGetKpiResultsByDepartment(body);
                case ADD_KPI_RESULT: return handleAddKpiResult(body);
                case UPDATE_KPI_RESULT: return handleUpdateKpiResult(body);
                case DELETE_KPI_RESULT: return handleDeleteKpiResult(body);

                case CALCULATE_BONUSES: return handleCalculateBonuses(body);
                case GET_ALL_BONUS_CALCULATIONS: return handleGetAllBonusCalculations(body);
                case APPROVE_CALCULATION: return handleApproveCalculation(body);
                case REJECT_CALCULATION: return handleRejectCalculation(body);

                case GET_BONUS_CALCULATIONS_BY_DEPARTMENT: return handleGetBonusCalcByDepartment(body);

                case GET_BONUS_REPORT: return handleGetBonusReport(body);
                case GET_STATISTICS: return handleGetStatistics(body);

                case UPDATE_PROFILE: return handleUpdateProfile(body);

                case GET_EMPLOYEES_BY_DEPARTMENT: return handleGetEmployeesByDepartment(body);

                default:
                    return Response.error("Неизвестный тип запроса: " + type);
            }
        } catch (Exception e) {
            FileLogger.log("Ошибка обработки запроса: " + request.getRequestType(), e);
            return Response.error(e.getMessage());
        }
    }

    private Response handleLogin(String body) {
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        String login = json.get("login").getAsString();
        String password = json.get("password").getAsString();
        User user = userService.login(login, password);
        user.setPassword(null);
        FileLogger.log("Пользователь авторизован: " + user.getLogin());
        return Response.ok(gson.toJson(user));
    }

    private Response handleRegister(String body) {
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        String login = json.get("login").getAsString();
        String password = json.get("password").getAsString();
        Role role = Role.fromDbValue(json.get("role").getAsString());
        String firstName = json.get("firstName").getAsString();
        String lastName = json.get("lastName").getAsString();
        User user = userService.register(login, password, role, firstName, lastName);
        user.setPassword(null);
        FileLogger.log("Новый пользователь зарегистрирован: " + user.getLogin());
        return Response.ok("Регистрация успешна", gson.toJson(user));
    }

    private Response handleGetEmployeeByUserId(String body) {
        int userId = JsonParser.parseString(body).getAsJsonObject().get("userId").getAsInt();
        Employee employee = employeeService.getEmployeeByUserId(userId);
        if (employee == null) {
            return Response.error("Сотрудник не найден для пользователя id=" + userId);
        }
        return Response.ok(gson.toJson(employee));
    }

    private Response handleGetAllUsers() {
        List<User> users = userService.getAllUsers();
        users.forEach(u -> u.setPassword(null));
        return Response.ok(gson.toJson(users));
    }

    private Response handleUpdateUser(String body) {
        User user = gson.fromJson(body, User.class);
        userService.updateUser(user);
        return Response.ok("Пользователь обновлён", null);
    }

    private Response handleBlockUser(String body) {
        int userId = JsonParser.parseString(body).getAsJsonObject().get("userId").getAsInt();
        userService.blockUser(userId);
        FileLogger.log("Пользователь заблокирован: id=" + userId);
        return Response.ok("Пользователь заблокирован", null);
    }

    private Response handleUnblockUser(String body) {
        int userId = JsonParser.parseString(body).getAsJsonObject().get("userId").getAsInt();
        userService.unblockUser(userId);
        FileLogger.log("Пользователь разблокирован: id=" + userId);
        return Response.ok("Пользователь разблокирован", null);
    }

    private Response handleDeleteUser(String body) {
        int userId = JsonParser.parseString(body).getAsJsonObject().get("userId").getAsInt();
        userService.deleteUser(userId);
        FileLogger.log("Пользователь удалён: id=" + userId);
        return Response.ok("Пользователь удалён", null);
    }

    private Response handleGetAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return Response.ok(gson.toJson(employees));
    }

    private Response handleAddEmployee(String body) {
        Employee emp = gson.fromJson(body, Employee.class);
        int id = employeeService.addEmployee(emp);
        emp.setEmployeeId(id);
        return Response.ok("Сотрудник добавлен", gson.toJson(emp));
    }

    private Response handleUpdateEmployee(String body) {
        Employee emp = gson.fromJson(body, Employee.class);
        employeeService.updateEmployee(emp);
        return Response.ok("Сотрудник обновлён", null);
    }

    private Response handleDeleteEmployee(String body) {
        int id = JsonParser.parseString(body).getAsJsonObject().get("employeeId").getAsInt();
        employeeService.deleteEmployee(id);
        return Response.ok("Сотрудник удалён", null);
    }

    private Response handleGetAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return Response.ok(gson.toJson(departments));
    }

    private Response handleAddDepartment(String body) {
        Department dept = gson.fromJson(body, Department.class);
        int id = departmentService.addDepartment(dept);
        dept.setDepartmentId(id);
        return Response.ok("Отдел добавлен", gson.toJson(dept));
    }

    private Response handleUpdateDepartment(String body) {
        Department dept = gson.fromJson(body, Department.class);
        departmentService.updateDepartment(dept);
        return Response.ok("Отдел обновлён", null);
    }

    private Response handleDeleteDepartment(String body) {
        int id = JsonParser.parseString(body).getAsJsonObject().get("departmentId").getAsInt();
        departmentService.deleteDepartment(id);
        return Response.ok("Отдел удалён", null);
    }

    private Response handleGetAllKpiIndicators() {
        List<KpiIndicator> indicators = kpiIndicatorService.getAllKpiIndicators();
        return Response.ok(gson.toJson(indicators));
    }

    private Response handleAddKpiIndicator(String body) {
        KpiIndicator kpi = gson.fromJson(body, KpiIndicator.class);
        int id = kpiIndicatorService.addKpiIndicator(kpi);
        kpi.setKpiId(id);
        return Response.ok("Показатель KPI добавлен", gson.toJson(kpi));
    }

    private Response handleUpdateKpiIndicator(String body) {
        KpiIndicator kpi = gson.fromJson(body, KpiIndicator.class);
        kpiIndicatorService.updateKpiIndicator(kpi);
        return Response.ok("Показатель KPI обновлён", null);
    }

    private Response handleDeleteKpiIndicator(String body) {
        int id = JsonParser.parseString(body).getAsJsonObject().get("kpiId").getAsInt();
        kpiIndicatorService.deleteKpiIndicator(id);
        return Response.ok("Показатель KPI удалён", null);
    }

    private Response handleGetAllPeriods() {
        List<Period> periods = periodService.getAllPeriods();
        return Response.ok(gson.toJson(periods));
    }

    private Response handleAddPeriod(String body) {
        Period period = gson.fromJson(body, Period.class);
        periodService.addPeriod(period);
        return Response.ok("Период добавлен", gson.toJson(period));
    }

    private Response handleUpdatePeriod(String body) {
        Period period = gson.fromJson(body, Period.class);
        periodService.updatePeriod(period);
        return Response.ok("Период обновлён", null);
    }

    private Response handleDeletePeriod(String body) {
        int year = JsonParser.parseString(body).getAsJsonObject().get("year").getAsInt();
        periodService.deletePeriod(year);
        return Response.ok("Период удалён", null);
    }

    private Response handleGetKpiResultsByDepartment(String body) {
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        int departmentId = json.get("departmentId").getAsInt();
        int year = json.get("year").getAsInt();
        List<KpiResult> results = kpiResultService.getKpiResultsByDepartmentAndYear(departmentId, year);
        return Response.ok(gson.toJson(results));
    }

    private Response handleAddKpiResult(String body) {
        KpiResult result = gson.fromJson(body, KpiResult.class);
        int id = kpiResultService.addKpiResult(result);
        result.setResultId(id);
        return Response.ok("Результат KPI добавлен", gson.toJson(result));
    }

    private Response handleUpdateKpiResult(String body) {
        KpiResult result = gson.fromJson(body, KpiResult.class);
        kpiResultService.updateKpiResult(result);
        return Response.ok("Результат KPI обновлён", null);
    }

    private Response handleDeleteKpiResult(String body) {
        int id = JsonParser.parseString(body).getAsJsonObject().get("resultId").getAsInt();
        kpiResultService.deleteKpiResult(id);
        return Response.ok("Результат KPI удалён", null);
    }

    private Response handleCalculateBonuses(String body) {
        int year = JsonParser.parseString(body).getAsJsonObject().get("year").getAsInt();
        bonusCalcService.calculateBonuses(year);
        FileLogger.log("Расчёт премиальных выполнен за " + year + " год");
        return Response.ok("Расчёт премиальных выполнен", null);
    }

    private Response handleGetAllBonusCalculations(String body) {
        int year = JsonParser.parseString(body).getAsJsonObject().get("year").getAsInt();
        List<BonusCalculation> calculations = bonusCalcService.getAllByYear(year);
        return Response.ok(gson.toJson(calculations));
    }

    private Response handleApproveCalculation(String body) {
        int calcId = JsonParser.parseString(body).getAsJsonObject().get("calculationId").getAsInt();
        bonusCalcService.approveCalculation(calcId);
        return Response.ok("Расчёт утверждён", null);
    }

    private Response handleRejectCalculation(String body) {
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        int calcId = json.get("calculationId").getAsInt();
        String comment = json.get("comment").getAsString();
        bonusCalcService.rejectCalculation(calcId, comment);
        return Response.ok("Расчёт отклонён", null);
    }

    private Response handleGetBonusCalcByDepartment(String body) {
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        int departmentId = json.get("departmentId").getAsInt();
        int year = json.get("year").getAsInt();
        List<BonusCalculation> calculations = bonusCalcService.getByDepartmentAndYear(departmentId, year);
        return Response.ok(gson.toJson(calculations));
    }

    private Response handleGetBonusReport(String body) {
        int year = JsonParser.parseString(body).getAsJsonObject().get("year").getAsInt();
        List<BonusCalculation> calculations = bonusCalcService.getAllByYear(year);
        return Response.ok(gson.toJson(calculations));
    }

    private Response handleGetStatistics(String body) {
        int year = JsonParser.parseString(body).getAsJsonObject().get("year").getAsInt();
        Map<String, Object> stats = bonusCalcService.getStatistics(year);
        return Response.ok(gson.toJson(stats));
    }

    private Response handleUpdateProfile(String body) {
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        int userId = json.get("userId").getAsInt();
        String newLogin = json.has("newLogin") ? json.get("newLogin").getAsString() : null;
        String newPassword = json.has("newPassword") ? json.get("newPassword").getAsString() : null;
        userService.updateProfile(userId, newLogin, newPassword);
        return Response.ok("Профиль обновлён", null);
    }

    private Response handleGetEmployeesByDepartment(String body) {
        int departmentId = JsonParser.parseString(body).getAsJsonObject().get("departmentId").getAsInt();
        List<Employee> employees = employeeService.getEmployeesByDepartment(departmentId);
        return Response.ok(gson.toJson(employees));
    }

    private Response handleGetUsersByRole(String body) {
        String roleStr = gson.fromJson(body, String.class);
        Role role = Role.fromDbValue(roleStr);
        List<User> users = userService.getUsersByRole(role);
        users.forEach(u -> u.setPassword(null));
        return Response.ok(gson.toJson(users));
    }
}