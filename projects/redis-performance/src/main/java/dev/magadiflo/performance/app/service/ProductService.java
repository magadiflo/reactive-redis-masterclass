package dev.magadiflo.performance.app.service;

import dev.magadiflo.performance.app.entity.Product;
import reactor.core.publisher.Mono;

public interface ProductService {
    Mono<Product> getProduct(Integer productId);

    Mono<Product> updateProduct(Integer productId, Product product);

    Mono<Void> deleteProduct(Integer productId);
}
