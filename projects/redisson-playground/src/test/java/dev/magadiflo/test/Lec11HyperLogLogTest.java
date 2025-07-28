package dev.magadiflo.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RHyperLogLogReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.LongStream;

@Slf4j
class Lec11HyperLogLogTest extends BaseTest {
    @Test
    void count() {
        RHyperLogLogReactive<Long> counter = this.client.getHyperLogLog("user:visits", LongCodec.INSTANCE);

        // Suponiendo que tenemos muchos datos
        List<Long> list1 = LongStream.rangeClosed(1, 25_000).boxed().toList();
        List<Long> list2 = LongStream.rangeClosed(25_001, 50_000).boxed().toList();
        List<Long> list3 = LongStream.rangeClosed(50_000, 75_000).boxed().toList();
        List<Long> list4 = LongStream.rangeClosed(75_001, 100_000).boxed().toList();

        Mono<Void> mono = Flux.just(list1, list2, list3, list4)
                .flatMap(counter::addAll)
                .then();

        StepVerifier.create(mono)
                .verifyComplete();

        counter.count()
                .doOnNext(i -> log.info("{}", i))
                .subscribe();
    }
}
