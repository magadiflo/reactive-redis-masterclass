package dev.magadiflo.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@Slf4j
class Lec01KeyValueTest extends BaseTest {

    @Test
    void keyValueAccessTest() {
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name");
        Mono<Void> set = bucket.set("sam");
        Mono<Void> get = bucket.get()
                .doOnNext(value -> log.info("{}", value))
                .then();

        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }

    @Test
    void keyValueAccessStringCodecTest() {
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("sam");
        Mono<Void> get = bucket.get()
                .doOnNext(value -> log.info("{}", value))
                .then();

        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }

    @Test
    void keyValueExpiryTest() {
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);

        Mono<Void> set = bucket.set("sam", Duration.ofSeconds(10));
        Mono<Void> get = bucket.get()
                .doOnNext(value -> log.info("{}", value))
                .then();

        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }

    @Test
    void keyValueExtendExpiryTest() {
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("sam", Duration.ofSeconds(10));
        Mono<Void> get = bucket.get()
                .doOnNext(value -> log.info("{}", value))
                .then();
        StepVerifier.create(set.concatWith(get))
                .verifyComplete();

        // Esperamos 5 segundos antes de extender la expiración
        this.sleep(5000);
        Mono<Boolean> mono = bucket.expire(Duration.ofSeconds(60));
        StepVerifier.create(mono)
                .expectNext(true)
                .verifyComplete();

        // Consultamos el tiempo restante antes de que expire
        Mono<Void> ttl = bucket.remainTimeToLive()
                .doOnNext(time -> log.info("{}", time))
                .then();
        StepVerifier.create(ttl)
                .verifyComplete();
    }
}
