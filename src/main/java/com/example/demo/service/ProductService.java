package com.example.demo.service;

import com.example.demo.common.dto.Response;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Mono<Response<Product>> create(Product product) {
        return repository.save(product.setId(UUID.randomUUID().toString()));
    }

    public Mono<Response<Product>> get(String id) {
        return repository.findById(id);
    }

    public Mono<Response<Product>> delete(String id) {
        return repository.deleteById(id);
    }

    public Flux<Response<Product>> list() {
        return repository.findAll();
    }

    public Mono<Response<Product>> update(String id, Product product) {
        return repository.save(product.setId(id));
    }
}
