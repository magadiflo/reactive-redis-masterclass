package dev.magadiflo.performance.app.service.util;

import dev.magadiflo.performance.app.entity.Product;
import dev.magadiflo.performance.app.repository.ProductRepository;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductCacheTemplate extends CacheTemplate<Integer, Product> {

    private final ProductRepository productRepository;
    private final RMapReactive<Integer, Product> productRMapReactive;

    public ProductCacheTemplate(ProductRepository productRepository, RedissonReactiveClient redissonReactiveClient) {
        this.productRepository = productRepository;
        this.productRMapReactive = redissonReactiveClient.getMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class));
    }

    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return this.productRepository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return this.productRMapReactive.get(id);
    }

    @Override
    protected Mono<Product> updateSource(Integer id, Product product) {
        return this.productRepository.findById(id)
                .doOnNext(productDB -> product.setId(id))
                .flatMap(productDB -> this.productRepository.save(product));
    }

    @Override
    protected Mono<Product> updateCache(Integer id, Product product) {
        return this.productRMapReactive.fastPut(id, product).thenReturn(product);
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return this.productRepository.deleteById(id);
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return this.productRMapReactive.fastRemove(id).then();
    }
}
