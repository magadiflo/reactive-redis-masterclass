package dev.magadiflo.performance.app.config;

import dev.magadiflo.performance.app.entity.Product;
import dev.magadiflo.performance.app.repository.ProductRepository;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import reactor.core.publisher.Flux;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class DataSetupConfig {

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ClassPathResource scheme = new ClassPathResource("sql/scheme.sql");
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(scheme);

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(resourceDatabasePopulator);
        return initializer;
    }

    @Bean
    public CommandLineRunner runner(ProductRepository productRepository) {
        return args -> Flux.range(1, 1000)
                .map(i -> Product.builder()
                        .description("product-" + i)
                        .price(ThreadLocalRandom.current().nextInt(1, 100))
                        .build()
                )
                .collectList()
                .flatMapMany(productRepository::saveAll)
                .then()
                .doFinally(signalType -> log.info("Configuración de datos realizada: {}", signalType))
                .subscribe();
    }
}
