package dev.magadiflo.test;

import dev.magadiflo.test.config.RedissonConfig;
import dev.magadiflo.test.dto.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.LocalCachedMapOptions;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;

import java.time.Duration;


@Slf4j
class Lec08LocalCacheMapTest extends BaseTest {

    private RLocalCachedMap<Integer, Student> studentsMap;

    @Override
    @BeforeAll
    void beforeAll() {
        RedissonConfig redissonConfig = new RedissonConfig();
        RedissonClient redissonClient = redissonConfig.getClient();

        LocalCachedMapOptions<Integer, Student> mapOptions = LocalCachedMapOptions.<Integer, Student>name("students")
                .codec(new TypedJsonJacksonCodec(Integer.class, Student.class))
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR)
                .retryAttempts(3)                           // Número de reintentos
                .retryInterval(Duration.ofMillis(1500));   // Intervalo entre reintentos

        this.studentsMap = redissonClient.getLocalCachedMap(mapOptions);
    }

    @Test
    void appServer1() {
        Student student1 = new Student("Milagros", 19, "Lima");
        Student student2 = new Student("Kiara", 24, "Rumisapa");

        this.studentsMap.put(1, student1);
        this.studentsMap.put(2, student2);

        Flux.interval(Duration.ofSeconds(1))
                .doOnNext(i -> log.info("{} => {}", i, this.studentsMap.get(1)))
                .subscribe();

        sleep(600_000);
    }

    @Test
    void appServer2() {
        Student student1 = new Student("Milagros Díaz", 19, "Lima/Callao");
        this.studentsMap.put(1, student1);
    }
}
