package com.bhaskarshashwath.Ziplink.service.impl;

import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.service.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    public User loadUserByUsername(String username){
        return null;
    }
}
