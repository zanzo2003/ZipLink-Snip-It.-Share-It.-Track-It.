package com.bhaskarshashwath.Ziplink.service;

import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.model.JwtAuthDTO;
import com.bhaskarshashwath.Ziplink.model.LoginDTO;

public interface UserService {

    User registerUser(User newUser);
    User getByUsername(String username);
    User getByEmail(String email);
    JwtAuthDTO authenticateUser(LoginDTO loginDetails);

}
