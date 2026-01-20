package com.stephenusselman.incidentservice.service.ai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Set;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentRequest;
import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentResult;
import com.stephenusselman.incidentservice.repository.IncidentRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class IncidentEnrichmentCoordinatorTest {

    @Mock
    private AiEnrichmentService aiEnrichmentService;

    @Mock
    private IncidentRepository repository;

    @Mock
    private Validator validator;

    @InjectMocks
    private IncidentEnrichmentCoordinator coordinator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Ensures a successful AI enrichment populates all AI fields,
     * sets the status to ENRICHED, and persists the incident.
     */
    @Test
    void whenAiEnrichmentSucceeds_thenIncidentIsEnrichedAndSaved() {
        Incident incident = baseIncident();

        IncidentEnrichmentResult result = new IncidentEnrichmentResult(
                "HIGH",
                "NETWORK",
                "AI summary",
                "Restart service"
        );

        when(validator.validate(any())).thenReturn(Collections.emptySet());
        when(aiEnrichmentService.enrichIncident(any(IncidentEnrichmentRequest.class)))
                .thenReturn(result);

        coordinator.enrichIncident(incident);

        assertEquals("HIGH", incident.getSeverity());
        assertEquals("NETWORK", incident.getCategory());
        assertEquals("AI summary", incident.getAiSummary());
        assertEquals("Restart service", incident.getRecommendedAction());
        assertEquals("ENRICHED", incident.getAiStatus());
        assertNull(incident.getAiErrorMessage());

        verify(repository, times(1)).save(incident);
    }

    /**
     * Ensures that when the AI service consistently fails,
     * the incident is marked as FAILED and the error message is recorded.
     */
    @Test
    void whenAiEnrichmentFails_thenIncidentIsMarkedFailed() {
        Incident incident = baseIncident();

        when(validator.validate(any())).thenReturn(Collections.emptySet());
        when(aiEnrichmentService.enrichIncident(any()))
                .thenThrow(new RuntimeException("AI unavailable"));

        coordinator.enrichIncident(incident);

        assertEquals("FAILED", incident.getAiStatus());
        assertNotNull(incident.getAiErrorMessage());

        verify(repository, times(1)).save(incident);
    }

    /**
     * Ensures validation failure on the enrichment request
     * prevents AI invocation and propagates an exception.
     */
    @Test
    void whenRequestValidationFails_thenExceptionIsThrown() {
        Incident incident = baseIncident();

        Set<ConstraintViolation<Object>> violations =
                Set.of(mock(ConstraintViolation.class));

        when(validator.validate(any()))
                .thenReturn(violations);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> coordinator.enrichIncident(incident)
        );

        assertTrue(exception.getMessage().contains("Validation failed"));

        verify(aiEnrichmentService, never()).enrichIncident(any());
        verify(repository, never()).save(any());
    }

    /**
     * Ensures validation failure on the AI result
     * causes enrichment to fail and marks the incident as FAILED.
     */
    @Test
    void whenResultValidationFails_thenIncidentIsMarkedFailed() {
        Incident incident = baseIncident();

        IncidentEnrichmentResult invalidResult =
                new IncidentEnrichmentResult(null, null, null, null);

        when(validator.validate(any(IncidentEnrichmentRequest.class)))
                .thenReturn(Collections.emptySet());

        when(aiEnrichmentService.enrichIncident(any()))
                .thenReturn(invalidResult);

        when(validator.validate(any(IncidentEnrichmentResult.class)))
                .thenReturn(Set.of(mock(ConstraintViolation.class)));

        coordinator.enrichIncident(incident);

        assertEquals("FAILED", incident.getAiStatus());
        assertNotNull(incident.getAiErrorMessage());

        verify(repository, times(1)).save(incident);
    }

    /**
     * Ensures the async wrapper delegates to the synchronous
     * enrichment logic without throwing.
     */
    @Test
    void enrichIncidentAsync_completesSuccessfully() {
        Incident incident = baseIncident();

        when(validator.validate(any())).thenReturn(Collections.emptySet());
        when(aiEnrichmentService.enrichIncident(any()))
                .thenThrow(new RuntimeException("Failure"));

        assertDoesNotThrow(() ->
                coordinator.enrichIncidentAsync(incident).join()
        );

        verify(repository, times(1)).save(incident);
    }

    /**
     * Creates a minimal valid incident for enrichment testing.
     */
    private Incident baseIncident() {
        Incident incident = new Incident();
        incident.setIncidentId("INC-123");
        incident.setDescription("Test incident");
        incident.setReportedBy("user");
        incident.setCreatedAt("2024-01-01T00:00:00Z");
        incident.setAiStatus("PENDING");
        return incident;
    }
}