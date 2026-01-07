package com.stephenusselman.incidentservice.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.CreateIncidentRequest;

/**
     * Create a new incident based on the provided request data.
     * 
     * @param request the DTO containing the information needed to create an incident
     * @return the newly created {@link Incident} entity
     */
@Service
public class IncidentService {
    
    public Incident createIncident(CreateIncidentRequest request) {
        return Incident.builder()
                .incidentId(UUID.randomUUID().toString())
                .description(request.getDescription())
                .reportedBy(request.getReportedBy())
                .createdAt(Instant.now())
                .build();
    }
}
