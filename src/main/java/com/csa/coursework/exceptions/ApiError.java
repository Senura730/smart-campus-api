package com.csa.coursework.exceptions;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ApiError {
    private String status;
    private String error;
    private String message;

    public ApiError() {}

    public ApiError(String status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}