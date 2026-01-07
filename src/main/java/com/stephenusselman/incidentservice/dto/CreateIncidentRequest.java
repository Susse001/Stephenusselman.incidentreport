package com.stephenusselman.incidentservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object representing a request to create a new incident.
 */
@Data
public class CreateIncidentRequest {
    
    /** Short description of the incident */
    @NotBlank
    private String description;

    /** Name or identifier of the reporter submitting the incident */
    private String reportedBy;
}
