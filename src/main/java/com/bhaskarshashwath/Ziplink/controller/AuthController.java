package com.bhaskarshashwath.Ziplink.controller;


import com.bhaskarshashwath.Ziplink.controller.common.ControllerHelper;
import com.bhaskarshashwath.Ziplink.model.UserDTO;
import com.bhaskarshashwath.Ziplink.model.common.ApiResponseDTO;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {


    @Autowired
    private ControllerHelper controllerHelper;

    @PostMapping("/public/register")
    public ResponseEntity<ApiResponseDTO> registerUser(
            @RequestBody @NonNull UserDTO newUserDetails
            ){


    }

}
