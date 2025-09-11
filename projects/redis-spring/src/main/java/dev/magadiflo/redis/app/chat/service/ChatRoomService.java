package dev.magadiflo.redis.app.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RListReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomService implements WebSocketHandler {

    private final RedissonReactiveClient client;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String room = this.getChatRoomName(session);
        RTopicReactive topic = this.client.getTopic(room, StringCodec.INSTANCE);
        RListReactive<String> list = this.client.getList("history:" + room, StringCodec.INSTANCE);

        //subscribe
        Mono<Void> incomingFlux = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(message -> list.add(message).then(topic.publish(message)))
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .doFinally(signalType -> log.info("Subscriber finally {}", signalType))
                .then();

        //publisher
        Flux<WebSocketMessage> outgoingFlux = topic.getMessages(String.class)
                .startWith(list.iterator())
                .map(session::textMessage)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .doFinally(signalType -> log.info("Publisher finally {}", signalType));

        return session.send(outgoingFlux)
                .and(incomingFlux);
    }

    private String getChatRoomName(WebSocketSession session) {
        URI uri = session.getHandshakeInfo().getUri();
        return UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .toSingleValueMap()
                .getOrDefault("room", "default");
    }
}
