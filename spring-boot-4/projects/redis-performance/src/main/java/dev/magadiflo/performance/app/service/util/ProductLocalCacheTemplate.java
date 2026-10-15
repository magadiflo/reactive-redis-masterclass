package dev.magadiflo.performance.app.service.util;

import dev.magadiflo.performance.app.entity.Product;
import dev.magadiflo.performance.app.repository.ProductRepository;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.LocalCachedMapOptions;
import org.redisson.codec.TypedJsonJackson3Codec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service("productLocalCacheTemplate")
public class ProductLocalCacheTemplate extends CacheTemplate<Integer, Product> {

    private final ProductRepository productRepository;
    private final RLocalCachedMap<Integer, Product> productRLocalCachedMap;

    public ProductLocalCacheTemplate(ProductRepository productRepository, RedissonClient redissonClient) {
        LocalCachedMapOptions<Integer, Product> mapOptions = LocalCachedMapOptions.<Integer, Product>name("products")
                .codec(new TypedJsonJackson3Codec(Integer.class, Product.class))
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR)
                .retryAttempts(3)
                .retryDelay(attempt -> Duration.ofMillis(500 + attempt * 1000L));


        this.productRepository = productRepository;
        this.productRLocalCachedMap = redissonClient.getLocalCachedMap(mapOptions);
    }

    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return this.productRepository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return Mono.fromSupplier(() -> this.productRLocalCachedMap.get(id));
    }

    @Override
    protected Mono<Product> updateSource(Integer id, Product product) {
        return this.productRepository.findById(id)
                .flatMap(productDB -> {
                    product.setId(productDB.getId());
                    return this.productRepository.save(product);
                });
    }

    @Override
    protected Mono<Product> updateCache(Integer id, Product product) {
        return Mono.fromCompletionStage(() -> this.productRLocalCachedMap.fastPutAsync(id, product))
                .thenReturn(product);
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return this.productRepository.deleteById(id);
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return Mono.fromCompletionStage(() -> this.productRLocalCachedMap.fastRemoveAsync(id))
                .then();
    }
}
