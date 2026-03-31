package com.bonussystem.client.controller.admin;

import com.bonussystem.common.model.User;
import com.bonussystem.common.model.enums.Role;
import com.bonussystem.common.model.enums.UserStatus;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class UserManagementController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colLogin;
    @FXML private TableColumn<User, Role> colRole;
    @FXML private TableColumn<User, UserStatus> colStatus;

    private final Gson gson = GsonProvider.get();
    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadUsers();
    }

    private void loadUsers() {
        Request request = new Request(RequestType.GET_ALL_USERS);
        Response response = ServerConnection.getInstance().sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            List<User> users = gson.fromJson(response.getBody(), new TypeToken<List<User>>(){}.getType());
            userList.setAll(users);
            userTable.setItems(userList);
        } else {
            AlertHelper.showError("Ошибка", response.getMessage());
        }
    }

    @FXML
    private void handleBlockUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите пользователя"); return; }
        if (!AlertHelper.showConfirmation("Подтверждение", "Заблокировать пользователя " + selected.getLogin() + "?")) return;
        JsonObject json = new JsonObject();
        json.addProperty("userId", selected.getUserId());
        Response response = ServerConnection.getInstance().sendRequest(new Request(RequestType.BLOCK_USER, json.toString()));
        if (response.getStatus() == ResponseStatus.OK) { AlertHelper.showInfo("Успешно", "Пользователь заблокирован"); loadUsers(); }
        else AlertHelper.showError("Ошибка", response.getMessage());
    }

    @FXML
    private void handleUnblockUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите пользователя"); return; }
        JsonObject json = new JsonObject();
        json.addProperty("userId", selected.getUserId());
        Response response = ServerConnection.getInstance().sendRequest(new Request(RequestType.UNBLOCK_USER, json.toString()));
        if (response.getStatus() == ResponseStatus.OK) { AlertHelper.showInfo("Успешно", "Пользователь разблокирован"); loadUsers(); }
        else AlertHelper.showError("Ошибка", response.getMessage());
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Внимание", "Выберите пользователя"); return; }
        if (!AlertHelper.showConfirmation("Подтверждение", "Удалить пользователя " + selected.getLogin() + "?")) return;
        JsonObject json = new JsonObject();
        json.addProperty("userId", selected.getUserId());
        Response response = ServerConnection.getInstance().sendRequest(new Request(RequestType.DELETE_USER, json.toString()));
        if (response.getStatus() == ResponseStatus.OK) { AlertHelper.showInfo("Успешно", "Пользователь удалён"); loadUsers(); }
        else AlertHelper.showError("Ошибка", response.getMessage());
    }

    @FXML
    private void handleRefresh() { loadUsers(); }
}