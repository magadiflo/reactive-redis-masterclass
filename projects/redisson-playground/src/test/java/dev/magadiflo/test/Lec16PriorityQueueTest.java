package dev.magadiflo.test;

import dev.magadiflo.test.assignment.Category;
import dev.magadiflo.test.assignment.PriorityQueue;
import dev.magadiflo.test.assignment.UserOrder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@Slf4j
class Lec16PriorityQueueTest extends BaseTest {

    private PriorityQueue priorityQueue;

    @BeforeAll
    void setupQueueBeforeAll() {
        RScoredSortedSetReactive<UserOrder> sortedSet = this.client.getScoredSortedSet("user:order:queue", new TypedJsonJacksonCodec(UserOrder.class));
        this.priorityQueue = new PriorityQueue(sortedSet);
    }

    @Test
    void producer() {
        UserOrder u1 = UserOrder.builder().id(1).category(Category.GUEST).build();
        UserOrder u2 = UserOrder.builder().id(2).category(Category.STD).build();
        UserOrder u3 = UserOrder.builder().id(3).category(Category.PRIME).build();
        UserOrder u4 = UserOrder.builder().id(4).category(Category.STD).build();
        UserOrder u5 = UserOrder.builder().id(5).category(Category.GUEST).build();

        Mono<Void> mono = Flux.just(u1, u2, u3, u4, u5)
                .flatMap(userOrder -> this.priorityQueue.add(userOrder))
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    void producer2() {
        Flux.interval(Duration.ofSeconds(1))
                .map(l -> l.intValue() * 5)
                .doOnNext(i -> {
                    UserOrder u1 = UserOrder.builder().id(i + 1).category(Category.GUEST).build();
                    UserOrder u2 = UserOrder.builder().id(i + 2).category(Category.STD).build();
                    UserOrder u3 = UserOrder.builder().id(i + 3).category(Category.PRIME).build();
                    UserOrder u4 = UserOrder.builder().id(i + 4).category(Category.STD).build();
                    UserOrder u5 = UserOrder.builder().id(i + 5).category(Category.GUEST).build();

                    Mono<Void> mono = Flux.just(u1, u2, u3, u4, u5)
                            .flatMap(userOrder -> this.priorityQueue.add(userOrder))
                            .then();

                    StepVerifier.create(mono)
                            .verifyComplete();

                }).subscribe();

        this.sleep(600_000);
    }

    @Test
    void consumer() {
        this.priorityQueue.takeItems()
                .delayElements(Duration.ofMillis(500))
                .doOnNext(userOrder -> log.info("{}", userOrder))
                .subscribe();

        this.sleep(600_000);
    }
}
