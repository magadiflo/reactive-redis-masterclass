package dev.magadiflo.redis.app.weather.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    record Product(int id, String name) {
    }

    @Cacheable(cacheNames = "product", key = "#id")
    public Product getProduct(int id) {
        ....
        ....
        return product;
    }

    @Cacheable(cacheNames = "product", key = "#id")
    public Mono<Product> getProduct(int id) {
        return Mono.fromSupplier(() -> ....)
        .map(...)
                .flatMap(...);
    }
}
