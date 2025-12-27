package com.bhaskarshashwath.Ziplink.domain;


import com.bhaskarshashwath.Ziplink.domain.core.DomainCore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="click_event")
public class ClickEvent extends DomainCore {

    private Long count;

    @ManyToOne
    @JoinColumn(name = "url_mapping_id")
    private UrlMapping urlMapping;

}
