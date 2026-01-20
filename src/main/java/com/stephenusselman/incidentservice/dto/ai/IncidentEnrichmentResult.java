package com.stephenusselman.incidentservice.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents the result of AI-driven incident enrichment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentEnrichmentResult {

    @NotBlank
    private String severity;

    @NotBlank
    private String category;

    @NotBlank
    private String summary;

    @NotBlank
    private String recommendedAction;
}