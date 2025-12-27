package com.bhaskarshashwath.Ziplink.service;

import com.bhaskarshashwath.Ziplink.domain.UrlMapping;
import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.model.ClickEventDTO;
import com.bhaskarshashwath.Ziplink.model.UrlMappingDTO;
import com.bhaskarshashwath.Ziplink.model.request.UrlMappingRequest;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface UrlMappingService {

    UrlMappingDTO createMapping(UrlMappingRequest request, User User);
    List<UrlMappingDTO> getUrlsByUser(User user);
    List<ClickEventDTO> getClickEventsByDate(String shortUrl, LocalDateTime start, LocalDateTime end);
    Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDate start, LocalDate end);
}
