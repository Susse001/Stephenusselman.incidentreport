package com.stephenusselman.incidentservice.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.CreateIncidentRequest;
import com.stephenusselman.incidentservice.repository.IncidentRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service layer for managing incidents.
 */
@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository repository;

    /**
     * Creates a new incident based on the input request.
     * 
     * @param request the DTO containing incident details
     * @return the created Incident entity
     */
    public Incident createIncident(CreateIncidentRequest request) {
        Incident incident = new Incident();
        incident.setIncidentId(UUID.randomUUID().toString());
        incident.setDescription(request.getDescription());
        incident.setSeverity(request.getSeverity());
        incident.setCategory(request.getCategory());
        incident.setReportedBy(request.getReportedBy());
        incident.setCreatedAt(Instant.now().toString());

        repository.save(incident);
        return incident;
    }

    /**
     * Gets the Incident associated with provided id as a string
     *
     * @param id the id
     * 
     * @return the Incident
     */
    public Incident getIncident(String id) {
        return repository.findById(id);
    }

    /**
     * Query incidents by severity OR category.
     */
    public List<Incident> searchIncidents(String severity, String category) {

        if (severity != null) {
            return repository.findBySeverity(severity);
        }

        if (category != null) {
            return repository.findByCategory(category);
        }

        throw new IllegalArgumentException("Either severity or category must be provided");
    }
}