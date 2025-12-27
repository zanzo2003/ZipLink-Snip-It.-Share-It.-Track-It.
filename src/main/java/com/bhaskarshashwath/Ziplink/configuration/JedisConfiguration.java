package com.bhaskarshashwath.Ziplink.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.time.Duration;

@Configuration
public class JedisConfiguration {

    @Value("${redis.url}")
    private String redisUrl;

    @Value("${redis.timeout-ms}")
    private int maxTimeOut;


    @Bean(destroyMethod = "close", name = "jedisPool")
    public JedisPool jedisPool(){

        JedisPoolConfig configuration = new JedisPoolConfig();

        configuration.setMaxTotal(50);
        configuration.setMaxIdle(20);
        configuration.setMinIdle(5);
        configuration.setTestOnBorrow(true);
        configuration.setTestWhileIdle(true);
        configuration.setTimeBetweenEvictionRuns(Duration.ofMillis(30000));
        configuration.setMinEvictableIdleDuration(Duration.ofMillis(30000));

        URI uri = URI.create(redisUrl);
        String host = uri.getHost();
        int port = uri.getPort();


        return new JedisPool(
                configuration,
                host,
                port,
                maxTimeOut
        );
    }


}
