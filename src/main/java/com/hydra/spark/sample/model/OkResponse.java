package com.hydra.spark.sample.model;

public class OkResponse {

    private int status;

    private String message;

    private String data;

    public OkResponse(){}

    public OkResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public OkResponse(int status, String message, String data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
