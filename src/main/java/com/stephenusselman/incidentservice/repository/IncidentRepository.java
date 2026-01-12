package com.stephenusselman.incidentservice.repository;

import org.springframework.stereotype.Repository;

import com.stephenusselman.incidentservice.domain.Incident;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

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

    /**
     * Queries incidents by severity using the {@code severity-index}..
     *
     * @param severity the severity value to query (GSI partition key)
     * @param limit the maximum number of items to return
     * @param lastEvaluatedKey the pagination cursor from a previous query,
     *                         or {@code null} to start from the beginning
     * @return a {@link Page} containing incidents and pagination metadata
     */
    public Page<Incident> queryBySeverity(String severity, int limit, Map<String, AttributeValue> lastEvaluatedKey) {
        return table.index("severity-index")
                .query(r -> r
                        .queryConditional(
                            QueryConditional.keyEqualTo(k -> k.partitionValue(severity))
                        )
                        .limit(limit)
                        .exclusiveStartKey(lastEvaluatedKey)
                )
                .iterator()
                .next();
        }
    
    /**
     * Queries incidents by category using the {@code category-index}.
     *
     * @param category the category value to query (GSI partition key)
     * @param limit the maximum number of items to return
     * @param lastEvaluatedKey the pagination cursor from a previous query,
     *                         or {@code null} to start from the beginning
     * @return a {@link Page} containing incidents and pagination metadata
     */
    public Page<Incident> queryByCategory(String category, int limit, Map<String, AttributeValue> lastEvaluatedKey) {
        return table.index("category-index")
                .query(r -> r
                        .queryConditional(
                                QueryConditional.keyEqualTo(
                                        Key.builder()
                                                .partitionValue(category)
                                                .build()
                                )
                        )
                        .limit(limit)
                        .exclusiveStartKey(lastEvaluatedKey)
                )
                .iterator()
                .next();
        }
}