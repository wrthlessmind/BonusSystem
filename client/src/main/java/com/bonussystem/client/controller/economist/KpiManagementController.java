package com.bonussystem.client.controller.economist;

import com.bonussystem.common.model.KpiIndicator;
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

public class KpiManagementController {

    @FXML private TableView<KpiIndicator> kpiTable;
    @FXML private TableColumn<KpiIndicator, Integer> colId;
    @FXML private TableColumn<KpiIndicator, String> colName;
    @FXML private TableColumn<KpiIndicator, String> colUnit;
    @FXML private TableColumn<KpiIndicator, BigDecimal> colWeight;
    @FXML private TableColumn<KpiIndicator, BigDecimal> colTarget;

    @FXML private TextField nameField;
    @FXML private TextField unitField;
    @FXML private TextField weightField;
    @FXML private TextField targetField;

    private final Gson gson = GsonProvider.get();
    private ObservableList<KpiIndicator> kpiList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("kpiId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        colTarget.setCellValueFactory(new PropertyValueFactory<>("targetValue"));
        loadKpiIndicators();

        kpiTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameField.setText(newVal.getName());
                unitField.setText(newVal.getUnit());
                weightField.setText(newVal.getWeight() != null ? newVal.getWeight().toString() : "");
                targetField.setText(newVal.getTargetValue() != null ? newVal.getTargetValue().toString() : "");
            }
        });
    }

    private void loadKpiIndicators() {
        Request request = new Request(RequestType.GET_ALL_KPI_INDICATORS);
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            List<KpiIndicator> indicators = gson.fromJson(response.getBody(), new TypeToken<List<KpiIndicator>>(){}.getType());
            kpiList.setAll(indicators);
            kpiTable.setItems(kpiList);
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        if (!validateFields()) return;
        KpiIndicator kpi = buildFromFields();
        Request request = new Request(RequestType.ADD_KPI_INDICATOR, gson.toJson(kpi));
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Показатель KPI добавлен");
            clearFields();
            loadKpiIndicators();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        KpiIndicator selected = kpiTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите показатель"); return; }
        if (!validateFields()) return;
        KpiIndicator kpi = buildFromFields();
        kpi.setKpiId(selected.getKpiId());
        Request request = new Request(RequestType.UPDATE_KPI_INDICATOR, gson.toJson(kpi));
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Показатель KPI обновлён");
            clearFields();
            loadKpiIndicators();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        KpiIndicator selected = kpiTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите показатель"); return; }
        if (!AlertHelper.showConfirmation("Подтверждение", "Удалить показатель " + selected.getName() + "?")) return;
        JsonObject json = new JsonObject();
        json.addProperty("kpiId", selected.getKpiId());
        Request request = new Request(RequestType.DELETE_KPI_INDICATOR, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertHelper.showInfo("Успешно", "Показатель KPI удалён");
            clearFields();
            loadKpiIndicators();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleRefresh() { loadKpiIndicators(); }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty() || unitField.getText().trim().isEmpty()
                || weightField.getText().trim().isEmpty() || targetField.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Внимание", "Заполните все поля");
            return false;
        }
        try {
            new BigDecimal(weightField.getText().trim());
            new BigDecimal(targetField.getText().trim());
        } catch (NumberFormatException e) {
            AlertHelper.showError("Ошибка", "Некорректный числовой формат");
            return false;
        }
        return true;
    }

    private KpiIndicator buildFromFields() {
        KpiIndicator kpi = new KpiIndicator();
        kpi.setName(nameField.getText().trim());
        kpi.setUnit(unitField.getText().trim());
        kpi.setWeight(new BigDecimal(weightField.getText().trim()));
        kpi.setTargetValue(new BigDecimal(targetField.getText().trim()));
        return kpi;
    }

    private void clearFields() {
        nameField.clear();
        unitField.clear();
        weightField.clear();
        targetField.clear();
    }
}