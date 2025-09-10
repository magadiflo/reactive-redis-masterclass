package dev.magadiflo.redis.app.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomService implements WebSocketHandler {

    private final RedissonReactiveClient client;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String room = "dummy";
        RTopicReactive topic = this.client.getTopic(room, StringCodec.INSTANCE);

        //subscribe
        Mono<Void> incomingFlux = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(topic::publish)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .doFinally(signalType -> log.info("Subscriber finally {}", signalType))
                .then();

        //publisher
        Flux<WebSocketMessage> outgoingFlux = topic.getMessages(String.class)
                .map(session::textMessage)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .doFinally(signalType -> log.info("Publisher finally {}", signalType));

        return session.send(outgoingFlux)
                .and(incomingFlux);
    }
}
