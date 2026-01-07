package com.stephenusselman.incidentservice.domain;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain entity representing an Incident in the Smart Incident Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Incident {

    /** Unique identifier for the incident */
    private String incidentId;

    /** Short description of the incident */
    private String description;

    /** Name or identifier of the reporter submitting the incident */
    private String reportedBy;

    /** Timestamp when the incident was created */
    private Instant createdAt;
    
}
