package com.example.demo.repository;

import com.example.demo.common.dto.Response;
import com.example.demo.common.handler.ResponseHandler;
import com.example.demo.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final DynamoDbEnhancedAsyncClient enhancedAsyncClient;
    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    public Mono<Response<Product>> save(Product product) {
        return Mono.defer(() -> Mono.fromFuture(() -> enhancedAsyncClient.table("Product", TableSchema.fromBean(Product.class))
                                .putItem(product))
                        .then(Mono.fromCallable(() -> ResponseHandler.createSuccessResponse("Product saved successfully", product))))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> ResponseHandler.handleErrorResponse("saving product", e));
    }

    public Mono<Response<Product>> findById(String id) {
        return Mono.defer(() -> Mono.fromFuture(() -> enhancedAsyncClient.table("Product", TableSchema.fromBean(Product.class))
                                .getItem(r -> r.key(k -> k.partitionValue(id))))
                        .map(product -> ResponseHandler.createSuccessResponse("Product retrieved successfully", product)))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> ResponseHandler.handleErrorResponse("retrieving product", e));
    }

    public Mono<Response<Product>> deleteById(String id) {
        return Mono.defer(() -> Mono.fromFuture(() -> enhancedAsyncClient.table("Product", TableSchema.fromBean(Product.class))
                                .getItem(r -> r.key(k -> k.partitionValue(id))))
                        .flatMap(product -> {
                            DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                                    .tableName("Product")
                                    .key(Map.of("id", AttributeValue.builder().s(id).build()))
                                    .build();

                            return Mono.fromFuture(() -> dynamoDbAsyncClient.deleteItem(deleteItemRequest))
                                    .map(v -> new Response<Product>(true, "Product deleted successfully", product));
                        }))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> Mono.just(new Response<Product>(false, "Error deleting product: " + e.getMessage(), null)));
    }

    public Flux<Response<Product>> findAll() {
        return Mono.defer(() -> Mono.fromFuture(() -> dynamoDbAsyncClient.scan(ScanRequest.builder().tableName("Product").build()))
                        .map(scanResponse -> scanResponse.items().stream()
                                .map(item -> Product.builder()
                                        .id(item.get("id").s())
                                        .name(item.get("name").s())
                                        .price(Double.valueOf(item.get("price").n()))
                                        .build())
                                .collect(Collectors.toList())))
                .flatMapMany(Flux::fromIterable)
                .map(product -> ResponseHandler.createSuccessResponse("Product retrieved successfully", product))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> Flux.just(new Response<>(false, "Error retrieving products: " + e.getMessage(), null)));
    }
}
