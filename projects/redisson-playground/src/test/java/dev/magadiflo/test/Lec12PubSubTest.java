package dev.magadiflo.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RPatternTopicReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.client.codec.StringCodec;

@Slf4j
class Lec12PubSubTest extends BaseTest {

    @Test
    void subscriber1() {
        RTopicReactive topic = this.client.getTopic("slack-room-1", StringCodec.INSTANCE);
        topic.getMessages(String.class)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .doOnNext(message -> log.info("sub1: {}", message))
                .subscribe();

        this.sleep(600_000);
    }

    @Test
    void subscriber2() {
        RTopicReactive topic = this.client.getTopic("slack-room-2", StringCodec.INSTANCE);
        topic.getMessages(String.class)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .doOnNext(message -> log.info("sub2: {}", message))
                .subscribe();

        this.sleep(600_000);
    }

    @Test
    void subscriber3() {
        RPatternTopicReactive patternTopic = this.client.getPatternTopic("slack-room-*", StringCodec.INSTANCE);
        patternTopic.addListener(String.class, (pattern, topic, msg) -> {
                    log.info("sub3: {}: {}: {}", pattern, topic, msg);
                })
                .subscribe();
        this.sleep(600_000);
    }
}
