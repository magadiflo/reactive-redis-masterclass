package dev.magadiflo.redis.app.city.client;

import dev.magadiflo.redis.app.city.dto.City;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CityClient {

    private final WebClient webClient;

    public CityClient(@Value("${city.service.url}") String url) {
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    public Mono<City> getCity(final String zipCode) {
        log.info("Llamando al servicio externo para zipCode: {}", zipCode);
        return this.webClient
                .get()
                .uri("/{zipCode}", zipCode)
                .retrieve()
                .bodyToMono(City.class);
    }

    public Flux<City> getAllCities() {
        log.info("Llamando al servicio externo para obtener todas las ciudades");
        return this.webClient
                .get()
                .retrieve()
                .bodyToFlux(City.class);
    }
}
