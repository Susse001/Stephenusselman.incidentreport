package com.stephenusselman.incidentservice.service.ai;

import org.springframework.stereotype.Service;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.repository.IncidentRepository;
import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentRequest;
import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncidentEnrichmentCoordinator {

    private final AiEnrichmentService aiEnrichmentService;
    private final IncidentRepository repository;
    private final Validator validator;

    /**
     * Perform AI enrichment on an incident.
     *
     * @param incident the incident to enrich
     */
    public void enrichIncident(Incident incident) {
        IncidentEnrichmentRequest request = new IncidentEnrichmentRequest(
            incident.getIncidentId(),
            incident.getDescription(),
            incident.getReportedBy(),
            incident.getCreatedAt()
        );

        // Validate request
        validate(request);

        try {
            // Call AI service
            IncidentEnrichmentResult result = aiEnrichmentService.enrichIncident(request);

            // Validate AI output
            validate(result);

            // Persist results
            incident.setSeverity(result.getSeverity());
            incident.setCategory(result.getCategory());
            incident.setAiSummary(result.getSummary());
            incident.setRecommendedAction(result.getRecommendedAction());
            incident.setAiStatus("ENRICHED");
            incident.setAiErrorMessage(null);

        } catch (Exception e) {
            incident.setAiStatus("FAILED");
            incident.setAiErrorMessage(e.getMessage());
        }

        // Save incident (updated AI fields)
        repository.save(incident);
    }

    private void validate(Object obj) {
        Set<ConstraintViolation<Object>> violations = validator.validate(obj);
        if (!violations.isEmpty()) {
            throw new RuntimeException("Validation failed: " + violations);
        }
    }
}
