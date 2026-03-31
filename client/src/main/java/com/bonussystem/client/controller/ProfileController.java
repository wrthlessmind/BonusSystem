package com.bonussystem.client.controller;

import com.bonussystem.common.model.User;
import com.bonussystem.common.model.enums.Role;
import com.bonussystem.common.tcp.Request;
import com.bonussystem.common.tcp.Response;
import com.bonussystem.common.tcp.enums.RequestType;
import com.bonussystem.common.tcp.enums.ResponseStatus;
import com.bonussystem.client.tcp.ServerConnection;
import com.bonussystem.client.util.AlertHelper;
import com.bonussystem.client.util.SceneManager;
import com.bonussystem.client.util.Session;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProfileController {

    @FXML private Label roleLabel;
    @FXML private TextField loginField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML
    private void initialize() {
        User user = Session.getCurrentUser();
        loginField.setText(user.getLogin());
        roleLabel.setText("Роль: " + mapRoleToDisplay(user.getRole()));
    }

    @FXML
    private void handleSave() {
        String newLogin = loginField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newLogin.isEmpty()) {
            AlertHelper.showWarning("Внимание", "Логин не может быть пустым");
            return;
        }
        if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            AlertHelper.showError("Ошибка", "Пароли не совпадают");
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("userId", Session.getCurrentUser().getUserId());
        json.addProperty("newLogin", newLogin);
        if (!newPassword.isEmpty()) {
            json.addProperty("newPassword", newPassword);
        }

        Request request = new Request(RequestType.UPDATE_PROFILE, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            Session.getCurrentUser().setLogin(newLogin);
            AlertHelper.showInfo("Успешно", "Профиль обновлён");
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        User user = Session.getCurrentUser();
        switch (user.getRole()) {
            case ADMIN: SceneManager.switchScene("/fxml/admin/admin_main.fxml", "Панель администратора"); break;
            case HR_MANAGER: SceneManager.switchScene("/fxml/hr/hr_main.fxml", "Панель HR-менеджера"); break;
            case LABOR_ECONOMIST: SceneManager.switchScene("/fxml/economist/economist_main.fxml", "Панель экономиста по труду"); break;
            case DEPARTMENT_HEAD: SceneManager.switchScene("/fxml/head/head_main.fxml", "Панель руководителя отдела"); break;
        }
    }

    private String mapRoleToDisplay(Role role) {
        switch (role) {
            case ADMIN: return "Администратор";
            case HR_MANAGER: return "HR-менеджер";
            case LABOR_ECONOMIST: return "Экономист по труду";
            case DEPARTMENT_HEAD: return "Руководитель отдела";
            default: return role.name();
        }
    }
}