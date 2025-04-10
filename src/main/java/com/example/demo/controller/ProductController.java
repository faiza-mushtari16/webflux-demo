package com.example.demo.controller;

import com.example.demo.common.dto.Response;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    @PostMapping
    public Mono<ResponseEntity<Response<Product>>> create(@RequestBody Product product) {
        return service.create(product)
                .map(newProduct -> ResponseEntity.status(HttpStatus.CREATED).body(newProduct));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Response<Product>>> get(@PathVariable String id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<Response<Product>> list() {
        return service.list();
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Response<Product>>> update(@PathVariable String id, @RequestBody Product product) {
        return service.update(id, product)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Response<Product>>> delete(@PathVariable String id) {
        return service.delete(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
