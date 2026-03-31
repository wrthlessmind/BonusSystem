package com.bonussystem.client.tcp;

import com.bonussystem.common.tcp.Request;
import com.bonussystem.common.tcp.Response;
import com.bonussystem.common.util.GsonProvider;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ServerConnection {

    private static ServerConnection instance;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final Gson gson = GsonProvider.get();

    private ServerConnection() {
        try {
            Properties props = new Properties();
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("client.properties")) {
                if (is == null) throw new RuntimeException("Файл client.properties не найден");
                props.load(is);
            }
            String host = props.getProperty("server.host", "localhost");
            int port = Integer.parseInt(props.getProperty("server.port", "5555"));

            socket = new Socket(host, port);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось подключиться к серверу", e);
        }
    }

    public static synchronized ServerConnection getInstance() {
        if (instance == null) {
            instance = new ServerConnection();
        }
        return instance;
    }

    public synchronized Response sendRequest(Request request) {
        try {
            String json = gson.toJson(request);
            out.println(json);
            String responseLine = in.readLine();
            if (responseLine == null) {
                throw new RuntimeException("Сервер закрыл соединение");
            }
            return gson.fromJson(responseLine, Response.class);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка связи с сервером", e);
        }
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}