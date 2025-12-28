package com.bhaskarshashwath.Ziplink.scheduler;


import com.bhaskarshashwath.Ziplink.domain.ClickEvent;
import com.bhaskarshashwath.Ziplink.domain.UrlMapping;
import com.bhaskarshashwath.Ziplink.repository.ClickEventRepository;
import com.bhaskarshashwath.Ziplink.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClickFlushJob {

    private final JedisPool jedisPool;
    private final ClickEventRepository clickEventRepository;
    private final UrlMappingRepository urlMappingRepository;
    private static final int BATCH_SIZE = 500;

    @Scheduled(fixedRate = 6000)
    @SchedulerLock(name = "clickFlushJob", lockAtMostFor = "50s", lockAtLeastFor = "10s")
    @Transactional
    public void flushAnalytics() {

        log.info("Started flush job at : {}", LocalDateTime.now());
        try (Jedis jedis = jedisPool.getResource()) {
            String cursor = "0";
            ScanParams scanParams = new ScanParams().match("stats:*").count(BATCH_SIZE);

            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                List<String> keys = scanResult.getResult();

                if (!keys.isEmpty()) {
                    processBatch(keys, jedis);
                }

                cursor = scanResult.getCursor();
            } while (!cursor.equals("0"));
            log.info("Ended flush job at : {}", LocalDateTime.now());
        } catch (Exception e) {
            log.info("Failed to fluish analytics : {} ", LocalDateTime.now());
            log.error("Failed to flush analytics", e);
        }
    }

    private void processBatch(List<String> keys, Jedis jedis) {
        // 1. Map Redis keys to Short URLs
        Map<String, String> keyToShortUrl = keys.stream()
                .collect(Collectors.toMap(k -> k, k -> k.replace("stats:", "")));

        // 2. Fetch all UrlMappings for this batch in one query
        Map<String, UrlMapping> urlMap = urlMappingRepository
                .findAllByShortUrlIn(new ArrayList<>(keyToShortUrl.values()))
                .stream()
                .collect(Collectors.toMap(UrlMapping::getShortUrl, u -> u));

        // 3. Prepare to fetch existing ClickEvents in bulk
        // We collect all dates and mappings to find what's already in the DB
        List<LocalDate> datesInBatch = new ArrayList<>();
        List<UrlMapping> mappingsInBatch = new ArrayList<>(urlMap.values());

        // Temporary storage for Redis data to avoid calling hgetAll twice
        Map<String, Map<String, String>> redisData = new HashMap<>();
        for (String key : keys) {
            Map<String, String> stats = jedis.hgetAll(key);
            redisData.put(key, stats);
            stats.keySet().forEach(d -> datesInBatch.add(LocalDate.parse(d)));
        }

        List<ClickEvent> existingEvents = clickEventRepository.findAllByUrlMappingInAndClickDateIn(mappingsInBatch, datesInBatch);
        Map<String, ClickEvent> existingEventsMap = existingEvents.stream()
                .collect(Collectors.toMap(
                        e -> e.getUrlMapping().getId() + "_" + e.getClickDate(),
                        e -> e
                ));

        Set<ClickEvent> toSave = new LinkedHashSet<>();

        // 5. Process the data
        for (String key : keys) {
            UrlMapping mapping = urlMap.get(keyToShortUrl.get(key));
            if (mapping == null) continue;

            Map<String, String> stats = redisData.get(key);
            for (var entry : stats.entrySet()) {
                LocalDate date = LocalDate.parse(entry.getKey());
                long newCount = Long.parseLong(entry.getValue());

                String lookupKey = mapping.getId() + "_" + date;
                ClickEvent event = existingEventsMap.get(lookupKey);

                if (event != null) {
                    // Update existing record
                    event.setCount(event.getCount() + newCount);
                    toSave.add(event);
                } else {
                    // Create new record
                    ClickEvent newEvent = ClickEvent.builder()
                            .urlMapping(mapping)
                            .clickDate(date)
                            .count(newCount)
                            .build();
                    toSave.add(newEvent);
                    // Add to map to prevent creating duplicates within the same batch
                    existingEventsMap.put(lookupKey, newEvent);
                }
            }
        }

        // 6. Save everything and clear Redis
        if (!toSave.isEmpty()) {
            clickEventRepository.saveAllAndFlush(toSave);
            Pipeline pipeline = jedis.pipelined();
            keys.forEach(pipeline::del);
            pipeline.sync();
        }
    }
}
