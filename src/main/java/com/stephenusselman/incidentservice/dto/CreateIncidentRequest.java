package com.stephenusselman.incidentservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a request to create a new incident.
 */
@Data
@NoArgsConstructor
public class CreateIncidentRequest {
    
    /** Short description of the incident */
    @NotBlank
    private String description;

    /** Severity Level of the incident */
    @NotBlank
    private String severity;

    /** Category of the incident */
    @NotBlank
    private String category;

    /** Name or identifier of the reporter submitting the incident */
    private String reportedBy;
}
