package com.bonussystem.client.controller;

import com.bonussystem.common.tcp.Request;
import com.bonussystem.common.tcp.Response;
import com.bonussystem.common.tcp.enums.RequestType;
import com.bonussystem.common.tcp.enums.ResponseStatus;
import com.bonussystem.client.tcp.ServerConnection;
import com.bonussystem.client.util.AlertHelper;
import com.bonussystem.client.util.SceneManager;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class RegisterController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML private Text firstNameLabel;
    @FXML private TextField firstNameField;
    @FXML private Text lastNameLabel;
    @FXML private TextField lastNameField;

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll(
                "HR-менеджер",
                "Экономист по труду",
                "Руководитель отдела"
        );
    }

    @FXML
    private void handleRoleChanged() {
        String selected = roleComboBox.getValue();
        boolean needsName = selected != null;
        firstNameLabel.setVisible(needsName);
        firstNameField.setVisible(needsName);
        lastNameLabel.setVisible(needsName);
        lastNameField.setVisible(needsName);
    }

    @FXML
    private void handleRegister() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String selectedRole = roleComboBox.getValue();

        if (login.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || selectedRole == null) {
            AlertHelper.showWarning("Внимание", "Заполните все поля");
            return;
        }
        if (!password.equals(confirmPassword)) {
            AlertHelper.showError("Ошибка", "Пароли не совпадают");
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            AlertHelper.showWarning("Внимание", "Введите имя и фамилию");
            return;
        }

        String roleDbValue = mapRoleToDbValue(selectedRole);

        JsonObject json = new JsonObject();
        json.addProperty("login", login);
        json.addProperty("password", password);
        json.addProperty("role", roleDbValue);
        json.addProperty("firstName", firstName);
        json.addProperty("lastName", lastName);

        Request request = new Request(RequestType.REGISTER, json.toString());
        Response response = ServerConnection.getInstance().sendRequest(request);

        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertHelper.showError("Ошибка регистрации", response.getMessage());
            return;
        }

        AlertHelper.showInfo("Успешно", "Регистрация прошла успешно. Теперь войдите в систему.");
        SceneManager.switchScene("/fxml/login.fxml", "Авторизация");
    }

    @FXML
    private void handleGoToLogin() {
        SceneManager.switchScene("/fxml/login.fxml", "Авторизация");
    }

    private String mapRoleToDbValue(String displayName) {
        switch (displayName) {
            case "HR-менеджер": return "hr_manager";
            case "Экономист по труду": return "labor_economist";
            case "Руководитель отдела": return "department_head";
            default: throw new RuntimeException("Неизвестная роль: " + displayName);
        }
    }
}