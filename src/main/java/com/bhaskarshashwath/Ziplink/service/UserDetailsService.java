package com.bhaskarshashwath.Ziplink.service;

import com.bhaskarshashwath.Ziplink.domain.User;

public interface UserDetailsService {

    User loadUserByUsername(String username);

}
