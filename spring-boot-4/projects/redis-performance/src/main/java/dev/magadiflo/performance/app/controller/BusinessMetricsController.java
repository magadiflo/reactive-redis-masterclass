package dev.magadiflo.performance.app.controller;

import dev.magadiflo.performance.app.service.impl.BusinessMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/{version}/products/metrics", version = "1")
public class BusinessMetricsController {

    private final BusinessMetricsService businessMetricsService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<ResponseEntity<Flux<Map<Integer, Double>>>> getMetrics() {
        // Cada vez que la consulta del top 3 termine, espera una señal del intervalo
        // de 3 segundos y vuelve a ejecutar la consulta
        Flux<Map<Integer, Double>> top3ProductsFlux = this.businessMetricsService.top3Products()
                .repeatWhen(emittedCount ->
                        Flux.interval(Duration.ofSeconds(3)));
        return Mono.just(ResponseEntity.ok(top3ProductsFlux));
    }

}
