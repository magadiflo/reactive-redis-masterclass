package dev.magadiflo.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

@Slf4j
class Lec06MapTest extends BaseTest {
    @Test
    void mapTest1() {
        RMapReactive<String, String> map = this.client.getMap("user:1", StringCodec.INSTANCE);
        Mono<String> name = map.put("name", "sam");
        Mono<String> age = map.put("age", "36");
        Mono<String> city = map.put("city", "Lima");

        StepVerifier.create(name.concatWith(age).concatWith(city).then())
                .verifyComplete();
    }

    @Test
    void mapTest2() {
        RMapReactive<String, String> map = this.client.getMap("user:2", StringCodec.INSTANCE);
        Map<String, String> javaMap = Map.of(
                "name", "jake",
                "age", "40",
                "city", "miami"
        );

        StepVerifier.create(map.putAll(javaMap).then())
                .verifyComplete();
    }
}
