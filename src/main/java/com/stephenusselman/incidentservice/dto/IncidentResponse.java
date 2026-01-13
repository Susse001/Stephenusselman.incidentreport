package com.stephenusselman.incidentservice.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object representing the response after an incident is created.
 */
@Data
@Builder
public class IncidentResponse {
    
    /** Unique identifier for the incident */
    private String incidentId;

    /** Description of the incident */
    private String description;

    /** Name of the user who reported the incident */
    private String reportedBy;

    /** Timestamp when the incident was created */
    private String createdAt;

    /** Current AI enrichment status: PENDING, ENRICHED, FAILED */
    private String aiStatus;

    /** Severity of the incident, possibly filled by AI */
    private String severity;

    /** Category of the incident, possibly filled by AI */
    private String category;

    /** AI-generated summary of the incident */
    private String aiSummary;

    /** AI-recommended remediation */
    private String recommendedAction;

    /** Error message if AI enrichment failed */
    private String aiErrorMessage;
}