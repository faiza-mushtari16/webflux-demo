package com.example.demo.repository;

import com.example.demo.model.Product;
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
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {

    private final DynamoDbEnhancedAsyncClient enhancedAsyncClient;
    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    public ProductRepository(DynamoDbEnhancedAsyncClient enhancedAsyncClient, DynamoDbAsyncClient dynamoDbAsyncClient) {
        this.enhancedAsyncClient = enhancedAsyncClient;
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
    }

    public Mono<Product> save(Product product) {
        return Mono.fromCallable(() -> {
                    enhancedAsyncClient.table("Product", TableSchema.fromBean(Product.class))
                            .putItem(product)
                            .get();
                    return product;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Product> findById(String id) {
        return Mono.fromCallable(() ->
                        enhancedAsyncClient.table("Product", TableSchema.fromBean(Product.class))
                                .getItem(r -> r.key(k -> k.partitionValue(id)))
                                .get()
                )
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteById(String id) {
        return Mono.fromCallable(() -> {
                    DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                            .tableName("Product")
                            .key(Map.of("id", AttributeValue.builder().s(id).build()))
                            .build();
                    dynamoDbAsyncClient.deleteItem(deleteItemRequest).get();
                    return null;
                })
                .subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Flux<Product> findAll() {
        return Mono.fromCallable(() -> {
                    ScanRequest scanRequest = ScanRequest.builder().tableName("Product").build();
                    ScanResponse response = dynamoDbAsyncClient.scan(scanRequest).get();
                    return response.items().stream()
                            .map(item -> Product.builder()
                                    .id(item.get("id").s())
                                    .name(item.get("name").s())
                                    .price(Double.valueOf(item.get("price").n()))
                                    .build())
                            .collect(Collectors.toList());
                })
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
