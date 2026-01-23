package com.stephenusselman.incidentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO representing the number of incidents within a specific time bucket.
 */
@Data
@AllArgsConstructor
public class TimeBucketCountResponse {

    /**
     * The time bucket identifier.
     */
    private String bucket;

    /**
     * The number of incidents that occurred within this time bucket.
     */
    private long count;
}