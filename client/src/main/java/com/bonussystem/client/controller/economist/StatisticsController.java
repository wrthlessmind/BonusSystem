package com.bonussystem.client.controller.economist;

import com.bonussystem.common.tcp.Request;
import com.bonussystem.common.tcp.Response;
import com.bonussystem.common.tcp.enums.RequestType;
import com.bonussystem.common.tcp.enums.ResponseStatus;
import com.bonussystem.client.tcp.ServerConnection;
import com.bonussystem.client.util.AlertHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class StatisticsController {

    @FXML private TextField yearField;
    @FXML private Label totalBonusesLabel;
    @FXML private Label avgBonusLabel;
    @FXML private Label maxKpiLabel;
    @FXML private Label minKpiLabel;
    @FXML private Label achievedPercentLabel;

    @FXML
    private void handleLoadStatistics() {
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
        Request request = new Request(RequestType.GET_STATISTICS, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            JsonObject stats = JsonParser.parseString(response.getBody()).getAsJsonObject();
            totalBonusesLabel.setText(stats.get("totalBonuses").getAsString() + " BYN");
            avgBonusLabel.setText(stats.get("avgBonus").getAsString() + " BYN");
            maxKpiLabel.setText(stats.get("maxKpiScore").getAsString() + "%");
            minKpiLabel.setText(stats.get("minKpiScore").getAsString() + "%");
            achievedPercentLabel.setText(stats.get("achievedPercent").getAsString() + "%");
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }
}