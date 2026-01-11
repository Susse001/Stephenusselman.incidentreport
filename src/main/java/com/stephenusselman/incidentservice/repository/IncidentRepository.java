package com.stephenusselman.incidentservice.repository;

import org.springframework.stereotype.Repository;

import com.stephenusselman.incidentservice.domain.Incident;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

/**
 * Repository for managing Incident entities in DynamoDB.
 */
@Repository
public class IncidentRepository {

    private final DynamoDbTable<Incident> table;

    public IncidentRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table(
                "Incidents",
                TableSchema.fromBean(Incident.class)
        );
    }

    /**
     * Saves an incident to DynamoDB.
     */
    public void save(Incident incident) {
        table.putItem(incident);
    }

    /**
     * Retrieves an incident by its ID.
     */
    public Incident findById(String incidentId) {
        return table.getItem(
                Key.builder().partitionValue(incidentId).build()
        );
    }
}