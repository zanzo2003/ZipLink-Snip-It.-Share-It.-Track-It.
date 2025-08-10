package com.bhaskarshashwath.Ziplink.controller.common;

import com.bhaskarshashwath.Ziplink.response.ApiResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ControllerHelper {

    public <T> ResponseEntity<ApiResponseDTO<T>> createSuccessResponse(T data, String message, HttpStatus status) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setStatus(status.value());
        response.setSuccessMessage(message);
        response.setData(data);
        return new ResponseEntity<>(response, status);
    }

    public <T> ResponseEntity<Void> redirectResponse( String message, HttpStatus status, HttpHeaders headers) {
        return ResponseEntity.status(status).headers(headers).build();
    }

    public ResponseEntity<ApiResponseDTO<Object>> createSuccessResponse(String message, HttpStatus status) {
        ApiResponseDTO<Object> response = new ApiResponseDTO<>();
        response.setStatus(status.value());
        response.setSuccessMessage(message);
        response.setData(null);
        return new ResponseEntity<>(response, status);
    }

    public ResponseEntity<ApiResponseDTO<Object>> createErrorResponse(String message, HttpStatus status) {
        ApiResponseDTO<Object> response = new ApiResponseDTO<>();
        response.setStatus(status.value());
        response.setSuccessMessage(message);
        response.setData(null);
        return new ResponseEntity<>(response, status);
    }


    public <T> ResponseEntity<ApiResponseDTO<T>> createOkResponse(T data, String message) {
        return createSuccessResponse(data, message, HttpStatus.OK);
    }


    public <T> ResponseEntity<ApiResponseDTO<T>> createCreatedResponse(T data, String message) {
        return createSuccessResponse(data, message, HttpStatus.CREATED);
    }
}
