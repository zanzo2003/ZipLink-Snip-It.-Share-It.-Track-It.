package com.bhaskarshashwath.Ziplink.service.impl;

import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.exception.ResourceNotFoundExcpetion;
import com.bhaskarshashwath.Ziplink.exception.UsernameAlreadyExistsException;
import com.bhaskarshashwath.Ziplink.repository.UserRepository;
import com.bhaskarshashwath.Ziplink.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;




@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public User registerUser(User newUser){
        // Check if username already exists
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + newUser.getUsername());
        }

        // Check if email already exists (optional but recommended)
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new UsernameAlreadyExistsException("Email already exists: " + newUser.getEmail());
        }

        // Encode password and save user
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(newUser);
    }

    @Override
    public User getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new ResourceNotFoundExcpetion("user not found"));
        return user;
    }

    @Override
    public User getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundExcpetion("user not found"));
        return user;
    }
}
