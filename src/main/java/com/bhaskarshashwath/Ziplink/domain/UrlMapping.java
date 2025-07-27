package com.bhaskarshashwath.Ziplink.domain;


import com.bhaskarshashwath.Ziplink.domain.core.DomainCore;
import jakarta.persistence.*;
import lombok.Data;


import java.util.List;

@Entity
@Table(name = "url_mapping")
@Data
public class UrlMapping extends DomainCore {

    private String originalUrl;
    private String shortUrl;
    private Long clickCount = 0L;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "urlMapping")
    private List<ClickEvent> clickEvents;

}
