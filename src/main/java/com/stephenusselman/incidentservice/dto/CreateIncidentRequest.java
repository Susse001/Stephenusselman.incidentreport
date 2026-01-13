package com.stephenusselman.incidentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a request to create a new incident.
 * Severity and category are optional, AI may enrich them if not provided.
 */
@Data
@NoArgsConstructor
public class CreateIncidentRequest {
    
    /** Short description of the incident */
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    /** Optional: Severity Level of the incident */
    private String severity;

    /** Optional: Category of the incident */
    private String category;

    /** Name or identifier of the reporter submitting the incident */
    @NotBlank(message = "ReportedBy is required")
    private String reportedBy;
}
