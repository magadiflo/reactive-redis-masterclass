package dev.magadiflo.test;

import dev.magadiflo.test.dto.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapCacheReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeUnit;

@Slf4j
class Lec07MapCacheTest extends BaseTest {
    @Test
    void mapCacheTest() {
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        RMapCacheReactive<Integer, Student> mapCache = this.client.getMapCache("users:cache", codec);

        Student student1 = new Student("Lesly", 31, "Piura");
        Student student2 = new Student("Cielo", 20, "Lambayeque");

        Mono<Student> mono1 = mapCache.put(1, student1, 5, TimeUnit.SECONDS);
        Mono<Student> mono2 = mapCache.put(2, student2, 10, TimeUnit.SECONDS);

        StepVerifier.create(mono1.concatWith(mono2).then())
                .verifyComplete();

        this.sleep(3_000);
        mapCache.get(1)
                .doOnNext(student -> this.print("student 1: {}", student))
                .subscribe();
        mapCache.get(2)
                .doOnNext(student -> this.print("student 2: {}", student))
                .subscribe();

        this.sleep(3_000);
        mapCache.get(1)
                .doOnNext(student -> this.print("student 1: {}", student))
                .subscribe();
        mapCache.get(2)
                .doOnNext(student -> this.print("student 2: {}", student))
                .subscribe();
    }

    private void print(String message, Student student) {
        log.info(message, student);
    }
}
