package com.bhaskarshashwath.Ziplink.service.impl;

import com.bhaskarshashwath.Ziplink.domain.UrlMapping;
import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.mappers.UrlMappingMapper;
import com.bhaskarshashwath.Ziplink.model.UrlMappingDTO;
import com.bhaskarshashwath.Ziplink.repository.UrlMappingRepository;
import com.bhaskarshashwath.Ziplink.request.UrlMappingRequest;
import com.bhaskarshashwath.Ziplink.service.UrlMappingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class UrlMappingServiceImpl implements UrlMappingService{

    private UrlMappingRepository repository;

    private UrlMappingMapper mapper;

    @Override
    public UrlMappingDTO createMapping(UrlMappingRequest request, User user) {

        UrlMapping mapping = mapper.toEntity(request, user);
        mapping.setShortUrl(generateShortUrl(request.getOriginalUrl()));
        log.info("Creating shorturl for {} , requested by user {}", request.getOriginalUrl(), user.getUsername());
        mapping = repository.save(mapping);
        UrlMappingDTO dto = mapper.toDTO(mapping);
        return dto;
    }

    @Override
    public List<UrlMappingDTO> getUrlsByUser(User user) {
        List<UrlMapping> mappings = repository.findAllByUser(user);
        log.info("Retrieved {} URL mappings for user ID: {}", mappings.size(), user.getId());
        return mappings.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toUnmodifiableList());
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
