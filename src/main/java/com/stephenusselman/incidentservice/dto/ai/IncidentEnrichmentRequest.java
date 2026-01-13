package com.stephenusselman.incidentservice.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentEnrichmentRequest {

    @NotBlank
    private String incidentId;

    @NotBlank
    private String description;

    @NotBlank
    private String reportedBy;

    @NotNull
    private String createdAt;
}