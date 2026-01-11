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
                .build();

        assertEquals("123", response.getIncidentId());
        assertEquals("Test", response.getDescription());
    }
}