package com.stephenusselman.incidentservice.service.ai;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.repository.IncidentRepository;
import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentRequest;
import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentResult;

import lombok.RequiredArgsConstructor;

/**
 * Coordinates AI-based enrichment of {@link Incident} entities.
 *
 * Enrichment is executed asynchronously to avoid blocking the main request
 * lifecycle and to isolate AI latency or failures from core incident creation.
 */
@Service
@RequiredArgsConstructor
public class IncidentEnrichmentCoordinator {

    private final AiEnrichmentService aiEnrichmentService;
    private final IncidentRepository repository;
    private final Validator validator;

    //Retry Configuration Values
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_DELAY_MS = 1000; // 1 sec
    private static final long MAX_DELAY_MS = 10000;    // 10 sec max delay
    private static final long TIMEOUT_MS = 10000;

    /**
     * Asynchronously enrich an incident with exponential backoff retry.
     */
    @Async
    public CompletableFuture<Void> enrichIncidentAsync(Incident incident) {
        enrichIncident(incident);
        return CompletableFuture.completedFuture(null);
    }

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

        validate(request);

        int attempt = 0;
        long delay = INITIAL_DELAY_MS;
        boolean success = false;

        while (attempt < MAX_RETRIES && !success) {
            attempt++;
            try {
                IncidentEnrichmentResult result = callAiWithTimeout(request, TIMEOUT_MS);

                validate(result);

                incident.setSeverity(result.getSeverity());
                incident.setCategory(result.getCategory());
                incident.setAiSummary(result.getSummary());
                incident.setRecommendedAction(result.getRecommendedAction());
                incident.setAiStatus("ENRICHED");
                incident.setAiErrorMessage(null);

                success = true;

            } catch (Exception e) {
                if (attempt >= MAX_RETRIES) {
                    incident.setAiStatus("FAILED");
                    incident.setAiErrorMessage(e.getMessage());
                } else {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ignored) {}
                    // Exponential backoff
                    delay = Math.min(delay * 2, MAX_DELAY_MS);
                }
            }
        }
        repository.save(incident);
    }

    /**
     * Invokes the AI enrichment service with a hard timeout.
     *
     * @param request the enrichment request payload
     * @param timeoutMs maximum time to wait for the AI response
     * @return the AI enrichment result
     * @throws Exception if the call fails or times out
     */
    private IncidentEnrichmentResult callAiWithTimeout(IncidentEnrichmentRequest request, long timeoutMs) throws Exception {
        Future<IncidentEnrichmentResult> future =
                Executors.newSingleThreadExecutor().submit(() -> aiEnrichmentService.enrichIncident(request));

        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            future.cancel(true);
            throw new RuntimeException("AI enrichment timed out");
        }
    }

    /**
     * Validates an object using Jakarta Bean Validation.
     *
     * @param obj the object to validate
     * @throws RuntimeException if validation constraints are violated
     */
    private void validate(Object obj) {
        Set<ConstraintViolation<Object>> violations = validator.validate(obj);
        if (!violations.isEmpty()) {
            throw new RuntimeException("Validation failed: " + violations);
        }
    }
}
