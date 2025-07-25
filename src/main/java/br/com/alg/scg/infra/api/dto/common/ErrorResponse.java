package br.com.alg.scg.infra.api.dto.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        @JsonProperty("timestamp")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        LocalDateTime timestamp,
        
        @JsonProperty("status")
        int status,
        
        @JsonProperty("error")
        String error,
        
        @JsonProperty("message")
        String message,
        
        @JsonProperty("path")
        String path,
        
        @JsonProperty("details")
        List<String> details
) {
    
    public static ErrorResponse create(int status, String error, String message, String path) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status,
            error,
            message,
            path,
            null
        );
    }
    
    public static ErrorResponse create(int status, String error, String message, String path, List<String> details) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status,
            error,
            message,
            path,
            details
        );
    }
}