package dev.magadiflo.redis.app.city.service;

import dev.magadiflo.redis.app.city.client.CityClient;
import dev.magadiflo.redis.app.city.dto.City;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJackson3Codec;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CityService {

    private final CityClient cityClient;
    private final RMapReactive<String, City> cityMap;

    public CityService(CityClient cityClient, RedissonReactiveClient client) {
        this.cityClient = cityClient;
        this.cityMap = client.getMapCache("city", new TypedJsonJackson3Codec(String.class, City.class));
    }

    public Mono<City> getCity(final String zipCode) {
        return this.cityMap.get(zipCode)
                .onErrorResume(throwable -> this.cityClient.getCity(zipCode));
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    public void updateCity() {
        this.cityClient.getAllCities()
                .collectList()
                .map(cities -> cities.stream()
                        .collect(Collectors.toMap(City::zip, Function.identity())))
                .flatMap(this.cityMap::putAll)
                .subscribe();
    }
}
