package com.bonussystem.client.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) { primaryStage = stage; }
    public static Stage getPrimaryStage() { return primaryStage; }

    public static void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки сцены: " + fxmlPath, e);
        }
    }

    public static <T> T switchSceneAndGetController(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки сцены: " + fxmlPath, e);
        }
    }

    public static <T> T loadPane(String fxmlPath, Pane container) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            container.getChildren().setAll(root);
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки панели: " + fxmlPath, e);
        }
    }
}