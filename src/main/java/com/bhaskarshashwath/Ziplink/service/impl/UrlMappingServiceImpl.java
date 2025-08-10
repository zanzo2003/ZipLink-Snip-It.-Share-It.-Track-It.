package com.bhaskarshashwath.Ziplink.service.impl;

import com.bhaskarshashwath.Ziplink.domain.ClickEvent;
import com.bhaskarshashwath.Ziplink.domain.UrlMapping;
import com.bhaskarshashwath.Ziplink.domain.User;
import com.bhaskarshashwath.Ziplink.exception.ResourceNotFoundExcpetion;
import com.bhaskarshashwath.Ziplink.mappers.UrlMappingMapper;
import com.bhaskarshashwath.Ziplink.model.ClickEventDTO;
import com.bhaskarshashwath.Ziplink.model.UrlMappingDTO;
import com.bhaskarshashwath.Ziplink.repository.ClickEventRepository;
import com.bhaskarshashwath.Ziplink.repository.UrlMappingRepository;
import com.bhaskarshashwath.Ziplink.request.UrlMappingRequest;
import com.bhaskarshashwath.Ziplink.service.UrlMappingService;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;



@Slf4j
@Service
@AllArgsConstructor
public class UrlMappingServiceImpl implements UrlMappingService{

    private UrlMappingRepository repository;

    private ClickEventRepository clickEventRepository;

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


    public List<ClickEventDTO> getClickEventsByDate(String shortUrl, LocalDateTime start, LocalDateTime end){
        UrlMapping urlDetails = repository.findByShortUrl(shortUrl).orElseThrow(()->new ResourceNotFoundExcpetion("URL Details not found"));
        return clickEventRepository.findByUrlMappingAndCreatedAtBetween(urlDetails, start, end)
                .stream()
                .collect(Collectors
                        .groupingBy( click ->
                                click.getCreatedAt().toLocalDate(), Collectors.counting()))
                .entrySet().stream()
                .map(entry-> {
                            ClickEventDTO result = new ClickEventDTO();
                            result.setClickDate(entry.getKey());
                            result.setCount(entry.getValue());
                            return result;
                        })
                .collect(Collectors.toList());
    }

    @Override
    public Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDate start, LocalDate end) {
        List<UrlMapping> userUrls = repository.findAllByUser(user);
        Map<LocalDate, Long> result = clickEventRepository.findByUrlMappingInAndCreatedAtBetween(userUrls, start.atStartOfDay(), end.plusDays(1).atStartOfDay())
                .stream()
                .collect(Collectors
                        .groupingBy( click ->
                                click.getCreatedAt().toLocalDate(), Collectors.counting()));

        return result;
    }

    @Override
    public UrlMapping getOriginalMapping(String shortUrl) {
        UrlMapping mapping =  repository.findByShortUrl(shortUrl).orElseThrow(()->new ResourceNotFoundExcpetion("Url Mapping not found"));
        // increasing click count
        mapping.setClickCount(mapping.getClickCount()+1);
        mapping = repository.save(mapping);
        log.info("Incrementing click event for mapping id :{}", mapping.getId());

        //recording click event
        ClickEvent event = new ClickEvent();
        event.setUrlMapping(mapping);
        clickEventRepository.save(event);
        log.info("Recording click event for mapping id :{}", mapping.getId());

        return mapping;
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
