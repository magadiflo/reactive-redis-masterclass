package dev.magadiflo.performance.app.controller;

import dev.magadiflo.performance.app.service.impl.BusinessMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/products/metrics")
public class BusinessMetricsController {

    private final BusinessMetricsService businessMetricsService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<ResponseEntity<Flux<Map<Integer, Double>>>> getMetrics() {
        return Mono.fromSupplier(() -> ResponseEntity.ok(this.businessMetricsService.top3Products()
                .repeatWhen(longFlux -> Flux.interval(Duration.ofSeconds(3)))));
    }
}
