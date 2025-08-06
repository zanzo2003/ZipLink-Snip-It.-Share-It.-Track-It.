package com.bhaskarshashwath.Ziplink.service.impl;

import com.bhaskarshashwath.Ziplink.domain.UrlMapping;
import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.model.UrlMappingDTO;
import com.bhaskarshashwath.Ziplink.repository.UrlMappingRepository;
import com.bhaskarshashwath.Ziplink.request.UrlMappingRequest;
import com.bhaskarshashwath.Ziplink.service.UrlMappingService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Random;


@Service
@AllArgsConstructor
public class UrlMappingServiceImpl implements UrlMappingService{

    private UrlMappingRepository repository;

    @Override
    public UrlMappingDTO createMapping(UrlMappingRequest request, User user) {
        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(request.getOriginalUrl());
        mapping.setUser(user);
        mapping.setShortUrl(generateShortUrl(request.getOriginalUrl()));
        mapping = repository.save(mapping);
        UrlMappingDTO dto = new UrlMappingDTO();
        BeanUtils.copyProperties(mapping, dto);
        dto.setUserId(user.getId());
        return dto;
    }

    private String generateShortUrl(String originalUrl){

        Random random = new Random();
        String characters = "012345689abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder shortUrl = new StringBuilder(8);

        for(int i = 0; i < 8; i++){
            shortUrl.append(characters.charAt(random.nextInt(characters.length())));
        }
        return shortUrl.toString();
    }
}
