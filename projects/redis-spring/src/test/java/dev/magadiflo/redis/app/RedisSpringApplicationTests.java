package dev.magadiflo.redis.app;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.redisson.api.RAtomicLongReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@SpringBootTest
class RedisSpringApplicationTests {

    @Autowired
    private ReactiveStringRedisTemplate template;

    @Autowired
    private RedissonReactiveClient client;

    @RepeatedTest(3)
    void springDataRedisTest(RepetitionInfo info) {
        ReactiveValueOperations<String, String> valueOperations = this.template.opsForValue();

        //¿Cuánto tiempo se tarja en ejecutar este bloque?
        long before = System.currentTimeMillis();
        Mono<Void> mono = Flux.range(1, 500_000)
                .flatMap(i -> valueOperations.increment("user:1:visit"))
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
        long after = System.currentTimeMillis();
        log.info("Repetition: {}/{} - Total time: {}ms", info.getCurrentRepetition(), info.getTotalRepetitions(), (after - before));
    }

    @RepeatedTest(3)
    void redissonTest(RepetitionInfo info) {
        RAtomicLongReactive atomicLong = this.client.getAtomicLong("user:2:visit");

        //¿Cuánto tiempo se tarja en ejecutar este bloque?
        long before = System.currentTimeMillis();
        Mono<Void> mono = Flux.range(1, 500_000)
                .flatMap(i -> atomicLong.incrementAndGet())
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
        long after = System.currentTimeMillis();
        log.info("Repetition= {}/{} - Total time= {}ms", info.getCurrentRepetition(), info.getTotalRepetitions(), (after - before));
    }

}
