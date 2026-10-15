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

        // Agrega lista para almacenar historial de conversación
        RListReactive<String> list = this.client.getList("history:" + room, StringCodec.INSTANCE);

        //Subscribe: recibir mensajes del cliente y publicarlos en Redis
        Mono<Void> incomingFlux = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(message -> list.add(message).then(topic.publish(message))) // Agregamos la conversación a la lista y publicamos el mensaje
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .doFinally(signalType -> log.info("Subscriber finally {}", signalType))
                .then(); // convierte el Flux<Long> en Mono<Void>, dejando claro que no usamos los valores

        //Publisher: escuchar mensajes desde Redis y enviarlos al cliente WebSocket
        Flux<WebSocketMessage> outgoingFlux = topic.getMessages(String.class)
                .startWith(list.iterator()) // El startWith(...) permite primero emitir el flujo definido, luego el flujo normal
                .map(session::textMessage) // convierte los mensajes en formato WebSocket
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .doFinally(signalType -> log.info("Publisher finally {}", signalType));

        // Ejecutar ambos flujos en paralelo mientras dure la sesión WebSocket
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
