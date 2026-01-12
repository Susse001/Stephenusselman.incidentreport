package com.stephenusselman.incidentservice.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.CreateIncidentRequest;
import com.stephenusselman.incidentservice.dto.IncidentResponse;
import com.stephenusselman.incidentservice.dto.PagedIncidentResponse;
import com.stephenusselman.incidentservice.repository.IncidentRepository;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import lombok.RequiredArgsConstructor;

/**
 * Service layer for managing incidents.
 */
@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository repository;

    /**
     * Creates a new incident based on the input request.
     * 
     * @param request the DTO containing incident details
     * @return the created Incident entity
     */
    public Incident createIncident(CreateIncidentRequest request) {
        Incident incident = new Incident();
        incident.setIncidentId(UUID.randomUUID().toString());
        incident.setDescription(request.getDescription());
        incident.setSeverity(request.getSeverity());
        incident.setCategory(request.getCategory());
        incident.setReportedBy(request.getReportedBy());
        incident.setCreatedAt(Instant.now().toString());

        repository.save(incident);
        return incident;
    }

    /**
     * Gets the Incident associated with provided id as a string
     *
     * @param id the id
     * 
     * @return the Incident
     */
    public Incident getIncident(String id) {
        return repository.findById(id);
    }

    /**
     * Query incidents by severity OR category.
     */
    public List<Incident> searchIncidents(String severity, String category) {

        if (severity != null) {
            return repository.findBySeverity(severity);
        }

        if (category != null) {
            return repository.findByCategory(category);
        }

        throw new IllegalArgumentException("Either severity or category must be provided");
    }

    /**
     * Searches incidents using either the severity or category secondary index
     * and returns a single paginated page of results.
     *
     * @param severity optional severity filter (e.g. HIGH, MEDIUM, LOW)
     * @param category optional category filter (e.g. NETWORK, SECURITY)
     * @param limit maximum number of items to return in this page
     * @param lastKey encoded pagination key from a previous response, or null
     * @return a {@link PagedIncidentResponse} containing incidents and a
     *         pagination token for the next page, if available
     */
    public PagedIncidentResponse searchIncidents(String severity, String category, int limit, String lastKey) {

        Map<String, AttributeValue> exclusiveStartKey = null;

        if (lastKey != null) {
            exclusiveStartKey = decodeLastKey(lastKey);
        }

        Page<Incident> page;

        if (severity != null) {
            page = repository.queryBySeverity(
                    severity,
                    limit,
                    exclusiveStartKey
            );
        } else {
            page = repository.queryByCategory(
                    category,
                    limit,
                    exclusiveStartKey
            );
        }

        String nextKey = page.lastEvaluatedKey() != null
                ? encodeLastKey(page.lastEvaluatedKey())
                : null;

        return PagedIncidentResponse.builder()
                .items(
                    page.items().stream()
                        .map(this::toResponse)
                        .toList()
                )
                .lastKey(nextKey)
                .build();
    }

    /**
     * Convert a domain {@link Incident} object into an {@link IncidentResponse} DTO.
     *
     * @param incident the incident entity to convert
     * @return a DTO representing the incident, suitable for API responses
     */
    private IncidentResponse toResponse(Incident incident) {
        return IncidentResponse.builder()
                .incidentId(incident.getIncidentId())
                .description(incident.getDescription())
                .createdAt(incident.getCreatedAt())
                .build();
    }

    /**
     * Encode a DynamoDB key map into a Base64 string suitable for pagination.
     * 
     * @param key the DynamoDB key map to encode
     * @return a Base64-encoded string representing the key
     */
    private String encodeLastKey(Map<String, AttributeValue> key) {
        return Base64.getEncoder()
                .encodeToString(key.toString().getBytes());
    }

    /**
     * Decode a Base64-encoded pagination token into a DynamoDB key map.
     * <p>
     * This method is not yet implemented. When implemented, it will allow
     * clients to provide a lastKey value to fetch the next page of results.
     *
     * @param encoded the Base64-encoded key string
     * @return the DynamoDB key map (to be implemented)
     * @throws UnsupportedOperationException always, as this is a placeholder
     */
    private Map<String, AttributeValue> decodeLastKey(String encoded) {
        throw new UnsupportedOperationException(
                "Decoding will be implemented later"
        );
    }
}