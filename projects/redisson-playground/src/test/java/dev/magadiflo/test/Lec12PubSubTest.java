package dev.magadiflo.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RTopicReactive;
import org.redisson.client.codec.StringCodec;

@Slf4j
class Lec12PubSubTest extends BaseTest {

    @Test
    void subscriber1() {
        RTopicReactive topic = this.client.getTopic("slack-room", StringCodec.INSTANCE);
        topic.getMessages(String.class)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .doOnNext(message -> log.info("sub1: {}", message))
                .subscribe();

        this.sleep(600_000);
    }

    @Test
    void subscriber2() {
        RTopicReactive topic = this.client.getTopic("slack-room", StringCodec.INSTANCE);
        topic.getMessages(String.class)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .doOnNext(message -> log.info("sub2: {}", message))
                .subscribe();

        this.sleep(600_000);
    }
}
