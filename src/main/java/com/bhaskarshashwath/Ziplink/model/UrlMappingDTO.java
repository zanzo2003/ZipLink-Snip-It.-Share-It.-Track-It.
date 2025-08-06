package com.bhaskarshashwath.Ziplink.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlMappingDTO {

    private Long id;
    private String originalUrl;
    private String shortUrl;
    private Long clickCount;
    private Long userId;
    private LocalDateTime createdAt;


}
