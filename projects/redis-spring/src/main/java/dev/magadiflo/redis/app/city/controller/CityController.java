package dev.magadiflo.redis.app.city.controller;

import dev.magadiflo.redis.app.city.dto.City;
import dev.magadiflo.redis.app.city.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/cities")
public class CityController {

    private final CityService cityService;

    @GetMapping(path = "/{zipCode}")
    public Mono<ResponseEntity<City>> getCity(@PathVariable String zipCode) {
        return this.cityService.getCity(zipCode)
                .map(ResponseEntity::ok);
    }

}
