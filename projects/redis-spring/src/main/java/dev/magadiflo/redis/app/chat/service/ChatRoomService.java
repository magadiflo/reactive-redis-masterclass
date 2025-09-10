package dev.magadiflo.redis.app.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ChatRoomService implements WebSocketHandler {
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        //subscribe
        session.receive();

        //publisher
        //session.send();
        return null;
    }
}
