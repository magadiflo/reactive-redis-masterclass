package dev.magadiflo.redis.app.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class WeatherService {

    private final ExternalServiceClient externalServiceClient;

    @Cacheable(cacheNames = "weather")
    public int getInfo(int zipCode) {
        return 0;
    }

    @Scheduled(fixedRate = 10_000)
    public void update() {
        log.info("Actualizando clima");
        IntStream.rangeClosed(1, 5)
                .forEach(this.externalServiceClient::getWeatherInfo);
    }
}
