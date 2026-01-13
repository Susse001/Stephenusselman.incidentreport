package com.stephenusselman.incidentservice.service.ai;

import org.springframework.stereotype.Service;

import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentRequest;
import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentResult;

/**
 * Mock implementation of AI enrichment service.
 * Returns deterministic values for testing and development.
 */
@Service
public class MockAiEnrichmentService implements AiEnrichmentService {

    @Override
    public IncidentEnrichmentResult enrich(IncidentEnrichmentRequest request) {

        // For testing purposes, return deterministic values
        return new IncidentEnrichmentResult(
                "HIGH",               // severity
                "SECURITY",           // category
                "Mock summary of the incident", // summary
                "Take immediate action"        // recommendedAction
        );
    }
}