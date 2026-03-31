package com.bonussystem.server.tcp;

import com.bonussystem.common.tcp.Request;
import com.bonussystem.common.tcp.Response;
import com.bonussystem.common.util.GsonProvider;
import com.bonussystem.server.db.DatabaseConnection;
import com.bonussystem.server.util.FileLogger;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final Gson gson = GsonProvider.get();
    private final RequestRouter router = new RequestRouter();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String clientAddr = socket.getInetAddress().getHostAddress();
        FileLogger.log("Клиент подключён: " + clientAddr);

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line;
            while ((line = in.readLine()) != null) {
                Request request = gson.fromJson(line, Request.class);
                FileLogger.log("Запрос от " + clientAddr + ": " + request.getRequestType());

                Response response = router.route(request);

                out.println(gson.toJson(response));
                FileLogger.log("Ответ для " + clientAddr + ": " + response.getStatus());
            }

        } catch (IOException e) {
            FileLogger.log("Клиент отключён: " + clientAddr, e);
        } finally {
            DatabaseConnection.closeForCurrentThread();
            try {
                socket.close();
            } catch (IOException e) {
                FileLogger.log("Ошибка закрытия сокета", e);
            }
        }
    }
}