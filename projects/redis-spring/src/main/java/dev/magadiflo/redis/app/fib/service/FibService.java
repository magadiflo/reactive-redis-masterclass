package dev.magadiflo.redis.app.fib.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FibService {

    @Cacheable(cacheNames = "math:fib", key = "#index")
    public int getFib(int index) {
        log.info("Calculando fib para índice: {}", index);
        int fib = this.fib(index);
        log.info("Cálculo obtenido: {}", fib);
        return fib;
    }

    @CacheEvict(cacheNames = "math:fib", key = "#index")
    public void clearCache(int index) {
        log.info("Limpiando hash key");

    }

    // Cada 10 segundos que limpie todas las entradas del math:fib
    //@Scheduled(fixedRate = 10_000) // Comentado para que poder trabajar con otras lecciones y no se esté ejecutando cada 10 segundos
    @CacheEvict(cacheNames = "math:fib", allEntries = true)
    public void clearCache() {
        log.info("Limpiando todas las keys del math:fib");

    }

    // Intencional 2^N, usamos recursividad intencionalmente,
    // este es el peor algoritmo, lo hacemos para que se tarde en calcular
    private int fib(int index) {
        if (index < 2) return index;
        return fib(index - 1) + fib(index - 2);
    }
}
