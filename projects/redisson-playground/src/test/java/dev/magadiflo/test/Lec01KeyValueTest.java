package dev.magadiflo.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
class Lec01KeyValueTest extends BaseTest {

    @Test
    void keyValueAccessTest() {
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name");
        Mono<Void> set = bucket.set("sam");
        Mono<Void> get = bucket.get()
                .doOnNext(s -> log.info("{}", s))
                .then();

        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }

    @Test
    void keyValueAccessTestStringCodec() {
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("sam");
        Mono<Void> get = bucket.get()
                .doOnNext(s -> log.info("{}", s))
                .then();

        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }
}
