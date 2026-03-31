package com.bonussystem.client.controller.head;

import com.bonussystem.common.model.BonusCalculation;
import com.bonussystem.common.model.Department;
import com.bonussystem.common.model.Employee;
import com.bonussystem.common.tcp.Request;
import com.bonussystem.common.tcp.Response;
import com.bonussystem.common.tcp.enums.RequestType;
import com.bonussystem.common.tcp.enums.ResponseStatus;
import com.bonussystem.common.util.GsonProvider;
import com.bonussystem.client.tcp.ServerConnection;
import com.bonussystem.client.util.AlertHelper;
import com.bonussystem.client.util.SceneManager;
import com.bonussystem.client.util.Session;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.util.List;

public class DepartmentResultsController {

    @FXML private TextField yearField;
    @FXML private TableView<BonusCalculation> resultsTable;
    @FXML private TableColumn<BonusCalculation, String>     colEmployeeName;
    @FXML private TableColumn<BonusCalculation, BigDecimal> colKpiScore;
    @FXML private TableColumn<BonusCalculation, BigDecimal> colBonusAmount;
    @FXML private TableColumn<BonusCalculation, String>     colStatus;
    @FXML private TableColumn<BonusCalculation, String>     colComment;

    private final Gson gson = GsonProvider.get();
    private final ObservableList<BonusCalculation> resultList = FXCollections.observableArrayList();
    private List<Employee> employeeList;
    private int departmentId = -1;

    @FXML
    private void initialize() {
        colEmployeeName.setCellValueFactory(cellData -> {
            int empId = cellData.getValue().getEmployeeId();
            String name = employeeList == null ? String.valueOf(empId) :
                    employeeList.stream()
                            .filter(e -> e.getEmployeeId() == empId)
                            .map(e -> e.getLastName() + " " + e.getFirstName())
                            .findFirst().orElse(String.valueOf(empId));
            return new SimpleStringProperty(name);
        });

        colKpiScore.setCellValueFactory(new PropertyValueFactory<>("kpiScore"));
        colBonusAmount.setCellValueFactory(new PropertyValueFactory<>("bonusAmount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        loadDepartmentId();
        loadEmployees();
    }

    private void loadDepartmentId() {
        Request request = new Request(RequestType.GET_ALL_DEPARTMENTS);
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            List<Department> departments = gson.fromJson(response.getBody(),
                    new TypeToken<List<Department>>(){}.getType());
            int currentUserId = Session.getCurrentUser().getUserId();
            for (Department dept : departments) {
                if (dept.getHeadUserId() != null && dept.getHeadUserId() == currentUserId) {
                    departmentId = dept.getDepartmentId();
                    break;
                }
            }
        }
        if (departmentId == -1) {
            AlertHelper.showWarning("Внимание", "Вы не назначены руководителем отдела");
        }
    }

    private void loadEmployees() {
        if (departmentId == -1) return;
        JsonObject json = new JsonObject();
        json.addProperty("departmentId", departmentId);
        Request request = new Request(RequestType.GET_EMPLOYEES_BY_DEPARTMENT, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            employeeList = gson.fromJson(response.getBody(),
                    new TypeToken<List<Employee>>(){}.getType());
        }
    }

    @FXML
    private void handleLoadResults() {
        if (departmentId == -1) {
            AlertHelper.showError("Ошибка", "Вы не назначены руководителем отдела");
            return;
        }
        if (yearField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Внимание", "Введите год");
            return;
        }
        int year;
        try {
            year = Integer.parseInt(yearField.getText().trim());
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Внимание", "Год должен быть числом (например: 2025)");
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("departmentId", departmentId);
        json.addProperty("year", year);
        Request request = new Request(RequestType.GET_BONUS_CALCULATIONS_BY_DEPARTMENT, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            List<BonusCalculation> calculations = gson.fromJson(response.getBody(),
                    new TypeToken<List<BonusCalculation>>(){}.getType());
            resultList.setAll(calculations);
            resultsTable.setItems(resultList);
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/fxml/head/head_main.fxml", "Панель руководителя отдела");
    }
}