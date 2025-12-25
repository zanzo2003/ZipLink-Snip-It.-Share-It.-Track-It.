package com.bhaskarshashwath.Ziplink.mappers.repository;

import com.bhaskarshashwath.Ziplink.domain.ClickEvent;
import com.bhaskarshashwath.Ziplink.domain.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    List<ClickEvent> findByUrlMappingAndCreatedAtBetween(UrlMapping mapping, LocalDateTime start, LocalDateTime end);
    List<ClickEvent> findByUrlMappingInAndCreatedAtBetween(List<UrlMapping> urlMappings, LocalDateTime start, LocalDateTime end);
}
