package com.stephenusselman.incidentservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IncidentResponseTest {

    /**
    * Verifies that the Lombok builder correctly populates all fields
    * and that getters return the expected values.
    */
    @Test
    void builderCreatesValidIncidentResponse() {
        IncidentResponse response = IncidentResponse.builder()
            .incidentId("INC-123")
            .description("Database outage affecting read replicas")
            .reportedBy("monitoring-service")
            .createdAt("2024-01-01T12:00:00Z")
            .aiStatus("ENRICHED")
            .severity("HIGH")
            .category("DATABASE")
            .aiSummary("Read replicas became unavailable due to disk saturation")
            .recommendedAction("Increase disk capacity and rebalance replicas")
            .aiErrorMessage(null)
            .build();

        assertEquals("INC-123", response.getIncidentId());
        assertEquals("Database outage affecting read replicas", response.getDescription());
        assertEquals("monitoring-service", response.getReportedBy());
        assertEquals("2024-01-01T12:00:00Z", response.getCreatedAt());
        assertEquals("ENRICHED", response.getAiStatus());
        assertEquals("HIGH", response.getSeverity());
        assertEquals("DATABASE", response.getCategory());
        assertEquals(
            "Read replicas became unavailable due to disk saturation",
            response.getAiSummary()
        );
        assertEquals(
            "Increase disk capacity and rebalance replicas",
            response.getRecommendedAction()
        );
        assertNull(response.getAiErrorMessage());
    }

    /**
    * Ensures optional response fields may be null without causing
    * errors, supporting partially enriched incidents.
    */
    @Test
    void builderAllowsNullOptionalFields() {
        IncidentResponse response = IncidentResponse.builder()
            .incidentId("INC-456")
            .description("Incident pending enrichment")
            .reportedBy("user123")
            .createdAt("2024-01-02T08:30:00Z")
            .aiStatus("PENDING")
            .build();

        assertEquals("INC-456", response.getIncidentId());
        assertEquals("Incident pending enrichment", response.getDescription());
        assertEquals("user123", response.getReportedBy());
        assertEquals("2024-01-02T08:30:00Z", response.getCreatedAt());
        assertEquals("PENDING", response.getAiStatus());

        assertNull(response.getSeverity());
        assertNull(response.getCategory());
        assertNull(response.getAiSummary());
        assertNull(response.getRecommendedAction());
        assertNull(response.getAiErrorMessage());
    }
}