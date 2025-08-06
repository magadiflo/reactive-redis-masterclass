package dev.magadiflo.performance.app.repository;

import dev.magadiflo.performance.app.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {
}
