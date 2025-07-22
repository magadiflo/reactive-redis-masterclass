package dev.magadiflo.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RListReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.LongStream;

@Slf4j
class Lec09ListQueueStackTest extends BaseTest {

    @Test
    void listTest() {
        RListReactive<Long> list = this.client.getList("number-input", LongCodec.INSTANCE);
        Mono<Void> monoListAdd = Flux.range(1, 10)
                .map(Long::valueOf)
                .flatMap(list::add)
                .then();

        StepVerifier.create(monoListAdd)
                .verifyComplete();

        StepVerifier.create(list.size())
                .expectNext(10)
                .verifyComplete();
    }

    @Test
    void listTestAll() {
        RListReactive<Long> list = this.client.getList("number-input", LongCodec.INSTANCE);

        List<Long> longList = LongStream.rangeClosed(1, 10)
                .boxed().toList();

        StepVerifier.create(list.addAll(longList).then())
                .verifyComplete();

        StepVerifier.create(list.size())
                .expectNext(10)
                .verifyComplete();
    }
}
