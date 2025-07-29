package dev.magadiflo.redis.app.fib.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FibService {
    public int getFib(int index) {
        log.info("Calculando fib para índice: {}", index);
        int fib = this.fib(index);
        log.info("Cálculo obtenido: {}", fib);
        return fib;
    }

    // Intencional 2^N, usamos recursividad intencionalmente,
    // este es el peor algoritmo, lo hacemos para que se tarde en calcular
    private int fib(int index) {
        if (index < 2) return index;
        return fib(index - 1) + fib(index - 2);
    }
}
