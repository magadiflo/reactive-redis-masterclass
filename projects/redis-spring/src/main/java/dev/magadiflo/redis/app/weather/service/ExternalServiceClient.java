package dev.magadiflo.redis.app.weather.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class ExternalServiceClient {
    @CachePut(cacheNames = "weather", key = "#zipCode")
    public int getWeatherInfo(int zipCode) {
        log.info("zipCode: {}", zipCode);
        return ThreadLocalRandom.current().nextInt(60, 100);
    }
}
