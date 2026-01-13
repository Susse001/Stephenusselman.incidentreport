package com.stephenusselman.incidentservice;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.stephenusselman.incidentservice.dto.IncidentResponse;

class IncidentResponseTest {

    @Test
    void builderCreatesCorrectObject() {
        IncidentResponse response = IncidentResponse.builder()
                .incidentId("123")
                .description("Test")
                .createdAt("2026-01-11T08:00:00Z")
                .aiStatus("ENRICHED")
                .severity("HIGH")
                .category("NETWORK")
                .aiSummary("Mock summary")
                .recommendedAction("Do something")
                .aiErrorMessage(null)
                .build();

        assertEquals("123", response.getIncidentId());
        assertEquals("Test", response.getDescription());
        assertEquals("ENRICHED", response.getAiStatus());
        assertEquals("HIGH", response.getSeverity());
        assertEquals("NETWORK", response.getCategory());
        assertEquals("Mock summary", response.getAiSummary());
        assertEquals("Do something", response.getRecommendedAction());
        assertNull(response.getAiErrorMessage());
    }
}