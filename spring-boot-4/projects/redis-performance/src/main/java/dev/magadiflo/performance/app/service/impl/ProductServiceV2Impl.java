package dev.magadiflo.performance.app.service.impl;

import dev.magadiflo.performance.app.entity.Product;
import dev.magadiflo.performance.app.service.ProductService;
import dev.magadiflo.performance.app.service.ProductVisitService;
import dev.magadiflo.performance.app.service.util.CacheTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service("v2")
public class ProductServiceV2Impl implements ProductService {

    private final CacheTemplate<Integer, Product> cacheTemplate;
    private final ProductVisitService productVisitService;

    public ProductServiceV2Impl(@Qualifier("productLocalCacheTemplate") CacheTemplate<Integer, Product> cacheTemplate,
                                ProductVisitService productVisitService) {
        this.cacheTemplate = cacheTemplate;
        this.productVisitService = productVisitService;
    }

    @Override
    public Mono<Product> getProduct(Integer productId) {
        return this.cacheTemplate.get(productId)
                .doFirst(() -> this.productVisitService.addVisit(productId));
    }

    @Override
    public Mono<Product> updateProduct(Integer productId, Product product) {
        return this.cacheTemplate.update(productId, product);
    }

    @Override
    public Mono<Void> deleteProduct(Integer productId) {
        return this.cacheTemplate.delete(productId);
    }
}
