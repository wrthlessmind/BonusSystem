package com.bonussystem.client.controller.economist;

import com.bonussystem.client.util.SceneManager;
import com.bonussystem.client.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class EconomistMainController {

    @FXML private StackPane contentArea;
    @FXML private Label userLabel;

    @FXML
    private void initialize() {
        userLabel.setText("Вы вошли как: " + Session.getCurrentUser().getLogin());
        showKpi();
    }

    @FXML private void showKpi() {
        SceneManager.loadPane("/fxml/economist/kpi_management.fxml", contentArea);
    }
    @FXML private void showPeriods() {
        SceneManager.loadPane("/fxml/economist/period_management.fxml", contentArea);
    }
    @FXML private void showBonusCalculation() {
        SceneManager.loadPane("/fxml/economist/bonus_calculation.fxml", contentArea);
    }
    @FXML private void showReport() {
        SceneManager.loadPane("/fxml/economist/report.fxml", contentArea);
    }
    @FXML private void showStatistics() {
        SceneManager.loadPane("/fxml/economist/statistics.fxml", contentArea);
    }
    @FXML private void showProfile() {
        SceneManager.loadPane("/fxml/profile.fxml", contentArea);
    }
    @FXML private void handleLogout() {
        Session.clear();
        SceneManager.switchScene("/fxml/login.fxml", "Авторизация");
    }
}