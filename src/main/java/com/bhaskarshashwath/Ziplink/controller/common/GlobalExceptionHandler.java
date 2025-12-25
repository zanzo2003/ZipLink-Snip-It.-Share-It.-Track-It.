package com.bhaskarshashwath.Ziplink.controller.common;

import com.bhaskarshashwath.Ziplink.exception.InvalidCredentialsException;
import com.bhaskarshashwath.Ziplink.exception.ResourceNotFoundExcpetion;
import com.bhaskarshashwath.Ziplink.exception.UsernameAlreadyExistsException;
import com.bhaskarshashwath.Ziplink.model.response.ApiResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ControllerHelper controllerHelper;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleIllegalArgument(IllegalArgumentException exception) {
        log.warn("IllegalArgumentException: {}", exception.getMessage());
        return controllerHelper.createErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundExcpetion.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleResourcenotFound(ResourceNotFoundExcpetion exception) {
        log.warn("ResourceNotFoundException: {}", exception.getMessage());
        return controllerHelper.createErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDTO<Object>> usernameNotUnique(UsernameAlreadyExistsException exception) {
        log.warn("UsernameAlreadyExistsException: {}", exception.getMessage());
        return controllerHelper.createErrorResponse(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponseDTO<Object>> invalidCredentials(InvalidCredentialsException exception) {
        log.warn("InvalidCredentialsException: {}", exception.getMessage());
        return controllerHelper.createErrorResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleGenericException(Exception exception) {
        log.error("Unhandled Exception occurred: {}", exception.getMessage(), exception);
        return controllerHelper.createErrorResponse("An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
