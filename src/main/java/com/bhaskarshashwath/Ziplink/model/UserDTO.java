package com.bhaskarshashwath.Ziplink.model;


import lombok.Data;


import java.util.Set;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String password;
    private Set<String> roles;
    private String email;

}
