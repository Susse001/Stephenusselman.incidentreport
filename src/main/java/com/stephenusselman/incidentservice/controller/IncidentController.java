package com.stephenusselman.incidentservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.CreateIncidentRequest;
import com.stephenusselman.incidentservice.dto.IncidentResponse;
import com.stephenusselman.incidentservice.dto.PagedIncidentResponse;
import com.stephenusselman.incidentservice.service.IncidentService;
import com.stephenusselman.incidentservice.service.ai.IncidentEnrichmentCoordinator;

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

    /** Service layer for handling AI-enrichment incident operations */
    private final IncidentEnrichmentCoordinator enrichmentCoordinator;

    /**
     * Create a new incident abd trigger AI enrichment
     * 
     * @param request incident creation request
     * @return an {@link IncidentResponse} containing ID, AI fields, and AI status
     */
    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident( @Valid @RequestBody CreateIncidentRequest request) {

        Incident incident = incidentService.createIncident(request);

        enrichmentCoordinator.enrichIncident(incident);

        IncidentResponse response = IncidentResponse.builder()
                .incidentId(incident.getIncidentId())
                .description(incident.getDescription())
                .createdAt(incident.getCreatedAt())
                .aiStatus(incident.getAiStatus())
                .severity(incident.getSeverity())
                .category(incident.getCategory())
                .aiSummary(incident.getAiSummary())
                .recommendedAction(incident.getRecommendedAction())
                .aiErrorMessage(incident.getAiErrorMessage())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve a single incident by its unique ID.
     *
     * @param id the unique identifier of the incident
     * @return an {@link IncidentResponse} containing the incident's details
     * @throws org.springframework.web.server.ResponseStatusException
     *         with {@code HttpStatus.NOT_FOUND} if the incident does not exist
     */
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
            .aiStatus(incident.getAiStatus())
            .severity(incident.getSeverity())
            .category(incident.getCategory())
            .aiSummary(incident.getAiSummary())
            .recommendedAction(incident.getRecommendedAction())
            .aiErrorMessage(incident.getAiErrorMessage())
            .build();
    }

    /**
     * Search incidents by severity or category with pagination support.
  
     * Results are returned in pages. If more results are available, a
     * {@code lastKey} value is included in the response and can be supplied
     * in a subsequent request to retrieve the next page.
     *
     * Examples:
     * GET /api/incidents?category=NETWORK&limit=20
     * GET /api/incidents?severity=LOW&lastKey=eyJpbmNpZGVudElkIjoiMTIzIn0=
     *
     * @param severity optional severity filter (mutually exclusive with category)
     * @param category optional category filter (mutually exclusive with severity)
     * @param limit maximum number of incidents to return (default is 10)
     * @param lastKey optional pagination token from a previous response
     * @return a paged response containing matching incidents and an optional pagination key
     * @throws org.springframework.web.server.ResponseStatusException
     *         if zero or more than one filter is provided
     */
    @GetMapping
    public PagedIncidentResponse searchIncidents(
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String lastKey) {

        if (severity != null && category != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only one filter may be specified at a time"
            );
        }

        if (severity == null && category == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Either severity or category must be provided"
            );
        }

       PagedIncidentResponse response = incidentService.searchIncidents(severity, category, limit, lastKey);

        return response;
    }
}
