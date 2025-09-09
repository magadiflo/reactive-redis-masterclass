package dev.magadiflo.performance.app.service.impl;

import dev.magadiflo.performance.app.entity.Product;
import dev.magadiflo.performance.app.service.ProductService;
import dev.magadiflo.performance.app.service.ProductVisitService;
import dev.magadiflo.performance.app.service.util.CacheTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service("v2")
public class ProductServiceImplV2 implements ProductService {

    private final CacheTemplate<Integer, Product> productCacheTemplate;
    private final ProductVisitService productVisitService;

    @Override
    public Mono<Product> getProduct(Integer productId) {
        return this.productCacheTemplate.get(productId)
                .doFirst(() -> this.productVisitService.addVisit(productId));
    }

    @Override
    public Mono<Product> updateProduct(Integer productId, Product product) {
        return this.productCacheTemplate.update(productId, product);
    }

    @Override
    public Mono<Void> deleteProduct(Integer productId) {
        return this.productCacheTemplate.delete(productId);
    }
}
