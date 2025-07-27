package com.bhaskarshashwath.Ziplink.domain.core;


import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;

@MappedSuperclass
@Data
public class DomainCore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
}
