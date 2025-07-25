package dev.magadiflo.test.assignment;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSetReactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class PriorityQueue {

    private final RScoredSortedSetReactive<UserOrder> queue;

    public Mono<Void> add(UserOrder userOrder) {
        return this.queue.add(
                userOrder.getCategory().ordinal(),
                userOrder
        ).then();
    }

    public Flux<UserOrder> takeItems() {
        return this.queue.takeFirstElements()
                .limitRate(1);
    }
}
