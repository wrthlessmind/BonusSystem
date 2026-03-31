package com.bonussystem.client.controller.economist;

import com.bonussystem.common.model.BonusCalculation;
import com.bonussystem.common.model.Employee;
import com.bonussystem.common.model.enums.CalculationStatus;
import com.bonussystem.common.tcp.Request;
import com.bonussystem.common.tcp.Response;
import com.bonussystem.common.tcp.enums.RequestType;
import com.bonussystem.common.tcp.enums.ResponseStatus;
import com.bonussystem.common.util.GsonProvider;
import com.bonussystem.client.tcp.ServerConnection;
import com.bonussystem.client.util.AlertHelper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BonusCalculationController {

    @FXML private TextField yearField;
    @FXML private TableView<BonusCalculation> calcTable;
    @FXML private TableColumn<BonusCalculation, Integer> colId;
    @FXML private TableColumn<BonusCalculation, String> colEmployee;
    @FXML private TableColumn<BonusCalculation, Integer> colYear;
    @FXML private TableColumn<BonusCalculation, BigDecimal> colKpiScore;
    @FXML private TableColumn<BonusCalculation, BigDecimal> colBonusAmount;
    @FXML private TableColumn<BonusCalculation, CalculationStatus> colStatus;
    @FXML private TableColumn<BonusCalculation, String> colComment;

    private final Gson gson = GsonProvider.get();
    private ObservableList<BonusCalculation> calcList = FXCollections.observableArrayList();
    private Map<Integer, String> employeeNames = new HashMap<>();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("calculationId"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colKpiScore.setCellValueFactory(new PropertyValueFactory<>("kpiScore"));
        colBonusAmount.setCellValueFactory(new PropertyValueFactory<>("bonusAmount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        colEmployee.setCellValueFactory(cellData -> {
            int empId = cellData.getValue().getEmployeeId();
            String name = employeeNames.getOrDefault(empId, "ID: " + empId);
            return new SimpleStringProperty(name);
        });

        loadEmployeeNames();
    }

    private void loadEmployeeNames() {
        Request request = new Request(RequestType.GET_ALL_EMPLOYEES);
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            List<Employee> employees = gson.fromJson(
                    response.getBody(), new TypeToken<List<Employee>>(){}.getType());
            for (Employee emp : employees) {
                employeeNames.put(emp.getEmployeeId(),
                        emp.getLastName() + " " + emp.getFirstName());
            }
        }
    }

    private Integer parseYear() {
        try {
            return Integer.parseInt(yearField.getText().trim());
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Внимание", "Год должен быть числом (например: 2025)");
            return null;
        }
    }

    @FXML
    private void handleCalculate() {
        if (yearField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Внимание", "Введите год");
            return;
        }
        Integer year = parseYear();
        if (year == null) return;
        if (!AlertHelper.showConfirmation("Подтверждение", "Запустить расчёт премий за " + year + " год?")) return;
        JsonObject json = new JsonObject();
        json.addProperty("year", year);
        Request request = new Request(RequestType.CALCULATE_BONUSES, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Расчёт выполнен");
            loadCalculations(year);
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleLoadCalculations() {
        if (yearField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Внимание", "Введите год");
            return;
        }
        Integer year = parseYear();
        if (year == null) return;
        loadCalculations(year);
    }

    private void loadCalculations(int year) {
        JsonObject json = new JsonObject();
        json.addProperty("year", year);
        Request request = new Request(RequestType.GET_ALL_BONUS_CALCULATIONS, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            List<BonusCalculation> calculations = gson.fromJson(
                    response.getBody(), new TypeToken<List<BonusCalculation>>(){}.getType());
            calcList.setAll(calculations);
            calcTable.setItems(calcList);
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleApprove() {
        BonusCalculation selected = calcTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите расчёт"); return; }
        JsonObject json = new JsonObject();
        json.addProperty("calculationId", selected.getCalculationId());
        Request request = new Request(RequestType.APPROVE_CALCULATION, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Расчёт утверждён");
            loadCalculations(selected.getYear());
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleReject() {
        BonusCalculation selected = calcTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите расчёт"); return; }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Отклонение расчёта");
        dialog.setHeaderText("Укажите причину отклонения:");
        dialog.showAndWait().ifPresent(comment -> {
            JsonObject json = new JsonObject();
            json.addProperty("calculationId", selected.getCalculationId());
            json.addProperty("comment", comment);
            Request request = new Request(RequestType.REJECT_CALCULATION, json.toString());
            Response response = ServerConnection.getInstance().sendRequest(request);
            if (response.getStatus() == ResponseStatus.OK) {
                AlertHelper.showInfo("Успешно", "Расчёт отклонён");
                loadCalculations(selected.getYear());
            } else {
                AlertHelper.showError("Ошибка", response.getMessage());
            }
        });
    }
}