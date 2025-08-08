package dev.magadiflo.performance.app.service.impl;

import dev.magadiflo.performance.app.entity.Product;
import dev.magadiflo.performance.app.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImplV2 implements ProductService {

    @Override
    public Mono<Product> getProduct(Integer productId) {
        return null;
    }

    @Override
    public Mono<Product> updateProduct(Integer productId, Product product) {
        return null;
    }
}
