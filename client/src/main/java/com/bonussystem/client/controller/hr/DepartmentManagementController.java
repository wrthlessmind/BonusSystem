package com.bonussystem.client.controller.hr;

import com.bonussystem.common.model.Department;
import com.bonussystem.common.model.Employee;
import com.bonussystem.common.model.User;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentManagementController {

    @FXML private TableView<Department> departmentTable;
    @FXML private TableColumn<Department, Integer> colId;
    @FXML private TableColumn<Department, String> colName;
    @FXML private TableColumn<Department, Integer> colHeadUserId;

    @FXML private TextField nameField;
    @FXML private ComboBox<User> headUserComboBox;

    private final Gson gson = GsonProvider.get();
    private final ObservableList<Department> departmentList = FXCollections.observableArrayList();
    private final ObservableList<User> headUserList = FXCollections.observableArrayList();
    private final Map<Integer, Employee> userEmployeeMap = new HashMap<>();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("departmentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        colHeadUserId.setCellValueFactory(new PropertyValueFactory<>("headUserId"));
        colHeadUserId.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer headId, boolean empty) {
                super.updateItem(headId, empty);
                if (empty || headId == null) {
                    setText(null);
                } else {
                    Employee emp = userEmployeeMap.get(headId);
                    setText(emp != null ? emp.getLastName() + " " + emp.getFirstName() : "ID: " + headId);
                }
            }
        });

        headUserComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) { setText(null); return; }
                Employee emp = userEmployeeMap.get(user.getUserId());
                setText(emp != null ? emp.getLastName() + " " + emp.getFirstName() : user.getLogin());
            }
        });
        headUserComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) { setText(null); return; }
                Employee emp = userEmployeeMap.get(user.getUserId());
                setText(emp != null ? emp.getLastName() + " " + emp.getFirstName() : user.getLogin());
            }
        });

        loadHeadUsers();
        loadDepartments();

        departmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameField.setText(newVal.getName());
                headUserList.stream()
                        .filter(u -> u.getUserId() == newVal.getHeadUserId())
                        .findFirst()
                        .ifPresent(headUserComboBox::setValue);
            }
        });
    }

    private void loadHeadUsers() {
        Response response = ServerConnection.getInstance().sendRequest(
                new Request(RequestType.GET_ALL_USERS));
        if (response.getStatus() != ResponseStatus.OK) {
            AlertHelper.showError("Ошибка", "Не удалось загрузить список пользователей");
            return;
        }
        List<User> allUsers = gson.fromJson(response.getBody(), new TypeToken<List<User>>(){}.getType());

        userEmployeeMap.clear();
        headUserList.clear();

        for (User user : allUsers) {
            JsonObject json = new JsonObject();
            json.addProperty("userId", user.getUserId());
            Response empResp = ServerConnection.getInstance().sendRequest(
                    new Request(RequestType.GET_EMPLOYEE_BY_USER_ID, json.toString()));
            if (empResp.getStatus() == ResponseStatus.OK) {
                Employee emp = gson.fromJson(empResp.getBody(), Employee.class);
                if (emp != null) userEmployeeMap.put(user.getUserId(), emp);
            }
            headUserList.add(user);
        }

        headUserComboBox.setItems(headUserList);
    }

    private void loadDepartments() {
        Response response = ServerConnection.getInstance().sendRequest(
                new Request(RequestType.GET_ALL_DEPARTMENTS));
        if (response.getStatus() == ResponseStatus.OK) {
            List<Department> departments = gson.fromJson(
                    response.getBody(), new TypeToken<List<Department>>(){}.getType());
            departmentList.setAll(departments);
            departmentTable.setItems(departmentList);
            departmentTable.refresh();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        if (nameField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Внимание", "Введите название отдела");
            return;
        }
        Department dept = buildDepartmentFromFields();
        Response response = ServerConnection.getInstance().sendRequest(
                new Request(RequestType.ADD_DEPARTMENT, gson.toJson(dept)));
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Отдел добавлен");
            clearFields();
            loadDepartments();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите отдел"); return; }
        if (nameField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Внимание", "Введите название отдела");
            return;
        }
        Department dept = buildDepartmentFromFields();
        dept.setDepartmentId(selected.getDepartmentId());
        Response response = ServerConnection.getInstance().sendRequest(
                new Request(RequestType.UPDATE_DEPARTMENT, gson.toJson(dept)));
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Отдел обновлён");
            clearFields();
            loadDepartments();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите отдел"); return; }
        if (!AlertHelper.showConfirmation("Подтверждение", "Удалить отдел " + selected.getName() + "?")) return;
        JsonObject json = new JsonObject();
        json.addProperty("departmentId", selected.getDepartmentId());
        Response response = ServerConnection.getInstance().sendRequest(
                new Request(RequestType.DELETE_DEPARTMENT, json.toString()));
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Отдел удалён");
            clearFields();
            loadDepartments();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadHeadUsers();
        loadDepartments();
    }

    private Department buildDepartmentFromFields() {
        Department dept = new Department();
        dept.setName(nameField.getText().trim());
        User selectedUser = headUserComboBox.getValue();
        dept.setHeadUserId(selectedUser != null ? selectedUser.getUserId() : null);
        return dept;
    }

    private void clearFields() {
        nameField.clear();
        headUserComboBox.setValue(null);
    }
}