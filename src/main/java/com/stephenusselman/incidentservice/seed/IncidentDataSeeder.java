package com.stephenusselman.incidentservice.seed;

import java.util.List;
import java.util.Random;

import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

import com.stephenusselman.incidentservice.dto.CreateIncidentRequest;
import com.stephenusselman.incidentservice.service.IncidentService;

import lombok.RequiredArgsConstructor;

@Profile("seed")
@Component
@RequiredArgsConstructor
public class IncidentDataSeeder {

    private final IncidentService incidentService;
    private final Random random = new Random();

    private static final int INCIDENT_COUNT = 10;

    private static final List<String> BASE_DESCRIPTIONS = List.of(
        "Multiple users report inability to access the internal dashboard. Requests are timing out intermittently.",
        "Database latency has increased significantly following the latest deployment. Queries exceeding SLA.",
        "Unauthorized login attempts detected from multiple IP addresses within a short time window.",
        "Primary network link is down, causing loss of connectivity to several downstream services.",
        "Disk usage on production server has reached critical levels, impacting application stability.",
        "API responses are returning 500 errors when processing large payloads.",
        "SSL certificate is nearing expiration, causing warnings in client applications.",
        "Scheduled batch job failed due to insufficient memory allocation."
    );

    /**
     * Seed incidents after the application is fully initialized.
     * This guarantees:
     * - DynamoDB tables exist
     * - Repositories are wired
     * - Async + AI enrichment pipelines are available
     */
    @EventListener(ApplicationReadyEvent.class)
    public void seedIncidents() {

        for (int i = 0; i < INCIDENT_COUNT; i++) {
            CreateIncidentRequest request = new CreateIncidentRequest();
            request.setDescription(generateDescription(i));
            request.setReportedBy("seed-bot");

            incidentService.createIncident(request);
        }
    }

    private String generateDescription(int index) {
        String base = BASE_DESCRIPTIONS.get(index % BASE_DESCRIPTIONS.size());

        return base
            + " Observed at "
            + (10 + random.nextInt(14)) + ":"
            + String.format("%02d", random.nextInt(60))
            + " UTC. Impact level reported as "
            + randomImpact()
            + ".";
    }

    private String randomImpact() {
        return switch (random.nextInt(3)) {
            case 0 -> "minor";
            case 1 -> "moderate";
            default -> "severe";
        };
    }

}