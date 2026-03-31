package com.bonussystem.common.tcp;

import com.bonussystem.common.tcp.enums.RequestType;

import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private RequestType requestType;
    private String body;

    public Request() {}

    public Request(RequestType requestType, String body) {
        this.requestType = requestType;
        this.body = body;
    }

    public Request(RequestType requestType) {
        this.requestType = requestType;
        this.body = null;
    }

    public RequestType getRequestType() { return requestType; }
    public String getBody() { return body; }

    public void setRequestType(RequestType requestType) { this.requestType = requestType; }
    public void setBody(String body) { this.body = body; }

    @Override
    public String toString() {
        return "Request{" +
                "requestType=" + requestType +
                ", body='" + (body != null && body.length() > 50 ? body.substring(0, 50) + "..." : body) + '\'' +
                '}';
    }
}