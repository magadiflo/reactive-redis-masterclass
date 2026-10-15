package dev.magadiflo.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RTransactionReactive;
import org.redisson.api.TransactionOptions;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
class Lec14TransactionTest extends BaseTest {

    private RBucketReactive<Long> user1Balance;
    private RBucketReactive<Long> user2Balance;

    @BeforeAll
    void accountSetupBeforeAll() {
        this.user1Balance = this.client.getBucket("user:1:balance", LongCodec.INSTANCE);
        this.user2Balance = this.client.getBucket("user:2:balance", LongCodec.INSTANCE);

        Mono<Void> set1 = this.user1Balance.set(100L);
        Mono<Void> set2 = this.user2Balance.set(0L);
        Mono<Void> mono = set1.concatWith(set2).then();

        StepVerifier.create(mono)
                .verifyComplete();
    }

    @AfterAll
    void accountBalanceStatusAfterAll() {
        Mono<Void> mono = Mono.zip(this.user1Balance.get(), this.user2Balance.get())
                .doOnNext(tuple ->
                        log.info("user1Balance value: {}, user2Balance value: {}", tuple.getT1(), tuple.getT2()))
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    void nonTransactionTest() {
        this.transfer(this.user1Balance, this.user2Balance, 50)
                .thenReturn(0)
                .map(value -> (5 / value)) // Simular error para ver cómo se comporta la transacción
                .doOnError(throwable -> log.error("{}", throwable.getMessage()))
                .subscribe();

        this.sleep(1_000);
    }

    @Test
    void transactionTest() {
        RTransactionReactive transaction = this.client.createTransaction(TransactionOptions.defaults());
        RBucketReactive<Long> txUser1Balance = transaction.getBucket("user:1:balance", LongCodec.INSTANCE);
        RBucketReactive<Long> txUser2Balance = transaction.getBucket("user:2:balance", LongCodec.INSTANCE);

        this.transfer(txUser1Balance, txUser2Balance, 50)
                .thenReturn(0)
                .map(value -> (5 / value))
                .then(transaction.commit())
                .doOnError(throwable -> log.error("{}", throwable.getMessage()))
                .onErrorResume(throwable -> transaction.rollback())
                .subscribe();

        this.sleep(1_000);
    }

    private Mono<Void> transfer(RBucketReactive<Long> fromAccount, RBucketReactive<Long> toAccount, int amount) {
        return Mono.zip(fromAccount.get(), toAccount.get())
                .filter(tuple -> tuple.getT1() >= amount)
                .flatMap(tuple -> fromAccount.set(tuple.getT1() - amount).thenReturn(tuple))
                .flatMap(tuple -> toAccount.set(tuple.getT2() + amount))
                .then();
    }
}
