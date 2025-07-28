package dev.magadiflo.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBlockingDequeReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@Slf4j
class Lec10MessageQueueTest extends BaseTest {

    private RBlockingDequeReactive<Long> msgQueue;

    @BeforeAll
    void beforeAllLec10() {
        this.msgQueue = this.client.getBlockingDeque("message-queue", LongCodec.INSTANCE);
    }

    @Test
    void consumer1() {
        this.msgQueue.takeElements()
                .doOnNext(i -> log.info("Consumer 1: {}", i))
                .doOnError(throwable -> log.error("Error 1: {}", throwable.getMessage()))
                .subscribe();

        this.sleep(600_000);
    }

    @Test
    void consumer2() {
        this.msgQueue.takeElements()
                .doOnNext(i -> log.info("Consumer 2: {}", i))
                .doOnError(throwable -> log.error("Error 2: {}", throwable.getMessage()))
                .subscribe();

        this.sleep(600_000);
    }

    @Test
    void producer() {
        Mono<Void> mono = Flux.range(1, 100)
                .delayElements(Duration.ofMillis(500))
                .doOnNext(i -> log.info("Elemento a ser agregado: {}", i))
                .map(Long::valueOf)
                .flatMap(i -> this.msgQueue.add(i))
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }
}
