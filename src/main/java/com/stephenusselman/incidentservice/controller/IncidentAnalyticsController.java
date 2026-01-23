package com.stephenusselman.incidentservice.controller;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stephenusselman.incidentservice.dto.CategoryCountResponse;
import com.stephenusselman.incidentservice.dto.SeverityCountResponse;
import com.stephenusselman.incidentservice.dto.TimeBucketCountResponse;
import com.stephenusselman.incidentservice.service.IncidentAnalyticsService;

import lombok.RequiredArgsConstructor;

/**
 * REST controller providing analytics endpoints for incident data.
 *
 */
@RestController
@RequestMapping("/api/incidents/analytics")
@RequiredArgsConstructor
@Validated
public class IncidentAnalyticsController {

    private final IncidentAnalyticsService analyticsService;

    /**
     * Retrieves the total count of incidents grouped by severity.
     * 
     * Example Response
     * {
     *   "counts": {
     *     "LOW": 5,
     *     "MEDIUM": 10,
     *     "HIGH": 3
     *   }
     * }
     *
     * @return a {@link SeverityCountResponse} containing counts per severity
     */
    @GetMapping("/severity-count")
    public SeverityCountResponse getSeverityCounts() {
        return analyticsService.getSeverityCounts();
    }

    /**
     * Retrieves the number of incidents aggregated over time within the specified range.
     *
     * Example
     * GET /incidents/analytics/over-time?from=2026-01-01T00:00:00Z
     *     &to=2026-01-15T23:59:59Z&interval=DAY
     * 
     * @param from the start of the date range in ISO-8601 format
     * @param to the end of the date range in ISO-8601 format
     * @param interval the time bucket unit (DAY or WEEK)
     * @return a sorted list of {@link TimeBucketCountResponse} per bucket
     */
    @GetMapping("/over-time")
    public List<TimeBucketCountResponse> getIncidentsOverTime(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String from,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String to,
            @RequestParam(defaultValue = "DAY") String interval
    ) {
        Instant fromInstant;
        Instant toInstant;

        try {
            fromInstant = Instant.parse(from);
            toInstant = Instant.parse(to);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid ISO-8601 date format for 'from' or 'to'", ex);
        }

        if (fromInstant.isAfter(toInstant)) {
            throw new IllegalArgumentException("'from' must be before or equal to 'to'");
        }

        if (Duration.between(fromInstant, toInstant).toDays() > 90) {
            throw new IllegalArgumentException("Date range cannot exceed 90 days");
        }

        ChronoUnit unit;
        switch (interval.toUpperCase()) {
            case "DAY" -> unit = ChronoUnit.DAYS;
            case "WEEK" -> unit = ChronoUnit.WEEKS;
            default -> throw new IllegalArgumentException("Invalid interval. Allowed values: DAY, WEEK");
        }

        return analyticsService.getIncidentsOverTime(fromInstant, toInstant, unit);
    }

    /**
     * Retrieves the top incident categories by count.
     * 
     * Example
     * GET /incidents/analytics/top-categories?limit=5
     *
     * @param limit maximum number of categories to return (default 5, min 1, max 100)
     * @return a list of {@link CategoryCountResponse} sorted by frequency
     */
    @GetMapping("/top-categories")
    public List<CategoryCountResponse> getTopCategories(
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int limit
    ) {
        return analyticsService.getTopCategories(limit);
    }
}