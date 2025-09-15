package dev.magadiflo.performance.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BusinessMetricsService {

    private final RedissonReactiveClient client;

    public Mono<Map<Integer, Double>> top3Products() {
        String format = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        RScoredSortedSetReactive<Integer> set = this.client.getScoredSortedSet("product:visit:" + format, IntegerCodec.INSTANCE);
        return set.entryRangeReversed(0, 2)
                .map(scoredEntries -> scoredEntries.stream()
                        .collect(Collectors.toMap(
                                ScoredEntry::getValue,
                                ScoredEntry::getScore,
                                (existingValue, newValue) -> existingValue, // en caso de colisión, conserva el valor existente
                                LinkedHashMap::new
                        )));
    }
}