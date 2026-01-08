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

    /** Timestamp when the incident was created */
    private String createdAt;
}
