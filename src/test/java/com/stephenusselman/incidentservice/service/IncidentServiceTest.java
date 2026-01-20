package com.stephenusselman.incidentservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.CreateIncidentRequest;
import com.stephenusselman.incidentservice.repository.IncidentRepository;
import com.stephenusselman.incidentservice.service.ai.IncidentEnrichmentCoordinator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class IncidentServiceTest {

    @Mock
    private IncidentRepository repository;

    @Mock
    private IncidentEnrichmentCoordinator enrichmentCoordinator;

    @InjectMocks
    private IncidentService incidentService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Ensures creating an incident persists it, initializes AI status,
     * and triggers asynchronous AI enrichment.
     */
    @Test
    void whenCreateIncident_thenSavedAndEnrichmentTriggered() {
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setDescription("Test incident");
        request.setReportedBy("user123");

        Incident result = incidentService.createIncident(request);

        verify(repository, times(1)).save(any(Incident.class));
        verify(enrichmentCoordinator, times(1))
                .enrichIncidentAsync(any(Incident.class));

        assertNotNull(result.getIncidentId());
        assertEquals("Test incident", result.getDescription());
        assertEquals("user123", result.getReportedBy());
        assertEquals("PENDING", result.getAiStatus());
        assertNotNull(result.getCreatedAt());
    }

    /**
     * Ensures getIncident delegates directly to the repository.
     */
    @Test
    void whenGetIncident_thenRepositoryIsCalled() {
        Incident incident = new Incident();
        incident.setIncidentId("123");

        when(repository.findById("123")).thenReturn(incident);

        Incident result = incidentService.getIncident("123");

        assertEquals("123", result.getIncidentId());
        verify(repository, times(1)).findById("123");
    }

    /**
     * Ensures searching by severity calls the correct repository method.
     */
    @Test
    void whenSearchBySeverity_thenRepositoryFindBySeverityCalled() {
        when(repository.findBySeverity("HIGH"))
                .thenReturn(List.of(new Incident()));

        List<Incident> results =
                incidentService.searchIncidents("HIGH", null);

        assertEquals(1, results.size());
        verify(repository, times(1)).findBySeverity("HIGH");
        verify(repository, never()).findByCategory(any());
    }

    /**
     * Ensures searching by category calls the correct repository method.
     */
    @Test
    void whenSearchByCategory_thenRepositoryFindByCategoryCalled() {
        when(repository.findByCategory("NETWORK"))
                .thenReturn(List.of(new Incident()));

        List<Incident> results =
                incidentService.searchIncidents(null, "NETWORK");

        assertEquals(1, results.size());
        verify(repository, times(1)).findByCategory("NETWORK");
        verify(repository, never()).findBySeverity(any());
    }

    /**
     * Ensures an exception is thrown when neither severity nor category
     * is provided.
     */
    @Test
    void whenSearchWithoutFilters_thenThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> incidentService.searchIncidents(null, null)
        );
    }
}