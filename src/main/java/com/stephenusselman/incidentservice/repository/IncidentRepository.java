package com.stephenusselman.incidentservice.repository;

import org.springframework.stereotype.Repository;

import com.stephenusselman.incidentservice.domain.Incident;

import software.amazon.awssdk.enhanced.dynamodb.*;

@Repository
public class IncidentRepository {

    private final DynamoDbTable<Incident> table;

    public IncidentRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table(
                "incidents",
                TableSchema.fromBean(Incident.class)
        );
    }

    public void save(Incident incident) {
        table.putItem(incident);
    }

    public Incident findById(String incidentId) {
        return table.getItem(
                Key.builder().partitionValue(incidentId).build()
        );
    }
}