package com.bonussystem.client.controller.economist;

import com.bonussystem.common.model.BonusCalculation;
import com.bonussystem.common.model.Employee;
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
import java.util.List;

public class ReportController {

    @FXML private TextField yearField;
    @FXML private TableView<BonusCalculation> reportTable;
    @FXML private TableColumn<BonusCalculation, String>     colEmployeeName;
    @FXML private TableColumn<BonusCalculation, Integer>    colYear;
    @FXML private TableColumn<BonusCalculation, BigDecimal> colKpiScore;
    @FXML private TableColumn<BonusCalculation, BigDecimal> colBonusAmount;
    @FXML private TableColumn<BonusCalculation, String>     colStatus;

    private final Gson gson = GsonProvider.get();
    private final ObservableList<BonusCalculation> reportList = FXCollections.observableArrayList();
    private List<Employee> employeeList;

    @FXML
    private void initialize() {
        loadEmployees();

        colEmployeeName.setCellValueFactory(cellData -> {
            int empId = cellData.getValue().getEmployeeId();
            String name = employeeList == null ? String.valueOf(empId) :
                    employeeList.stream()
                            .filter(e -> e.getEmployeeId() == empId)
                            .map(e -> e.getLastName() + " " + e.getFirstName())
                            .findFirst().orElse(String.valueOf(empId));
            return new SimpleStringProperty(name);
        });

        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colKpiScore.setCellValueFactory(new PropertyValueFactory<>("kpiScore"));
        colBonusAmount.setCellValueFactory(new PropertyValueFactory<>("bonusAmount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadEmployees() {
        Request request = new Request(RequestType.GET_ALL_EMPLOYEES);
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            employeeList = gson.fromJson(response.getBody(),
                    new TypeToken<List<Employee>>(){}.getType());
        }
    }

    @FXML
    private void handleLoadReport() {
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
        json.addProperty("year", year);
        Request request = new Request(RequestType.GET_BONUS_REPORT, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            List<BonusCalculation> report = gson.fromJson(response.getBody(),
                    new TypeToken<List<BonusCalculation>>(){}.getType());
            reportList.setAll(report);
            reportTable.setItems(reportList);
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }
}