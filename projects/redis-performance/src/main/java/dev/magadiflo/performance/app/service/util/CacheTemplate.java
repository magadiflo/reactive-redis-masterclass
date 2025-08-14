package dev.magadiflo.performance.app.service.util;

import reactor.core.publisher.Mono;

public abstract class CacheTemplate<KEY, ENTITY> {

    public final Mono<ENTITY> get(KEY key) {
        return this.getFromCache(key)
                .switchIfEmpty(this.getFromSource(key)
                        .flatMap(entity -> this.updateCache(key, entity))
                );
    }

    public final Mono<ENTITY> update(KEY key, ENTITY entity) {
        return this.updateSource(key, entity)
                .flatMap(updatedEntity -> this.deleteFromCache(key)
                        .thenReturn(updatedEntity)
                );
    }

    public final Mono<Void> delete(KEY key) {
        return this.deleteFromSource(key)
                .then(this.deleteFromCache(key));
    }

    protected abstract Mono<ENTITY> getFromSource(KEY key);

    protected abstract Mono<ENTITY> getFromCache(KEY key);

    protected abstract Mono<ENTITY> updateSource(KEY key, ENTITY entity);

    protected abstract Mono<ENTITY> updateCache(KEY key, ENTITY entity);

    protected abstract Mono<Void> deleteFromSource(KEY key);

    protected abstract Mono<Void> deleteFromCache(KEY key);
}
