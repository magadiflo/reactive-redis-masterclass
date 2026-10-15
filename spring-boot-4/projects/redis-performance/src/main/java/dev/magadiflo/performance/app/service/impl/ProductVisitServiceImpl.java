package dev.magadiflo.performance.app.service.impl;

import dev.magadiflo.performance.app.service.ProductVisitService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.BatchOptions;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductVisitServiceImpl implements ProductVisitService {

    private final RedissonReactiveClient client;
    private final Sinks.Many<Integer> sink = Sinks.many().unicast().onBackpressureBuffer();

    @PostConstruct
    private void init() {
        this.sink.asFlux()
                .buffer(Duration.ofSeconds(3))
                .map(listProductIds -> listProductIds.stream()
                        .collect(Collectors.groupingBy(
                                Function.identity(),
                                Collectors.counting())
                        )
                )
                .flatMap(this::updateBatch)
                .subscribe();
    }

    /**
     * Usamos emitNext() en lugar de tryEmitNext() porque este method puede ser
     * invocado concurrentemente por varias peticiones WebFlux sobre el mismo Sink
     * (el servicio es singleton).
     * <p>
     * - tryEmitNext(): si se produce un acceso concurrente no serializado, puede
     * devolver FAIL_NON_SERIALIZED. Si ignoramos ese resultado, la visita no se
     * registra en el Sink.
     * - emitNext(): permite gestionar los fallos mediante un EmitFailureHandler.
     * Devolver true indica que se debe reintentar la emisión.
     * <p>
     * Reintentamos únicamente ante FAIL_NON_SERIALIZED, ya que otro hilo puede
     * estar emitiendo en ese momento. Los demás fallos, como FAIL_CANCELLED,
     * FAIL_TERMINATED o FAIL_OVERFLOW, no se resuelven simplemente reintentando
     * en las mismas condiciones, por lo que devolvemos false.
     */
    @Override
    public void addVisit(int productId) {
        this.sink.emitNext(productId, (signalType, emitResult) ->
                emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED
        );
    }

    /**
     * Registra los incrementos de visitas de varios productos mediante un lote reactivo de Redisson.
     * <p>
     * Cada llamada a {@code set.addScore(...)} agrega una operación al lote, cuya ejecución se
     * realiza posteriormente mediante {@code batch.execute()}. Aunque {@code addScore(...)}
     * devuelve un {@code Mono<Double>}, no es necesario suscribirse individualmente a cada uno,
     * ya que las operaciones se ejecutan conjuntamente al ejecutar el lote.
     * <p>
     * Se utiliza {@code forEach} para registrar las operaciones del lote y, finalmente,
     * {@code batch.execute().then()} para ejecutarlo y devolver un {@code Mono<Void>} que completa
     * cuando finaliza la ejecución, sin exponer los resultados de las operaciones.
     */
    private Mono<Void> updateBatch(Map<Integer, Long> productVisitCounts) {
        RBatchReactive batch = this.client.createBatch(BatchOptions.defaults());
        String format = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        RScoredSortedSetReactive<Integer> set = batch.getScoredSortedSet("product:visit:" + format, IntegerCodec.INSTANCE);

        productVisitCounts.forEach((productId, visitCount) -> {
            set.addScore(productId, visitCount);
        });

        return batch.execute().then();
    }
}
