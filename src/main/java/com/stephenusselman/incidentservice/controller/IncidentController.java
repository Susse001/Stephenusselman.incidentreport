package com.stephenusselman.incidentservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.CreateIncidentRequest;
import com.stephenusselman.incidentservice.dto.IncidentResponse;
import com.stephenusselman.incidentservice.service.IncidentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller for managing incidents in the Smart Incident Service.
 */
@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {
    /** Service layer for handling incident-related operations */
    private final IncidentService incidentService;

    /**
     * Create a new incident based on the request payload.
     * 
     * @param request the incident creation request containing details such as
     *                description and reporter information
     * @return an {@link IncidentResponse} containing the ID, description, and
     *         creation timestamp of the newly created incident
     */
    @PostMapping
    public IncidentResponse createIncident( @Valid @RequestBody CreateIncidentRequest request) {

        Incident incident = incidentService.createIncident(request);

        return IncidentResponse.builder()
                .incidentId(incident.getIncidentId())
                .description(incident.getDescription())
                .createdAt(incident.getCreatedAt())
                .build();
    }

    @GetMapping("/{id}")
    public IncidentResponse getIncident(@PathVariable String id) {
    Incident incident = incidentService.getIncident(id);

    if (incident == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found");
    }

    return IncidentResponse.builder()
            .incidentId(incident.getIncidentId())
            .description(incident.getDescription())
            .createdAt(incident.getCreatedAt())
            .build();
}
}
