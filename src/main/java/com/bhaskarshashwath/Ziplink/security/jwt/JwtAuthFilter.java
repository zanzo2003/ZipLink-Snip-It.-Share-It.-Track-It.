package com.bhaskarshashwath.Ziplink.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    // using the user details from the spring security
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{

        try{

            // Get token from header
            // validate token
            // if valid get user details
            // -- get username -> load user -> set user details in auth context

            String authToken = jwtUtils.getJwtFromAuthHeader(request);
            if( authToken != null && jwtUtils.validateToken(authToken)){
                String username = jwtUtils.getUsernameFromToken(authToken);
                UserDetails user = userDetailsService.loadUserByUsername(username);

                if(user != null){
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                }
            }
            else{
                throw new Exception();
            }

        }catch(Exception e){}
    }


}
