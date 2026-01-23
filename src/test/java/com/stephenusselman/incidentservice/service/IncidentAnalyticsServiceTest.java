package com.stephenusselman.incidentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.CategoryCountResponse;
import com.stephenusselman.incidentservice.dto.SeverityCountResponse;
import com.stephenusselman.incidentservice.dto.TimeBucketCountResponse;
import com.stephenusselman.incidentservice.repository.IncidentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


class IncidentAnalyticsServiceTest {

    @Mock
    private IncidentRepository repository;

    @InjectMocks
    private IncidentAnalyticsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSeverityCounts() {
        Incident low = new Incident();
        low.setSeverity("LOW");

        Incident high = new Incident();
        high.setSeverity("HIGH");

        when(repository.findAll()).thenReturn(Arrays.asList(low, high, low));

        SeverityCountResponse response = service.getSeverityCounts();

        Map<String, Long> counts = response.getCounts();
        assertEquals(2L, counts.get("LOW"));
        assertEquals(1L, counts.get("HIGH"));
    }

    @Test
    void testGetTopCategories() {
        Incident catA = new Incident();
        catA.setCategory("Network");

        Incident catB = new Incident();
        catB.setCategory("Database");

        Incident catC = new Incident();
        catC.setCategory("Network");

        when(repository.findAll()).thenReturn(Arrays.asList(catA, catB, catC));

        List<CategoryCountResponse> topCategories = service.getTopCategories(2);

        assertEquals("Network", topCategories.get(0).getCategory());
        assertEquals(2L, topCategories.get(0).getCount());
        assertEquals("Database", topCategories.get(1).getCategory());
        assertEquals(1L, topCategories.get(1).getCount());
    }

    @Test
    void testGetIncidentsOverTime() {
        Instant t1 = Instant.parse("2026-01-01T10:00:00Z");
        Instant t2 = Instant.parse("2026-01-01T12:00:00Z");
        Instant t3 = Instant.parse("2026-01-02T09:00:00Z");

        Incident i1 = new Incident();
        i1.setCreatedAt(t1.toString());

        Incident i2 = new Incident();
        i2.setCreatedAt(t2.toString());

        Incident i3 = new Incident();
        i3.setCreatedAt(t3.toString());

        when(repository.findByCreatedAtBetween(t1, t3)).thenReturn(Arrays.asList(i1, i2, i3));

        List<TimeBucketCountResponse> buckets = service.getIncidentsOverTime(t1, t3, ChronoUnit.DAYS);

        assertEquals(2, buckets.size());
        assertEquals("2026-01-01T00:00:00Z", buckets.get(0).getBucket());
        assertEquals(2L, buckets.get(0).getCount());
        assertEquals("2026-01-02T00:00:00Z", buckets.get(1).getBucket());
        assertEquals(1L, buckets.get(1).getCount());
    }
}