package com.stephenusselman.incidentservice.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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