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

        Student student1 = new Student("Milagros", 19, "Lima");
        Student student2 = new Student("Kiara", 24, "Rumisapa");

        Mono<Student> studentMono1 = mapCache.put(1, student1, 5, TimeUnit.SECONDS);
        Mono<Student> studentMono2 = mapCache.put(2, student2, 10, TimeUnit.SECONDS);

        StepVerifier.create(studentMono1.concatWith(studentMono2).then())
                .verifyComplete();

        this.sleep(3_000);
        mapCache.get(1)
                .doOnNext(this::print1)
                .subscribe();
        mapCache.get(2)
                .doOnNext(this::print2)
                .subscribe();

        this.sleep(3_000);
        mapCache.get(1)
                .doOnNext(this::print1)
                .subscribe();
        mapCache.get(2)
                .doOnNext(this::print2)
                .subscribe();
    }

    private void print1(Student student) {
        log.info("student 1: {}", student);
    }

    private void print2(Student student) {
        log.info("student 2: {}", student);
    }
}
