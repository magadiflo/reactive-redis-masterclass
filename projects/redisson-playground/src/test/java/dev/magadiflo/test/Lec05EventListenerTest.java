package dev.magadiflo.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@Slf4j
class Lec05EventListenerTest extends BaseTest {
    @Test
    void expiredEventTest() {
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("sam", Duration.ofSeconds(10));
        Mono<Void> get = bucket.get()
                .doOnNext(value -> log.info("{}", value))
                .then();

        Mono<Void> event = bucket.addListener(new ExpiredObjectListener() {
            @Override
            public void onExpired(String name) {
                log.info("Expiró: {}", name);
            }
        }).then();

        StepVerifier.create(set.concatWith(get).concatWith(event))
                .verifyComplete();

        // extendiendo el tiempo de vida
        this.sleep(11_000);
    }
}
