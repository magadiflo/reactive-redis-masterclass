package dev.magadiflo.performance.app.service.impl;

import dev.magadiflo.performance.app.service.ProductVisitService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.BatchOptions;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductVisitServiceImpl implements ProductVisitService {

    private final RedissonReactiveClient client;
    private final Sinks.Many<Integer> sink;

    public ProductVisitServiceImpl(RedissonReactiveClient client) {
        this.client = client;
        this.sink = Sinks.many().unicast().onBackpressureBuffer();
    }

    @PostConstruct
    private void init() {
        this.sink.asFlux()
                .buffer(Duration.ofSeconds(3)) // list (1,2,1,1,3,5,1...)
                .map(listProductIds -> listProductIds.stream()
                        .collect(Collectors.groupingBy( // 1:4, 5:1...
                                Function.identity(),
                                Collectors.counting())
                        )
                )
                .flatMap(this::updateBatch)
                .subscribe();
    }

    @Override
    public void addVisit(int productId) {
        this.sink.tryEmitNext(productId);
    }

    private Mono<Void> updateBatch(Map<Integer, Long> productVisitCounts) {
        RBatchReactive batch = this.client.createBatch(BatchOptions.defaults());
        String format = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        RScoredSortedSetReactive<Integer> set = batch.getScoredSortedSet("product:visit:" + format, IntegerCodec.INSTANCE);

        return Flux.fromIterable(productVisitCounts.entrySet())
                .map(productVisit -> set.addScore(productVisit.getKey(), productVisit.getValue()))
                .then(batch.execute())
                .then();
    }
}
