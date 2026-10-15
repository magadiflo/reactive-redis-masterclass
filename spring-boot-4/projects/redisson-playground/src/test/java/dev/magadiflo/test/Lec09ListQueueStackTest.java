package dev.magadiflo.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RDequeReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RQueueReactive;
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
        Mono<Void> monoList = Flux.range(1, 10)
                .map(Long::valueOf)
                .flatMap(list::add)
                .then();

        StepVerifier.create(monoList)
                .verifyComplete();
        StepVerifier.create(list.size())
                .expectNext(10)
                .verifyComplete();
    }

    @Test
    void listOrderAllTest() {
        RListReactive<Long> list = this.client.getList("number-input", LongCodec.INSTANCE);
        Mono<Void> monoList = Flux.range(1, 10)
                .map(Long::valueOf)
                .concatMap(list::add)
                .then();

        StepVerifier.create(monoList)
                .verifyComplete();
        StepVerifier.create(list.size())
                .expectNext(10)
                .verifyComplete();
    }

    @Test
    void listOrderTest() {
        RListReactive<Long> list = this.client.getList("number-input", LongCodec.INSTANCE);
        List<Long> longList = LongStream.rangeClosed(1, 10)
                .boxed()
                .toList();

        StepVerifier.create(list.addAll(longList).then())
                .verifyComplete();
        StepVerifier.create(list.size())
                .expectNext(10)
                .verifyComplete();
    }

    @Test
    void queueTest() {
        RQueueReactive<Long> queue = this.client.getQueue("number-input", LongCodec.INSTANCE);
        Mono<Void> queuePoll = queue.poll()
                .repeat(3)
                .doOnNext(value -> log.info("{}", value))
                .then();
        StepVerifier.create(queuePoll)
                .verifyComplete();
        StepVerifier.create(queue.size())
                .expectNext(6)
                .verifyComplete();
    }

    @Test
    void stackTest() {
        RDequeReactive<Long> deque = this.client.getDeque("number-input", LongCodec.INSTANCE);
        Mono<Void> dequePollLast = deque.pollLast()
                .repeat(3)
                .doOnNext(value -> log.info("Eliminado: {}", value))
                .then();

        StepVerifier.create(dequePollLast)
                .verifyComplete();
        StepVerifier.create(deque.size())
                .expectNext(6)
                .verifyComplete();
    }
}
