package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamoDbBean
public class Product {
    private String id;
    private String name;
    private Double price;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public Product setId(String id) {
        this.id = id;
        return this;
    }
}

