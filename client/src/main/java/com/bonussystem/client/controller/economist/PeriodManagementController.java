package com.bonussystem.client.controller.economist;

import com.bonussystem.common.model.Period;
import com.bonussystem.common.model.enums.PeriodStatus;
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

import java.math.BigDecimal;
import java.util.List;

public class PeriodManagementController {

    @FXML private TableView<Period> periodTable;
    @FXML private TableColumn<Period, Integer> colYear;
    @FXML private TableColumn<Period, BigDecimal> colBonusFund;
    @FXML private TableColumn<Period, PeriodStatus> colStatus;

    @FXML private TextField yearField;
    @FXML private TextField bonusFundField;

    private final Gson gson = GsonProvider.get();
    private ObservableList<Period> periodList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colBonusFund.setCellValueFactory(new PropertyValueFactory<>("bonusFund"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadPeriods();

        periodTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                yearField.setText(String.valueOf(newVal.getYear()));
                bonusFundField.setText(newVal.getBonusFund() != null ? newVal.getBonusFund().toString() : "");
            }
        });
    }

    private void loadPeriods() {
        Request request = new Request(RequestType.GET_ALL_PERIODS);
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            List<Period> periods = gson.fromJson(response.getBody(), new TypeToken<List<Period>>(){}.getType());
            periodList.setAll(periods);
            periodTable.setItems(periodList);
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        if (!validateFields()) return;
        Period period = new Period();
        period.setYear(Integer.parseInt(yearField.getText().trim()));
        period.setBonusFund(new BigDecimal(bonusFundField.getText().trim()));
        period.setStatus(PeriodStatus.OPEN);
        Request request = new Request(RequestType.ADD_PERIOD, gson.toJson(period));
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Период добавлен");
            clearFields();
            loadPeriods();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Period selected = periodTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите период"); return; }
        if (bonusFundField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Внимание", "Введите размер фонда");
            return;
        }
        try {
            new BigDecimal(bonusFundField.getText().trim());
        } catch (NumberFormatException e) {
            AlertHelper.showError("Ошибка", "Некорректный числовой формат");
            return;
        }
        Period period = new Period();
        period.setYear(selected.getYear());
        period.setBonusFund(new BigDecimal(bonusFundField.getText().trim()));
        period.setStatus(selected.getStatus());
        Request request = new Request(RequestType.UPDATE_PERIOD, gson.toJson(period));
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Период обновлён");
            loadPeriods();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Period selected = periodTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите период"); return; }
        if (!AlertHelper.showConfirmation("Подтверждение", "Удалить период " + selected.getYear() + "?")) return;
        JsonObject json = new JsonObject();
        json.addProperty("year", selected.getYear());
        Request request = new Request(RequestType.DELETE_PERIOD, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Период удалён");
            clearFields();
            loadPeriods();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleRefresh() { loadPeriods(); }

    private boolean validateFields() {
        if (yearField.getText().trim().isEmpty() || bonusFundField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Внимание", "Заполните все поля");
            return false;
        }
        try {
            Integer.parseInt(yearField.getText().trim());
            new BigDecimal(bonusFundField.getText().trim());
        } catch (NumberFormatException e) {
            AlertHelper.showError("Ошибка", "Некорректный числовой формат");
            return false;
        }
        return true;
    }

    private void clearFields() {
        yearField.clear();
        bonusFundField.clear();
    }
}