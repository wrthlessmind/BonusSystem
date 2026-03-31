package com.bonussystem.common.tcp;

import com.bonussystem.common.tcp.enums.ResponseStatus;

import java.io.Serial;
import java.io.Serializable;

public class Response implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ResponseStatus status;
    private String message;
    private String body;

    public Response() {}

    public Response(ResponseStatus status, String message, String body) {
        this.status = status;
        this.message = message;
        this.body = body;
    }

    public static Response ok(String body) {
        return new Response(ResponseStatus.OK, null, body);
    }

    public static Response ok(String message, String body) {
        return new Response(ResponseStatus.OK, message, body);
    }

    public static Response error(String message) {
        return new Response(ResponseStatus.ERROR, message, null);
    }

    public ResponseStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public String getBody() { return body; }

    public void setStatus(ResponseStatus status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setBody(String body) { this.body = body; }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}