package dev.magadiflo.redis.app.city.service;

import dev.magadiflo.redis.app.city.client.CityClient;
import dev.magadiflo.redis.app.city.dto.City;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CityService {

    private final CityClient cityClient;
    private final RMapReactive<String, City> cityMap;

    public CityService(CityClient cityClient, RedissonReactiveClient client) {
        this.cityClient = cityClient;
        this.cityMap = client.getMap("city", new TypedJsonJacksonCodec(String.class, City.class));
    }

    public Mono<City> getCity(final String zipCode) {
        return this.cityMap.get(zipCode)
                .switchIfEmpty(this.cityClient.getCity(zipCode)
                        .flatMap(city -> this.cityMap.fastPut(zipCode, city).thenReturn(city)));
    }
}
