package com.example.productDemo.Controller;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String code;
    private String message;
    private T data;
    private List<String> errors;

    // Constructor for success response
    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
 
    }

    // Constructor for error response
    public ApiResponse(String code, String message, List<String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    // Getters...
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public List<String> getErrors() {
        return errors;
    }
}