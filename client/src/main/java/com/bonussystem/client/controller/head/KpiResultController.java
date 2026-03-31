package com.bonussystem.client.controller.head;

import com.bonussystem.common.model.Department;
import com.bonussystem.common.model.Employee;
import com.bonussystem.common.model.KpiIndicator;
import com.bonussystem.common.model.KpiResult;
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
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.List;

public class KpiResultController {

    @FXML private TextField yearField;
    @FXML private TableView<KpiResult> resultTable;
    @FXML private TableColumn<KpiResult, Integer> colId;
    @FXML private TableColumn<KpiResult, String>  colEmployeeName;
    @FXML private TableColumn<KpiResult, String>  colKpiName;
    @FXML private TableColumn<KpiResult, Integer> colYear;
    @FXML private TableColumn<KpiResult, BigDecimal> colActualValue;
    @FXML private TableColumn<KpiResult, String>  colNote;

    @FXML private ComboBox<Employee>     employeeComboBox;
    @FXML private ComboBox<KpiIndicator> kpiComboBox;
    @FXML private TextField actualValueField;
    @FXML private TextField noteField;

    private final Gson gson = GsonProvider.get();
    private final ObservableList<KpiResult> resultList = FXCollections.observableArrayList();
    private List<Employee> employeeList;
    private List<KpiIndicator> kpiList;
    private int departmentId = -1;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("resultId"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colActualValue.setCellValueFactory(new PropertyValueFactory<>("actualValue"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        colEmployeeName.setCellValueFactory(cellData -> {
            int empId = cellData.getValue().getEmployeeId();
            String name = employeeList == null ? String.valueOf(empId) :
                    employeeList.stream()
                            .filter(e -> e.getEmployeeId() == empId)
                            .map(e -> e.getLastName() + " " + e.getFirstName())
                            .findFirst().orElse(String.valueOf(empId));
            return new SimpleStringProperty(name);
        });

        colKpiName.setCellValueFactory(cellData -> {
            int kpiId = cellData.getValue().getKpiId();
            String name = kpiList == null ? String.valueOf(kpiId) :
                    kpiList.stream()
                            .filter(k -> k.getKpiId() == kpiId)
                            .map(KpiIndicator::getName)
                            .findFirst().orElse(String.valueOf(kpiId));
            return new SimpleStringProperty(name);
        });

        employeeComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Employee e) {
                return e == null ? "" : e.getLastName() + " " + e.getFirstName();
            }
            @Override public Employee fromString(String s) { return null; }
        });

        kpiComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(KpiIndicator k) {
                return k == null ? "" : k.getKpiId() + ": " + k.getName();
            }
            @Override public KpiIndicator fromString(String s) { return null; }
        });

        loadDepartmentId();
        loadEmployees();
        loadKpiIndicators();

        resultTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (employeeList != null) {
                    employeeList.stream()
                            .filter(e -> e.getEmployeeId() == newVal.getEmployeeId())
                            .findFirst().ifPresent(employeeComboBox::setValue);
                }
                if (kpiList != null) {
                    kpiList.stream()
                            .filter(k -> k.getKpiId() == newVal.getKpiId())
                            .findFirst().ifPresent(kpiComboBox::setValue);
                }
                actualValueField.setText(newVal.getActualValue() != null ? newVal.getActualValue().toString() : "");
                noteField.setText(newVal.getNote() != null ? newVal.getNote() : "");
            }
        });
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
            AlertHelper.showWarning("Внимание", "Вы не назначены руководителем ни одного отдела");
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
            employeeComboBox.setItems(FXCollections.observableArrayList(employeeList));
        }
    }

    private void loadKpiIndicators() {
        Request request = new Request(RequestType.GET_ALL_KPI_INDICATORS);
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            kpiList = gson.fromJson(response.getBody(),
                    new TypeToken<List<KpiIndicator>>(){}.getType());
            kpiComboBox.setItems(FXCollections.observableArrayList(kpiList));
        }
    }

    @FXML
    private void handleLoadResults() {
        if (departmentId == -1) { AlertHelper.showError("Ошибка", "Отдел не найден"); return; }
        if (yearField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Внимание", "Введите год"); return;
        }
        int year;
        try {
            year = Integer.parseInt(yearField.getText().trim());
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Внимание", "Год должен быть числом");
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("departmentId", departmentId);
        json.addProperty("year", year);
        Request request = new Request(RequestType.GET_KPI_RESULTS_BY_DEPARTMENT, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            List<KpiResult> results = gson.fromJson(response.getBody(),
                    new TypeToken<List<KpiResult>>(){}.getType());
            resultList.setAll(results);
            resultTable.setItems(resultList);
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        if (!validateFields()) return;
        KpiResult result = buildFromFields();
        Request request = new Request(RequestType.ADD_KPI_RESULT, gson.toJson(result));
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Результат KPI добавлен");
            clearFields();
            handleLoadResults();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        KpiResult selected = resultTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите запись"); return; }
        if (!validateFields()) return;
        KpiResult result = buildFromFields();
        result.setResultId(selected.getResultId());
        Request request = new Request(RequestType.UPDATE_KPI_RESULT, gson.toJson(result));
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Результат KPI обновлён");
            clearFields();
            handleLoadResults();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        KpiResult selected = resultTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите запись"); return; }
        if (!AlertHelper.showConfirmation("Подтверждение", "Удалить результат?")) return;
        JsonObject json = new JsonObject();
        json.addProperty("resultId", selected.getResultId());
        Request request = new Request(RequestType.DELETE_KPI_RESULT, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Результат KPI удалён");
            clearFields();
            handleLoadResults();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/fxml/head/head_main.fxml", "Панель руководителя отдела");
    }

    private boolean validateFields() {
        if (employeeComboBox.getValue() == null || kpiComboBox.getValue() == null
                || actualValueField.getText().trim().isEmpty()
                || yearField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Внимание", "Заполните обязательные поля");
            return false;
        }
        try {
            Integer.parseInt(yearField.getText().trim());
            new BigDecimal(actualValueField.getText().trim());
        } catch (NumberFormatException e) {
            AlertHelper.showError("Ошибка", "Некорректный числовой формат");
            return false;
        }
        return true;
    }

    private KpiResult buildFromFields() {
        KpiResult result = new KpiResult();
        result.setEmployeeId(employeeComboBox.getValue().getEmployeeId());
        result.setKpiId(kpiComboBox.getValue().getKpiId());
        result.setYear(Integer.parseInt(yearField.getText().trim()));
        result.setActualValue(new BigDecimal(actualValueField.getText().trim()));
        result.setNote(noteField.getText().trim());
        return result;
    }

    private void clearFields() {
        employeeComboBox.setValue(null);
        kpiComboBox.setValue(null);
        actualValueField.clear();
        noteField.clear();
    }
}