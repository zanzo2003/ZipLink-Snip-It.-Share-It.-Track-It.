package com.bhaskarshashwath.Ziplink.repository;

import com.bhaskarshashwath.Ziplink.domain.ClickEvent;
import com.bhaskarshashwath.Ziplink.domain.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    List<ClickEvent> findByUrlMappingAndClickDateBetween(UrlMapping mapping, LocalDate start, LocalDate end);
    List<ClickEvent> findByUrlMappingInAndClickDateBetween(List<UrlMapping> urlMappings, LocalDate start, LocalDate end);
    List<ClickEvent> findAllByUrlMappingInAndClickDateIn(
            Collection<UrlMapping> urlMappings,
            Collection<LocalDate> clickDates
    );
}
