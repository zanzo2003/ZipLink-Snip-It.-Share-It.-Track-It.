package com.bhaskarshashwath.Ziplink.service.impl;

import com.bhaskarshashwath.Ziplink.domain.ClickEvent;
import com.bhaskarshashwath.Ziplink.domain.UrlMapping;
import com.bhaskarshashwath.Ziplink.exception.ResourceNotFoundExcpetion;
import com.bhaskarshashwath.Ziplink.mappers.repository.ClickEventRepository;
import com.bhaskarshashwath.Ziplink.mappers.repository.UrlMappingRepository;
import com.bhaskarshashwath.Ziplink.service.UrlRedirectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlRedirectServiceImpl implements UrlRedirectService {

    private final UrlMappingRepository urlMappingRepository;
    private final ClickEventRepository clickEventRepository;


    @Override
    public UrlMapping getOriginalMapping(String shortUrl) {
        UrlMapping mapping =  urlMappingRepository.findByShortUrl(shortUrl).orElseThrow(()->new ResourceNotFoundExcpetion("Url Mapping not found"));
        // increasing click count
        mapping.setClickCount(mapping.getClickCount()+1);
        mapping = urlMappingRepository.save(mapping);
        log.info("Incrementing click event for mapping id :{}", mapping.getId());

        //recording click event
        ClickEvent event = new ClickEvent();
        event.setUrlMapping(mapping);
        clickEventRepository.save(event);
        log.info("Recording click event for mapping id :{}", mapping.getId());

        return mapping;
    }
}
