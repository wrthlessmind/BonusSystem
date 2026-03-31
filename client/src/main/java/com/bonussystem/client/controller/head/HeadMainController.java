package com.bonussystem.client.controller.head;

import com.bonussystem.client.util.SceneManager;
import com.bonussystem.client.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class HeadMainController {

    @FXML private StackPane contentArea;
    @FXML private Label userLabel;

    @FXML
    private void initialize() {
        userLabel.setText("Вы вошли как: " + Session.getCurrentUser().getLogin());
        showKpiResults();
    }

    @FXML private void showKpiResults() {
        SceneManager.loadPane("/fxml/head/kpi_results.fxml", contentArea);
    }
    @FXML private void showDepartmentResults() {
        SceneManager.loadPane("/fxml/head/department_results.fxml", contentArea);
    }
    @FXML private void showProfile() {
        SceneManager.loadPane("/fxml/profile.fxml", contentArea);
    }
    @FXML private void handleLogout() {
        Session.clear();
        SceneManager.switchScene("/fxml/login.fxml", "Авторизация");
    }
}