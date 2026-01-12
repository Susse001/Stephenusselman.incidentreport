package com.stephenusselman.incidentservice.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

/**
 * Configuration for DynamoDB.
 * Automatically switches between DynamoDB Local (for development) and AWS DynamoDB (for production/EB).
 */
@Configuration
public class DynamoDbConfig {

    /**
     * If true, use DynamoDB Local; if false, use real AWS DynamoDB.
     * Can be set in application.properties or environment variable.
     */
    @Value("${dynamodb.local:false}")
    private boolean useLocal;

    @Value("${dynamodb.region}")
    private String region;

    /**
     * Low-level DynamoDB client.
     * Uses either local endpoint or AWS credentials depending on environment.
     */
    @Bean
    public DynamoDbClient dynamoDbClient() {
        if (useLocal) {
            // DynamoDB Local (development)
            return DynamoDbClient.builder()
                    .endpointOverride(URI.create("http://localhost:8000"))
                    .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create("dummy", "dummy")
                            )
                    )
                    .build();
        } else {
            // Real AWS DynamoDB (production)
            return DynamoDbClient.builder()
                    .region(Region.of(region))
                    .build();
        }
    }

    /**
     * DynamoDB Enhanced Client for object mapping.
     */
    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }
}