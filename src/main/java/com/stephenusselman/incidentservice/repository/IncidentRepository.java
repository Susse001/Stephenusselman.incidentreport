package com.stephenusselman.incidentservice.repository;

import org.springframework.stereotype.Repository;

import com.stephenusselman.incidentservice.domain.Incident;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

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

    /**
     * Query incidents by severity using the severity GSI.
     */
    public List<Incident> findBySeverity(String severity) {
        DynamoDbIndex<Incident> index =
                table.index("severity-index");

        List<Incident> results = new ArrayList<>();

        index.query(
                QueryConditional.keyEqualTo(
                        Key.builder()
                                .partitionValue(severity)
                                .build()
                )
        ).forEach(page -> results.addAll(page.items()));

        return results;
    }

    /**
     * Query incidents by category using the category GSI.
     */
    public List<Incident> findByCategory(String category) {
        DynamoDbIndex<Incident> index =
                table.index("category-index");

        List<Incident> results = new ArrayList<>();

        index.query(
                QueryConditional.keyEqualTo(
                        Key.builder()
                                .partitionValue(category)
                                .build()
                )
        ).forEach(page -> results.addAll(page.items()));

        return results;
    }
}