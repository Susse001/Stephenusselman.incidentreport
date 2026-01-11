package com.stephenusselman.incidentservice;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.CreateIncidentRequest;
import com.stephenusselman.incidentservice.repository.IncidentRepository;
import com.stephenusselman.incidentservice.service.IncidentService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

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

        incidentService.createIncident(request);

        verify(incidentRepository, times(1)).save(org.mockito.ArgumentMatchers.any(Incident.class));
    }
}