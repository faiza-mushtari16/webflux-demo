package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<Product>> create(@RequestBody Product product) {
        try {
            return service.create(product)
                    .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p));
        } catch (Exception ex) {
            return Mono.error(ex);
        }
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> get(@PathVariable String id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<Product> list() {
        return service.list();
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> update(@PathVariable String id, @RequestBody Product product) {
        return service.update(id, product)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.delete(id).thenReturn(ResponseEntity.noContent().build());
    }
}
