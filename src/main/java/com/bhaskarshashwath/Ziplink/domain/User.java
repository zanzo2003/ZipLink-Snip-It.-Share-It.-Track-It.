package com.bhaskarshashwath.Ziplink.domain;


import com.bhaskarshashwath.Ziplink.domain.core.DomainCore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@ToString(exclude = {"password"})
@Table(name = "users")
public class User extends DomainCore {

    private String username;
    private String password;
    private String email;
    private String role = "ROLE_USER";

}
