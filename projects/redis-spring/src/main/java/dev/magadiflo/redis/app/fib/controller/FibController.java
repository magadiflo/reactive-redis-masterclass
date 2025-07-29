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
@RequestMapping(path = "/api/v1/fib")
public class FibController {

    private final FibService fibService;

    @GetMapping(path = "/{index}")
    public Mono<ResponseEntity<Integer>> getFib(@PathVariable int index) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(this.fibService.getFib(index)));
    }

}
