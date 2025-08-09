package com.bhaskarshashwath.Ziplink.controller;


import com.bhaskarshashwath.Ziplink.controller.common.ControllerHelper;
import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.model.ClickEventDTO;
import com.bhaskarshashwath.Ziplink.response.ApiResponseDTO;
import com.bhaskarshashwath.Ziplink.service.UrlMappingService;
import com.bhaskarshashwath.Ziplink.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private UrlMappingService urlMappingService;
    private ControllerHelper controllerHelper;
    private UserService userService;

    @GetMapping("/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<List<ClickEventDTO>>> getAnalytics(
            @PathVariable @NonNull String shortUrl,
            @RequestParam("startDate") @NonNull String startDate,
            @RequestParam("endDate") @NonNull String endDate){

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        return controllerHelper
                .createOkResponse(
                        urlMappingService.getClickEventsByDate(shortUrl, start, end),
                        "Retrieved click events successfully"
                );
    }




    @GetMapping("/totalClicks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Map<LocalDate, Long>>> getTotalClicksByDate(
            Principal principal,
            @RequestParam("startDate") @NonNull String startDate,
            @RequestParam("endDate") @NonNull String endDate
            ){

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        User user = userService.getByUsername(principal.getName());

        return controllerHelper.createOkResponse(
                urlMappingService.getTotalClicksByUserAndDate(user, start, end),
                "Total clicks fetched successfully"
        );
    }
}
