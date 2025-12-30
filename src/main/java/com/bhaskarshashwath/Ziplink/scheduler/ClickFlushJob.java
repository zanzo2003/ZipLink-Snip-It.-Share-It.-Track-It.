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

    @Scheduled(fixedRate = 30000)
    @SchedulerLock(name = "clickFlushJob", lockAtMostFor = "50s", lockAtLeastFor = "10s")
    @Transactional
    public void flushAnalytics() {

        LocalDateTime startTime = LocalDateTime.now();
        log.info("[FLUSH_JOB] Started click analytics flush at {}", startTime);

        try (Jedis jedis = jedisPool.getResource()) {

            processClickEvent(jedis);
//            processClickCount(jedis);

        } catch (Exception e) {
            log.error("[FLUSH_JOB] Flush failed due to exception", e);
            throw e;
        }
    }

    private void processClickCount(Jedis jedis){
        String cursor = "0";
        ScanParams scanParams = new ScanParams()
                .match("click:*")
                .count(BATCH_SIZE);

        int totalKeysProcessed = 0;

        do {
            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
            List<String> keys = scanResult.getResult();
            cursor = scanResult.getCursor();

            if (!keys.isEmpty()) {
                log.info("[FLUSH_JOB - CLICK COUNT] Processing batch of {} Redis keys", keys.size());
                processClickCountBatch(keys, jedis);
                totalKeysProcessed += keys.size();
            }

        } while (!cursor.equals("0"));

        log.info(
                "[FLUSH_JOB - CLICK COUNT] flush at {} | Total keys processed: {}",
                LocalDateTime.now(),
                totalKeysProcessed
        );

    }

    private void processClickCountBatch(List<String> keys, Jedis jedis){}

    private void processClickEvent(Jedis jedis){
        String cursor = "0";
        ScanParams scanParams = new ScanParams()
                .match("stats:*")
                .count(BATCH_SIZE);

        int totalKeysProcessed = 0;

        do {
            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
            List<String> keys = scanResult.getResult();
            cursor = scanResult.getCursor();

            if (!keys.isEmpty()) {
                log.info("[FLUSH_JOB - CLICK EVENT] Click Events Processing batch of {} Redis keys", keys.size());
                processClickEventBatch(keys, jedis);
                totalKeysProcessed += keys.size();
            }

        } while (!cursor.equals("0"));

        log.info(
                "[FLUSH_JOB - CLICK EVENT] flush at {} | Total keys processed: {}",
                LocalDateTime.now(),
                totalKeysProcessed
        );

    }

    private void processClickEventBatch(List<String> keys, Jedis jedis) {

        log.debug("[FLUSH_BATCH - CLICK EVENT] Starting batch processing for {} keys", keys.size());

        // 1. Map Redis keys → short URLs
        Map<String, String> keyToShortUrl = keys.stream()
                .collect(Collectors.toMap(k -> k, k -> k.replace("stats:", "")));

        log.debug(
                "[FLUSH_BATCH - CLICK EVENT] Extracted {} shortUrls from Redis keys",
                keyToShortUrl.size()
        );

        // 2. Fetch URL mappings
        Map<String, UrlMapping> urlMap = urlMappingRepository
                .findAllByShortUrlIn(new ArrayList<>(keyToShortUrl.values()))
                .stream()
                .collect(Collectors.toMap(UrlMapping::getShortUrl, u -> u));

        log.debug(
                "[FLUSH_BATCH - CLICK EVENT] Loaded {} UrlMapping entities from DB",
                urlMap.size()
        );

        List<LocalDate> datesInBatch = new ArrayList<>();
        List<UrlMapping> mappingsInBatch = new ArrayList<>(urlMap.values());

        // 3. Load Redis hash data
        Map<String, Map<String, String>> redisData = new HashMap<>();

        for (String key : keys) {
            Map<String, String> stats = jedis.hgetAll(key);
            redisData.put(key, stats);
            stats.keySet().forEach(d -> datesInBatch.add(LocalDate.parse(d)));
        }

        log.debug(
                "[FLUSH_BATCH - CLICK EVENT] Loaded Redis analytics | Keys: {} | Date buckets: {}",
                redisData.size(),
                datesInBatch.size()
        );

        // 4. Fetch existing DB click events
        List<ClickEvent> existingEvents =
                clickEventRepository.findAllByUrlMappingInAndClickDateIn(
                        mappingsInBatch,
                        datesInBatch
                );

        Map<String, ClickEvent> existingEventsMap = existingEvents.stream()
                .collect(Collectors.toMap(
                        e -> e.getUrlMapping().getId() + "_" + e.getClickDate(),
                        e -> e
                ));

        log.debug(
                "[FLUSH_BATCH - CLICK EVENT] Found {} existing click events in DB",
                existingEventsMap.size()
        );

        Set<ClickEvent> toSave = new LinkedHashSet<>();
        long totalClicksAggregated = 0;

        // 5. Merge Redis counts → DB entities
        for (String key : keys) {
            UrlMapping mapping = urlMap.get(keyToShortUrl.get(key));
            if (mapping == null) {
                log.warn(
                        "[FLUSH_BATCH - CLICK EVENT] UrlMapping missing for Redis key {} — skipping",
                        key
                );
                continue;
            }

            Map<String, String> stats = redisData.get(key);
            for (var entry : stats.entrySet()) {
                LocalDate date = LocalDate.parse(entry.getKey());
                long newCount = Long.parseLong(entry.getValue());
                totalClicksAggregated += newCount;

                String lookupKey = mapping.getId() + "_" + date;
                ClickEvent event = existingEventsMap.get(lookupKey);

                if (event != null) {
                    event.setCount(event.getCount() + newCount);
                    toSave.add(event);
                } else {
                    ClickEvent newEvent = ClickEvent.builder()
                            .urlMapping(mapping)
                            .clickDate(date)
                            .count(newCount)
                            .build();
                    toSave.add(newEvent);
                    existingEventsMap.put(lookupKey, newEvent);
                }
            }
        }

        log.info(
                "[FLUSH_BATCH - CLICK EVENT] Aggregated {} click events | Total clicks: {}",
                toSave.size(),
                totalClicksAggregated
        );

        // 6. Persist + evict Redis
        if (!toSave.isEmpty()) {
            clickEventRepository.saveAllAndFlush(toSave);

            Pipeline pipeline = jedis.pipelined();
            keys.forEach(pipeline::del);
            pipeline.sync();

            log.info(
                    "[FLUSH_BATCH - CLICK EVENT] Persisted {} click events and evicted {} Redis keys",
                    toSave.size(),
                    keys.size()
            );
        } else {
            log.debug("[FLUSH_BATCH - CLICK EVENT] No click events to persist in this batch");
        }
    }
}
