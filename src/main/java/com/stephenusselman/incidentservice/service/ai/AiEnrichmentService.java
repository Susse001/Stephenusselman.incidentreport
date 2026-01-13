package com.stephenusselman.incidentservice.service.ai;

import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentRequest;
import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentResult;

/**
 * Service interface for enriching incidents using AI.
 */
public interface AiEnrichmentService {

    /**
     * Enriches the incident using AI.
     *
     * @param request the incident data to enrich
     * @return the AI-enriched result, including severity, category, summary, and recommended actions
     */
    IncidentEnrichmentResult enrich(IncidentEnrichmentRequest request);
}