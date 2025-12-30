package com.bhaskarshashwath.Ziplink.service.impl;

import com.bhaskarshashwath.Ziplink.domain.UrlMapping;
import com.bhaskarshashwath.Ziplink.exception.ResourceNotFoundExcpetion;
import com.bhaskarshashwath.Ziplink.repository.ClickEventRepository;
import com.bhaskarshashwath.Ziplink.repository.UrlMappingRepository;
import com.bhaskarshashwath.Ziplink.service.UrlRedirectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDate;



@Slf4j
@Service
@RequiredArgsConstructor
public class UrlRedirectServiceImpl implements UrlRedirectService {

    private final UrlMappingRepository urlMappingRepository;
    private final ClickEventRepository clickEventRepository;
    private final JedisPool jedisPool;


    @Override
    public UrlMapping getOriginalMapping(String shortUrl) {
        String urlKey = "url:" + shortUrl;
        String clickKey = "click:" + shortUrl;
        String statsKey = "stats:" + shortUrl;
        String today = LocalDate.now().toString();

        try(Jedis jedis = jedisPool.getResource()){

            String originalUrl = jedis.get(urlKey);

            if( originalUrl == null){
                UrlMapping urlMapping = urlMappingRepository
                        .findByShortUrl(shortUrl)
                        .orElseThrow(() -> new ResourceNotFoundExcpetion("ShortUrl doesn't exists."));
                originalUrl = urlMapping.getOriginalUrl();
                jedis.set(urlKey, originalUrl);
                log.info("Searched the DB instead of cache for : {}", originalUrl);
            }

            jedis.incr(clickKey);
            jedis.hincrBy(statsKey, today, 1);
            log.info("Searched the Cache instead of DB for : {}", originalUrl);
            log.info("Update cache click with : {}", jedis.get(clickKey));
            log.info("Update cache stats with : {}", jedis.hget("stats:"+shortUrl, today));

            UrlMapping mapping = new UrlMapping();
            mapping.setOriginalUrl(originalUrl);
            mapping.setShortUrl(shortUrl);

            return mapping;
        }
    }
}
