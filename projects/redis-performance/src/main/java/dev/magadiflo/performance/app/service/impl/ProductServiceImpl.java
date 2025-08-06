package dev.magadiflo.performance.app.service.impl;

import dev.magadiflo.performance.app.entity.Product;
import dev.magadiflo.performance.app.repository.ProductRepository;
import dev.magadiflo.performance.app.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public Mono<Product> getProduct(Integer productId) {
        return this.productRepository.findById(productId);
    }

    @Override
    @Transactional
    public Mono<Product> updateProduct(Integer productId, Product product) {
        return this.productRepository.findById(productId)
                .map(productDB -> {
                    product.setId(productId);
                    return product;
                })
                .flatMap(this.productRepository::save)
                .switchIfEmpty(Mono.error(() -> new NoSuchElementException("No existe el producto con id: " + productId)));
    }
}
