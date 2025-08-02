package com.bhaskarshashwath.Ziplink.service;

import com.bhaskarshashwath.Ziplink.domain.User;

public interface UserService {

    User registerUser(User newUser);
    User getByUsername(String username);
    User getByEmail(String email);

}
