package com.bhaskarshashwath.Ziplink.service.impl;

import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.exception.InvalidCredentialsException;
import com.bhaskarshashwath.Ziplink.exception.ResourceNotFoundExcpetion;
import com.bhaskarshashwath.Ziplink.exception.UsernameAlreadyExistsException;
import com.bhaskarshashwath.Ziplink.model.JwtAuthDTO;
import com.bhaskarshashwath.Ziplink.model.LoginDTO;
import com.bhaskarshashwath.Ziplink.repository.UserRepository;
import com.bhaskarshashwath.Ziplink.security.jwt.JwtUtils;
import com.bhaskarshashwath.Ziplink.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;




@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public User registerUser(User newUser) throws UsernameAlreadyExistsException{

        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + newUser.getUsername());
        }

        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new UsernameAlreadyExistsException("Email already exists: " + newUser.getEmail());
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(newUser);
    }

    @Override
    public User getByUsername(String username) throws ResourceNotFoundExcpetion{
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new ResourceNotFoundExcpetion("user not found"));
        return user;
    }

    @Override
    public User getByEmail(String email) throws ResourceNotFoundExcpetion{
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundExcpetion("user not found"));
        return user;
    }

    @Override
    public JwtAuthDTO authenticateUser(LoginDTO loginDetails) throws InvalidCredentialsException, ResourceNotFoundExcpetion {
//        User userDetails = getByUsername(loginDetails.getUsername());
//        if( !passwordEncoder.matches(loginDetails.getPassword(), userDetails.getPassword())){
//            throw new InvalidCredentialsException("Invalid Credentials");
//        }
//        UserDetailsImpl details = UserDetailsImpl.build(userDetails);
//        return JwtAuthDTO.builder().token(jwtUtils.generateToken(details)).build();

        JwtAuthDTO dto = null;
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDetails.getUsername(), loginDetails.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
            dto = JwtAuthDTO.builder().token(jwtUtils.generateToken(details)).build();
        }
        catch(AuthenticationException exception){
            throw new InvalidCredentialsException("Invalid Credentials");
        }
        return dto;
    }


}
