package com.stephenusselman.incidentservice.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Paginated response for incident search results.
 */
@Data
@Builder
public class PagedIncidentResponse {

    /** List of incidents returned for this page */
    private List<IncidentResponse> items;

    /** Cursor for fetching the next page (Base64 encoded) */
    private String lastKey;
}