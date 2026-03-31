package com.bonussystem.client.controller.hr;

import com.bonussystem.common.model.Department;
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

public class EmployeeManagementController {

    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Integer> colId;
    @FXML private TableColumn<Employee, String> colFullName;
    @FXML private TableColumn<Employee, String> colPosition;
    @FXML private TableColumn<Employee, BigDecimal> colSalary;
    @FXML private TableColumn<Employee, Integer> colDeptId;

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField positionField;
    @FXML private TextField salaryField;
    @FXML private ComboBox<Department> deptComboBox;

    private final Gson gson = GsonProvider.get();
    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private ObservableList<Department> departmentList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colFullName.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getLastName() + " " + cellData.getValue().getFirstName()
        ));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));

        colDeptId.setCellValueFactory(new PropertyValueFactory<>("departmentId"));
        colDeptId.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer deptId, boolean empty) {
                super.updateItem(deptId, empty);
                if (empty || deptId == null) {
                    setText(null);
                } else {
                    departmentList.stream()
                            .filter(d -> d.getDepartmentId() == deptId)
                            .findFirst()
                            .ifPresentOrElse(
                                    d -> setText(d.getName()),
                                    () -> setText("ID: " + deptId)
                            );
                }
            }
        });

        deptComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Department dept, boolean empty) {
                super.updateItem(dept, empty);
                setText(empty || dept == null ? null : dept.getName());
            }
        });
        deptComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Department dept, boolean empty) {
                super.updateItem(dept, empty);
                setText(empty || dept == null ? null : dept.getName());
            }
        });

        loadDepartments();
        loadEmployees();

        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                firstNameField.setText(newVal.getFirstName());
                lastNameField.setText(newVal.getLastName());
                positionField.setText(newVal.getPosition());
                salaryField.setText(newVal.getBaseSalary() != null ? newVal.getBaseSalary().toString() : "");
                departmentList.stream()
                        .filter(d -> d.getDepartmentId() == newVal.getDepartmentId())
                        .findFirst()
                        .ifPresent(deptComboBox::setValue);
            }
        });
    }

    private void loadDepartments() {
        Request request = new Request(RequestType.GET_ALL_DEPARTMENTS);
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            List<Department> departments = gson.fromJson(response.getBody(), new TypeToken<List<Department>>(){}.getType());
            departmentList.setAll(departments);
            deptComboBox.setItems(departmentList);
        } else {
            AlertHelper.showError("Ошибка", "Не удалось загрузить список отделов");
        }
    }

    private void loadEmployees() {
        Request request = new Request(RequestType.GET_ALL_EMPLOYEES);
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            List<Employee> employees = gson.fromJson(response.getBody(), new TypeToken<List<Employee>>(){}.getType());
            employeeList.setAll(employees);
            employeeTable.setItems(employeeList);
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        if (!validateFields()) return;
        Employee emp = buildEmployeeFromFields();
        Response response = ServerConnection.getInstance().sendRequest(
                new Request(RequestType.ADD_EMPLOYEE, gson.toJson(emp)));
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Сотрудник добавлен");
            clearFields();
            loadEmployees();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите сотрудника"); return; }
        if (!validateFields()) return;
        Employee emp = buildEmployeeFromFields();
        emp.setEmployeeId(selected.getEmployeeId());
        Response response = ServerConnection.getInstance().sendRequest(
                new Request(RequestType.UPDATE_EMPLOYEE, gson.toJson(emp)));
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Сотрудник обновлён");
            clearFields();
            loadEmployees();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите сотрудника"); return; }
        if (!AlertHelper.showConfirmation("Подтверждение", "Удалить сотрудника?")) return;
        JsonObject json = new JsonObject();
        json.addProperty("employeeId", selected.getEmployeeId());
        Response response = ServerConnection.getInstance().sendRequest(
                new Request(RequestType.DELETE_EMPLOYEE, json.toString()));
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Сотрудник удалён");
            clearFields();
            loadEmployees();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadDepartments();
        loadEmployees();
    }

    private boolean validateFields() {
        if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()
                || positionField.getText().trim().isEmpty() || salaryField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Внимание", "Заполните обязательные поля (Имя, Фамилия, Должность, Оклад)");
            return false;
        }
        try {
            new BigDecimal(salaryField.getText().trim());
        } catch (NumberFormatException e) {
            AlertHelper.showError("Ошибка", "Некорректный формат оклада");
            return false;
        }
        return true;
    }

    private Employee buildEmployeeFromFields() {
        Employee emp = new Employee();
        emp.setFirstName(firstNameField.getText().trim());
        emp.setLastName(lastNameField.getText().trim());
        emp.setPosition(positionField.getText().trim());
        emp.setBaseSalary(new BigDecimal(salaryField.getText().trim()));
        Department selectedDept = deptComboBox.getValue();
        emp.setDepartmentId(selectedDept != null ? selectedDept.getDepartmentId() : null);
        return emp;
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        positionField.clear();
        salaryField.clear();
        deptComboBox.setValue(null);
    }
}