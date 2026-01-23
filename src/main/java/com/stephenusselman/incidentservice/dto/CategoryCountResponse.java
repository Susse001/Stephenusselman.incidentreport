package com.stephenusselman.incidentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO representing aggregated incident counts by category.
 */
@Data
@AllArgsConstructor
public class CategoryCountResponse {

    /**
     * The incident category name.
     */
    private String category;

    /**
     * The number of incidents associated with the given category.
     */
    private long count;
}
