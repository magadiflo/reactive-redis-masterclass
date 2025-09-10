package dev.magadiflo.redis.app.chat.config;

import dev.magadiflo.redis.app.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class ChatRoomSocketConfig {

    private final ChatRoomService chatRoomService;

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> urlMap = Map.of(
                "/chat", this.chatRoomService
        );
        return new SimpleUrlHandlerMapping(urlMap, -1);
    }

}
