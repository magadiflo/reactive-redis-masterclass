package dev.magadiflo.redis.app.city.service;

import dev.magadiflo.redis.app.city.client.CityClient;
import dev.magadiflo.redis.app.city.dto.City;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCacheReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CityService {

    private final CityClient cityClient;
    private final RMapCacheReactive<String, City> cityMap;

    public CityService(CityClient cityClient, RedissonReactiveClient client) {
        this.cityClient = cityClient;
        this.cityMap = client.getMapCache("city", new TypedJsonJacksonCodec(String.class, City.class));
    }

    public Mono<City> getCity(final String zipCode) {
        return this.cityMap.get(zipCode)
                .doOnNext(city -> log.info("Cache HIT - Obteniendo desde Redis para zipCode: {}", zipCode))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Cache MISS - Consultando servicio externo para zipCode: {}", zipCode);
                    return this.cityClient.getCity(zipCode)
                            .flatMap(city -> this.cityMap.fastPut(zipCode, city, 10, TimeUnit.SECONDS) //Eliminará la entrada del mapa cada 10 segundos
                                    .doOnNext(result -> {
                                        String message = result ?
                                                "Nuevo valor guardado en Redis para zipCode: {}" :
                                                "ZipCode {} ya existía en Redis (valor reemplazado)";
                                        log.info(message, zipCode);
                                    })
                                    .thenReturn(city));

                }));
    }
}
