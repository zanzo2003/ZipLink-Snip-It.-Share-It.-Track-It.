package com.bhaskarshashwath.Ziplink.exception;


import com.bhaskarshashwath.Ziplink.controller.common.ControllerHelper;
import com.bhaskarshashwath.Ziplink.response.ApiResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionhandler {

    @Autowired
    private ControllerHelper controllerHelper;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleIllegalArgument(IllegalArgumentException exception){
        return controllerHelper.createErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ResourceNotFoundExcpetion.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleResourcenotFound(ResourceNotFoundExcpetion exception){
        return controllerHelper.createErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDTO<Object>> usernameNotUnique(UsernameAlreadyExistsException exception){
        return controllerHelper.createErrorResponse(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponseDTO<Object>> invalidCredentials(InvalidCredentialsException exception){
        return controllerHelper.createErrorResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleGenericException(Exception exception) {
        return controllerHelper.createErrorResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
