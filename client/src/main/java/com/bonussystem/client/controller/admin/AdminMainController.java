package com.bonussystem.client.controller.admin;

import com.bonussystem.client.util.SceneManager;
import com.bonussystem.client.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class AdminMainController {

    @FXML private StackPane contentArea;
    @FXML private Label userLabel;

    @FXML
    private void initialize() {
        userLabel.setText("Вы вошли как: " + Session.getCurrentUser().getLogin());
        showUsers();
    }

    @FXML
    private void showUsers() {
        SceneManager.loadPane("/fxml/admin/user_management.fxml", contentArea);
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