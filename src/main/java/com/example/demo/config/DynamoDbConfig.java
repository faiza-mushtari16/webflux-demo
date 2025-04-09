package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    private static final String LOCAL_DYNAMO_DB_ENDPOINT = "http://localhost:8000";
    private static final String ACCESS_KEY = "dummyAccessKey";
    private static final String SECRET_KEY = "dummySecretKey";

    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient() {
        return DynamoDbAsyncClient.builder()
                .endpointOverride(URI.create(LOCAL_DYNAMO_DB_ENDPOINT))
                .region(Region.US_WEST_2)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                .build();
    }

    @Bean
    public DynamoDbEnhancedAsyncClient enhancedAsyncClient(DynamoDbAsyncClient dynamoDbAsyncClient) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(dynamoDbAsyncClient)
                .build();
    }
}
