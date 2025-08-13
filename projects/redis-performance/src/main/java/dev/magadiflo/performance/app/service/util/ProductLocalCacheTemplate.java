package dev.magadiflo.performance.app.service.util;

import dev.magadiflo.performance.app.entity.Product;
import dev.magadiflo.performance.app.repository.ProductRepository;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.LocalCachedMapOptions;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ProductLocalCacheTemplate extends CacheTemplate<Integer, Product> {

    private final RLocalCachedMap<Integer, Product> productRLocalCachedMap;
    private final ProductRepository productRepository;

    public ProductLocalCacheTemplate(RedissonClient redissonClient, ProductRepository productRepository) {
        LocalCachedMapOptions<Integer, Product> mapOptions = LocalCachedMapOptions.<Integer, Product>name("product")
                .codec(new TypedJsonJacksonCodec(Integer.class, Product.class))
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR)
                .retryAttempts(3)
                .retryDelay(attempt -> Duration.ofMillis(500 + attempt * 1000L));

        this.productRLocalCachedMap = redissonClient.getLocalCachedMap(mapOptions);
        this.productRepository = productRepository;
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
                .doOnNext(productDB -> product.setId(id))
                .flatMap(productDB -> this.productRepository.save(product));
    }

    @Override
    protected Mono<Product> updateCache(Integer id, Product product) {
        return Mono.create(productMonoSink -> {
            this.productRLocalCachedMap.fastPutAsync(id, product)
                    .thenAccept(aBoolean -> productMonoSink.success(product))
                    .exceptionally(throwable -> {
                        productMonoSink.error(throwable);
                        return null;
                    });
        });
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return this.productRepository.deleteById(id);
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return Mono.create(voidMonoSink -> {
            this.productRLocalCachedMap.fastRemoveAsync(id)
                    .thenAccept(aLong -> voidMonoSink.success())
                    .exceptionally(throwable -> {
                        voidMonoSink.error(throwable);
                        return null;
                    });
        });
    }
}
