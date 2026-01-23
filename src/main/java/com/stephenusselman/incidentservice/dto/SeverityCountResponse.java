package com.stephenusselman.incidentservice.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO representing aggregated incident counts by severity.
 *
 */
@Data
@AllArgsConstructor
public class SeverityCountResponse {

    /**
     * Map of severity values to their corresponding incident counts.
     */
    private Map<String, Long> counts;
}