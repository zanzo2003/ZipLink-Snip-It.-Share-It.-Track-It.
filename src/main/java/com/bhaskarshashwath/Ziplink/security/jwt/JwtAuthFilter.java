package com.bhaskarshashwath.Ziplink.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;



@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    // using the user details from the spring security
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{

        try{
            String authToken = jwtUtils.getJwtFromAuthHeader(request);
            if( authToken != null && jwtUtils.validateToken(authToken)){
                String username = jwtUtils.getUsernameFromToken(authToken);
                UserDetails user = userDetailsService.loadUserByUsername(username);

                if(user != null){

                    /*
                    creating the UsernamePasswordAuthenticationToken object and using WebAuthenticationDetailsSource to converting
                    HttpServletRequest into WebAuthenticationDetails class then setting the details in security context
                     */
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            else{
                throw new Exception();
            }

        }catch(Exception e){
            log.error("Error setting User Details in Security Context : ", e.getStackTrace());
        }
    }


}
