package com.group4.cursus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyCustomResponse {
    private final String message;
    private final Boolean success;
    private final Map<String, String> data;

    public MyCustomResponse(String message, Boolean success) {
        this.message = message;
        this.success = success;
         this.data = new HashMap<>();
    }

    public MyCustomResponse(String message, Boolean success, Map<String, String> data) {
        this.message = message;
        this.success = success;
        this.data = data;
    }

    public static MyCustomResponse fail(String message) {
        return new MyCustomResponse(message, false);
    }

    public boolean isSuccess() {
        return true;
    }

    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }
}
