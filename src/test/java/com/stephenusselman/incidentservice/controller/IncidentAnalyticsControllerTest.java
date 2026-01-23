package com.stephenusselman.incidentservice.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import com.stephenusselman.incidentservice.dto.CategoryCountResponse;
import com.stephenusselman.incidentservice.dto.SeverityCountResponse;
import com.stephenusselman.incidentservice.dto.TimeBucketCountResponse;
import com.stephenusselman.incidentservice.service.IncidentAnalyticsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for {@link IncidentAnalyticsController}.
 *
 * <p>
 * Uses {@link MockMvc} to simulate HTTP requests and verifies
 * that the controller endpoints return expected status and data.
 * </p>
 */
class IncidentAnalyticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IncidentAnalyticsService service;

    private IncidentAnalyticsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new IncidentAnalyticsController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetSeverityCountsEndpoint() throws Exception {
        SeverityCountResponse mockResponse = new SeverityCountResponse(
            Map.of("LOW", 5L, "HIGH", 2L)
        );

        when(service.getSeverityCounts()).thenReturn(mockResponse);

        mockMvc.perform(get("api/incidents/analytics/severity-count")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.counts.LOW").value(5))
            .andExpect(jsonPath("$.counts.HIGH").value(2));
    }

    @Test
    void testGetTopCategoriesEndpoint() throws Exception {
        List<CategoryCountResponse> mockCategories = List.of(
            new CategoryCountResponse("Network", 3L),
            new CategoryCountResponse("Database", 1L)
        );

        when(service.getTopCategories(2)).thenReturn(mockCategories);

        mockMvc.perform(get("api/incidents/analytics/top-categories")
                .param("limit", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].category").value("Network"))
            .andExpect(jsonPath("$[0].count").value(3))
            .andExpect(jsonPath("$[1].category").value("Database"))
            .andExpect(jsonPath("$[1].count").value(1));
    }

    @Test
    void testGetIncidentsOverTimeEndpoint() throws Exception {
        Instant t1 = Instant.parse("2026-01-01T00:00:00Z");
        Instant t2 = Instant.parse("2026-01-02T00:00:00Z");

        List<TimeBucketCountResponse> mockBuckets = List.of(
            new TimeBucketCountResponse(t1.toString(), 2L),
            new TimeBucketCountResponse(t2.toString(), 1L)
        );

        when(service.getIncidentsOverTime(t1, t2, ChronoUnit.DAYS)).thenReturn(mockBuckets);

        mockMvc.perform(get("api/incidents/analytics/over-time")
                .param("from", t1.toString())
                .param("to", t2.toString())
                .param("interval", "DAY")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].bucket").value(t1.toString()))
            .andExpect(jsonPath("$[0].count").value(2))
            .andExpect(jsonPath("$[1].bucket").value(t2.toString()))
            .andExpect(jsonPath("$[1].count").value(1));
    }
}