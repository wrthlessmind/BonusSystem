package com.bonussystem.client.controller;

import com.bonussystem.common.model.User;
import com.bonussystem.common.tcp.Request;
import com.bonussystem.common.tcp.Response;
import com.bonussystem.common.tcp.enums.RequestType;
import com.bonussystem.common.tcp.enums.ResponseStatus;
import com.bonussystem.common.util.GsonProvider;
import com.bonussystem.client.tcp.ServerConnection;
import com.bonussystem.client.util.AlertHelper;
import com.bonussystem.client.util.SceneManager;
import com.bonussystem.client.util.Session;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;

    private final Gson gson = GsonProvider.get();

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            AlertHelper.showWarning("Внимание", "Заполните все поля");
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("login", login);
        json.addProperty("password", password);

        Request request = new Request(RequestType.LOGIN, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);

        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertHelper.showError("Ошибка входа", response.getMessage());
            return;
        }

        User user = gson.fromJson(response.getBody(), User.class);
        Session.setCurrentUser(user);

        switch (user.getRole()) {
            case ADMIN:
                SceneManager.switchScene("/fxml/admin/admin_main.fxml", "Панель администратора");
                break;
            case HR_MANAGER:
                SceneManager.switchScene("/fxml/hr/hr_main.fxml", "Панель HR-менеджера");
                break;
            case LABOR_ECONOMIST:
                SceneManager.switchScene("/fxml/economist/economist_main.fxml", "Панель экономиста по труду");
                break;
            case DEPARTMENT_HEAD:
                SceneManager.switchScene("/fxml/head/head_main.fxml", "Панель руководителя отдела");
                break;
        }
    }

    @FXML
    private void handleGoToRegister() {
        SceneManager.switchScene("/fxml/register.fxml", "Регистрация");
    }
}