package com.bhaskarshashwath.Ziplink.mappers;


import com.bhaskarshashwath.Ziplink.domain.UrlMapping;
import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.model.UrlMappingDTO;
import com.bhaskarshashwath.Ziplink.request.UrlMappingRequest;
import org.springframework.stereotype.Component;

@Component
public class UrlMappingMapper {


    public UrlMapping toEntity(UrlMappingRequest request, User user){
        UrlMapping entity = new UrlMapping();
        entity.setOriginalUrl(request.getOriginalUrl());
        entity.setUser(user);
        return entity;
    }

    public UrlMappingDTO toDTO(UrlMapping entity){
        UrlMappingDTO dto = new UrlMappingDTO();
        dto.setId(entity.getId());
        dto.setOriginalUrl(entity.getOriginalUrl());
        dto.setShortUrl(entity.getShortUrl());
        dto.setClickCount(entity.getClickCount());
        dto.setUserId(entity.getUser().getId());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
