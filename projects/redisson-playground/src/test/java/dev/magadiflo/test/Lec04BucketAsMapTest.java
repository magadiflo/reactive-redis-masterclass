package dev.magadiflo.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@Slf4j
class Lec04BucketAsMapTest extends BaseTest {
    @Test
    void bucketsAsMapTest() {
        Mono<Void> mono = this.client.getBuckets(StringCodec.INSTANCE)
                .get("user:1:name", "user:2:name", "user:3:name", "user:4:name")
                .doOnNext(stringObjectMap -> log.info("{}", stringObjectMap))
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }
}
