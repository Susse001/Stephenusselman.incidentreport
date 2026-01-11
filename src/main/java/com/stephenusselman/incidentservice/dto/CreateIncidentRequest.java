package com.stephenusselman.incidentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a request to create a new incident.
 */
@Data
@NoArgsConstructor
public class CreateIncidentRequest {
    
    /** Short description of the incident */
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    /** Severity Level of the incident */
    @NotBlank(message = "Severity is required")
    private String severity;

    /** Category of the incident */
    @NotBlank(message = "Category is required")
    private String category;

    /** Name or identifier of the reporter submitting the incident */
    @NotBlank(message = "ReportedBy is required")
    private String reportedBy;
}
