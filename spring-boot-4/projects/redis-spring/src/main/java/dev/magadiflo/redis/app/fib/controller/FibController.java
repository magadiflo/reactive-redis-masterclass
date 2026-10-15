package dev.magadiflo.redis.app.fib.controller;

import dev.magadiflo.redis.app.fib.service.FibService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/{version}/fib", version = "1")
public class FibController {

    private final FibService fibService;

    @GetMapping(path = "/{index}")
    public Mono<ResponseEntity<Integer>> getFib(@PathVariable int index) {
        return Mono.just(ResponseEntity.ok(this.fibService.getFib(index)));
    }

    @GetMapping(path = "/{index}/clear")
    public Mono<ResponseEntity<Void>> clear(@PathVariable int index) {
        this.fibService.clearCache(index);
        return Mono.just(ResponseEntity.noContent().build());
    }
}
