package com.stephenusselman.incidentservice;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.CreateIncidentRequest;
import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentResult;
import com.stephenusselman.incidentservice.repository.IncidentRepository;
import com.stephenusselman.incidentservice.service.IncidentService;
import com.stephenusselman.incidentservice.service.ai.AiEnrichmentService;
import com.stephenusselman.incidentservice.service.ai.IncidentEnrichmentCoordinator;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    
    @Mock
    private AiEnrichmentService aiEnrichmentService;

    @Mock
    private IncidentEnrichmentCoordinator AiEnrichmentCoordinator;

    @InjectMocks
    private IncidentService incidentService;


    public IncidentServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenCreateIncident_callsRepositorySave() {
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setDescription("Test incident");
        request.setSeverity("HIGH");
        request.setCategory("NETWORK");
        request.setReportedBy("User123");

        IncidentEnrichmentResult mockResult = new IncidentEnrichmentResult(
        "HIGH",
        "NETWORK",
        "Short summary",
        "Do this action"
        );

        when(aiEnrichmentService.enrichIncident(org.mockito.ArgumentMatchers.any()))
        .thenReturn(mockResult);

        incidentService.createIncident(request);

        verify(incidentRepository, times(1)).save(org.mockito.ArgumentMatchers.any(Incident.class));
    }
}