package dev.magadiflo.performance.app.controller;

import dev.magadiflo.performance.app.entity.Product;
import dev.magadiflo.performance.app.service.ProductService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/v2/products")
public class ProductControllerV2 {

    private final ProductService productService;

    public ProductControllerV2(@Qualifier("v2") ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(path = "/{productId}")
    public Mono<ResponseEntity<Product>> getProduct(@PathVariable Integer productId) {
        return this.productService.getProduct(productId)
                .map(ResponseEntity::ok);
    }

    @PutMapping(path = "/{productId}")
    public Mono<ResponseEntity<Product>> updateProduct(@PathVariable Integer productId,
                                                       @RequestBody Mono<Product> productMono) {
        return productMono
                .flatMap(product -> this.productService.updateProduct(productId, product))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(path = "/{productId}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable Integer productId) {
        return this.productService.deleteProduct(productId)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
