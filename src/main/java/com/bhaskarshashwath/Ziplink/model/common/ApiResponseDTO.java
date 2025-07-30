package com.bhaskarshashwath.Ziplink.model.common;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponseDTO <T> {

    private int status;
    private String successMessage;
    private T data;
    private LocalDateTime timeStamp = LocalDateTime.now();

}
