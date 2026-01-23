package com.stephenusselman.incidentservice.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.CategoryCountResponse;
import com.stephenusselman.incidentservice.dto.SeverityCountResponse;
import com.stephenusselman.incidentservice.dto.TimeBucketCountResponse;
import com.stephenusselman.incidentservice.repository.IncidentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncidentAnalyticsService {

    private final IncidentRepository incidentRepository;

    /**
     * Aggregates incidents by severity.
     *
     * @return a {@link SeverityCountResponse} containing counts per severity
     */
    public SeverityCountResponse getSeverityCounts() {
        List<Incident> incidents = incidentRepository.findAll();

        Map<String, Long> counts =
            incidents.stream()
                .collect(Collectors.groupingBy(
                    Incident::getSeverity,
                    Collectors.counting()
                ));

        return new SeverityCountResponse(counts);
    }

    /**
    * Aggregates incidents over time using the provided time bucket.
    *
    * <p>
    * The {@code createdAt} field is stored as an ISO-8601 string in DynamoDB
    * and is converted to {@link Instant} at runtime for time-based aggregation.
    * </p>
    *
    * @param from the start of the time range (inclusive)
    * @param to the end of the time range (inclusive)
    * @param unit the time unit used for bucketing (e.g. DAYS, WEEKS)
    * @return a chronologically sorted list of time bucket counts
    */
    public List<TimeBucketCountResponse> getIncidentsOverTime(Instant from, Instant to, ChronoUnit unit) {
        List<Incident> incidents =
            incidentRepository.findByCreatedAtBetween(from, to);

        Map<Instant, Long> buckets =
            incidents.stream()
                .map(i -> Instant.parse(i.getCreatedAt())) // explicit conversion
                .collect(Collectors.groupingBy(
                    instant -> instant.truncatedTo(unit),
                    Collectors.counting()
                ));

        return buckets.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> new TimeBucketCountResponse(
                e.getKey().toString(),
                e.getValue()
            ))
            .toList();
    }

    /**
    * Retrieves the most frequently occurring incident categories.
    *
    * @param limit the maximum number of categories to return
    * @return a list of category count results ordered by frequency
    */
    public List<CategoryCountResponse> getTopCategories(int limit) {
        List<Incident> incidents = incidentRepository.findAll();

        return incidents.stream()
            .collect(Collectors.groupingBy(
                Incident::getCategory,
                Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .map(e -> new CategoryCountResponse(e.getKey(), e.getValue()))
            .toList();
    }
}