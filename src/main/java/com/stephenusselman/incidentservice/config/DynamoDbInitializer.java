package com.stephenusselman.incidentservice.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;


/**
 * Initializes the DynamoDB table for Incidents at application startup.
 * Ensures the table exists with proper global secondary indexes.
 * Only runs for "local" or "seed" profiles.
 */
@Configuration
@Profile({"local", "seed"})
public class DynamoDbInitializer {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbClient dynamoDbClient;

    public DynamoDbInitializer(DynamoDbEnhancedClient enhancedClient, DynamoDbClient dynamoDbClient) {
        this.enhancedClient = enhancedClient;
        this.dynamoDbClient = dynamoDbClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        String tableName = "Incidents";

        // Check if table already exists
        try {
            dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
            System.out.println("DynamoDB table '" + tableName + "' already exists. Skipping creation.");
            return;
        } catch (ResourceNotFoundException e) {
            System.out.println("DynamoDB table '" + tableName + "' not found. Creating...");
        }

        // Define attribute definitions
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(AttributeDefinition.builder().attributeName("incidentId").attributeType("S").build());
        attributeDefinitions.add(AttributeDefinition.builder().attributeName("severity").attributeType("S").build());
        attributeDefinitions.add(AttributeDefinition.builder().attributeName("category").attributeType("S").build());
        attributeDefinitions.add(AttributeDefinition.builder().attributeName("createdAt").attributeType("S").build());

        // Define key schema
        List<KeySchemaElement> keySchema = new ArrayList<>();
        keySchema.add(KeySchemaElement.builder().attributeName("incidentId").keyType(KeyType.HASH).build());

        // Define global secondary indexes
        List<GlobalSecondaryIndex> gsiList = new ArrayList<>();

        gsiList.add(GlobalSecondaryIndex.builder()
                .indexName("severity-index")
                .keySchema(
                        KeySchemaElement.builder().attributeName("severity").keyType(KeyType.HASH).build(),
                        KeySchemaElement.builder().attributeName("createdAt").keyType(KeyType.RANGE).build()
                )
                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                .build());

        gsiList.add(GlobalSecondaryIndex.builder()
                .indexName("category-index")
                .keySchema(
                        KeySchemaElement.builder().attributeName("category").keyType(KeyType.HASH).build(),
                        KeySchemaElement.builder().attributeName("createdAt").keyType(KeyType.RANGE).build()
                )
                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                .build());

        // Build create table request
        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(tableName)
                .attributeDefinitions(attributeDefinitions)
                .keySchema(keySchema)
                .globalSecondaryIndexes(gsiList)
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                .build();

        // Create the table
        dynamoDbClient.createTable(request);
        while (true) {
            try {
                String status = dynamoDbClient.describeTable(
                    DescribeTableRequest.builder().tableName(tableName).build()
                ).table().tableStatusAsString();

                if ("ACTIVE".equals(status)) break;
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
                }
            }
        System.out.println("DynamoDB table '" + tableName + "' is ACTIVE with GSIs.");
    }
}