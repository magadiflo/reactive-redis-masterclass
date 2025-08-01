package dev.magadiflo.redis.app.weather.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class ExternalServiceClient {
    @CachePut(cacheNames = "weather", key = "#zipCode")
    public int getWeatherInfo(int zipCode) {
        return ThreadLocalRandom.current().nextInt(60, 100);
    }
}
