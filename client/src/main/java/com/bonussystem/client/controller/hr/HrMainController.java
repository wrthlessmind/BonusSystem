package com.bonussystem.client.controller.hr;

import com.bonussystem.client.util.SceneManager;
import com.bonussystem.client.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class HrMainController {

    @FXML private StackPane contentArea;
    @FXML private Label userLabel;

    @FXML
    private void initialize() {
        userLabel.setText("Вы вошли как: " + Session.getCurrentUser().getLogin());
        showEmployees();
    }

    @FXML
    private void showEmployees() {
        SceneManager.loadPane("/fxml/hr/employee_management.fxml", contentArea);
    }

    @FXML
    private void showDepartments() {
        SceneManager.loadPane("/fxml/hr/department_management.fxml", contentArea);
    }

    @FXML
    private void showProfile() {
        SceneManager.loadPane("/fxml/profile.fxml", contentArea);
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        SceneManager.switchScene("/fxml/login.fxml", "Авторизация");
    }
}