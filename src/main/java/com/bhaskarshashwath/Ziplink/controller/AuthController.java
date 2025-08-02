package com.bhaskarshashwath.Ziplink.controller;


import com.bhaskarshashwath.Ziplink.controller.common.ControllerHelper;
import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.model.UserDTO;
import com.bhaskarshashwath.Ziplink.model.common.ApiResponseDTO;
import com.bhaskarshashwath.Ziplink.service.UserService;
import com.bhaskarshashwath.Ziplink.service.impl.UserDetailsServiceImpl;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
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
    private UserService userService;

    @Autowired
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

}
