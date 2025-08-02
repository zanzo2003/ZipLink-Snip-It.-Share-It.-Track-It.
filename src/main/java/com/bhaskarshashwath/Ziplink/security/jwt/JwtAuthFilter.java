//package com.bhaskarshashwath.Ziplink.security.jwt;
//
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//
//
//@Component
//@Slf4j
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtils jwtUtils;
//
//    // using the user details from the spring security
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
//
//        try{
//            String authToken = jwtUtils.getJwtFromAuthHeader(request);
//            if( authToken != null && jwtUtils.validateToken(authToken)){
//                String username = jwtUtils.getUsernameFromToken(authToken);
//                UserDetails user = userDetailsService.loadUserByUsername(username);
//
//                if(user != null){
//
//                    /*
//                    creating the UsernamePasswordAuthenticationToken object and using WebAuthenticationDetailsSource to convert
//                    HttpServletRequest into WebAuthenticationDetails class then setting the details in security context
//                     */
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                }
//            }
//            else{
//                log.error("User not found");
//                throw new UsernameNotFoundException("User not found");
//            }
//
//        }catch(UsernameNotFoundException e){
//            log.error("User not found in records : {}", e.getStackTrace());
//        } catch (RuntimeException e) {
//            log.error("Error setting user details in security context");
//            throw new RuntimeException(e);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//
//}



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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authToken = jwtUtils.getJwtFromAuthHeader(request);

            // Only process if token exists and is valid
            if (authToken != null && jwtUtils.validateToken(authToken)) {
                String username = jwtUtils.getUsernameFromToken(authToken);

                // Only set authentication if no authentication exists in context
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    try {
                        UserDetails user = userDetailsService.loadUserByUsername(username);

                        if (user != null) {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            log.debug("Successfully authenticated user: {}", username);
                        }
                    } catch (UsernameNotFoundException e) {
                        log.warn("User not found in database: {}", username);
                        // Don't set authentication, let the request continue
                    }
                }
            } else {
                // No token or invalid token - this is normal for public endpoints
                log.debug("No valid JWT token found in request headers");
            }

        } catch (Exception e) {
            log.error("Error processing JWT authentication: {}", e.getMessage());
            // Clear any partial authentication that might have been set
            SecurityContextHolder.clearContext();
        }

        // Always continue the filter chain
        filterChain.doFilter(request, response);
    }
}
