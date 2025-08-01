package dev.magadiflo.redis.app.weather.controller;

import dev.magadiflo.redis.app.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/weathers")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping(path = "/{zipCode}")
    public Mono<ResponseEntity<Integer>> getWeather(@PathVariable int zipCode) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(this.weatherService.getInfo(zipCode)));
    }

}
