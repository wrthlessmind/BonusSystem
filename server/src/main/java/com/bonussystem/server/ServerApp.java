package com.bonussystem.server;

import com.bonussystem.server.config.ServerConfig;
import com.bonussystem.server.tcp.ClientHandler;
import com.bonussystem.server.util.FileLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {

    public static void main(String[] args) {
        int port = ServerConfig.getInstance().getServerPort();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            FileLogger.log("Сервер запущен на порту " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread thread = new Thread(new ClientHandler(clientSocket));
                thread.start();
            }

        } catch (IOException e) {
            FileLogger.log("Ошибка запуска сервера", e);
            throw new RuntimeException("Не удалось запустить сервер на порту " + port, e);
        }
    }
}