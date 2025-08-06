package com.bhaskarshashwath.Ziplink.controller;


import com.bhaskarshashwath.Ziplink.controller.common.ControllerHelper;
import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.model.JwtAuthDTO;
import com.bhaskarshashwath.Ziplink.model.LoginDTO;
import com.bhaskarshashwath.Ziplink.model.UserDTO;
import com.bhaskarshashwath.Ziplink.response.ApiResponseDTO;
import com.bhaskarshashwath.Ziplink.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthController {


    private UserService userService;

    private ControllerHelper controllerHelper;

    @PostMapping("/public/register")
    public ResponseEntity<ApiResponseDTO<UserDTO>> registerUser(
            @RequestBody @NonNull UserDTO newUserDetails
            ){

        User newUser = new User();
        newUser.setUsername(newUserDetails.getUsername());
        newUser.setEmail(newUserDetails.getEmail());
        newUser.setPassword(newUserDetails.getPassword());
        newUser.setRole("ROLE_USER");
        newUser = userService.registerUser(newUser);
        BeanUtils.copyProperties(newUser, newUserDetails);
        return controllerHelper.createOkResponse(newUserDetails, "registration successful");
    }

    @PostMapping("/public/login")
    public ResponseEntity<ApiResponseDTO<JwtAuthDTO>> loginUser(
            @RequestBody @NonNull LoginDTO loginDetails
            ){
        return controllerHelper.createOkResponse(userService.authenticateUser(loginDetails), "Login Successful");
    }

}
