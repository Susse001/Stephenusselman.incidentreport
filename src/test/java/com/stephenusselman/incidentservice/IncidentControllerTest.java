package com.stephenusselman.incidentservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.Instant;
import java.util.List;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.stephenusselman.incidentservice.controller.IncidentController;
import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.CreateIncidentRequest;
import com.stephenusselman.incidentservice.dto.IncidentResponse;
import com.stephenusselman.incidentservice.dto.PagedIncidentResponse;
import com.stephenusselman.incidentservice.service.IncidentService;
import com.stephenusselman.incidentservice.service.ai.IncidentEnrichmentCoordinator;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(IncidentController.class)
public class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IncidentService incidentService;

    @MockitoBean
    private IncidentEnrichmentCoordinator enrichmentCoordinator;

    /**
     * Ensures that a POST request missing the description field
     * returns a 400 Bad Request with the correct validation error.
     */
    @Test
    void whenMissingDescription_thenReturns400() throws Exception {
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setSeverity("HIGH");
        request.setCategory("NETWORK");
        request.setReportedBy("User123");

        mockMvc.perform(post("/api/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value("Description is required"));
    }

    /**
     * Ensures that a valid POST request successfully creates an incident
     * and returns the expected IncidentResponse with 200 OK.
     */
    @Test
    void whenValidRequest_thenReturns200() throws Exception {
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setDescription("Test incident");
        request.setReportedBy("User123");

        Incident incident = new Incident();
        incident.setIncidentId("123"); 
        incident.setDescription(request.getDescription());
        incident.setReportedBy(request.getReportedBy());
        incident.setCreatedAt(Instant.now().toString());
        incident.setAiStatus("ENRICHED");
        incident.setSeverity("HIGH");
        incident.setCategory("NETWORK");
        incident.setAiSummary("Mock summary");
        incident.setRecommendedAction("Take action");

        Mockito.when(incidentService.createIncident(Mockito.any(CreateIncidentRequest.class)))
           .thenReturn(incident);

        Mockito.doNothing().when(enrichmentCoordinator).enrichIncident(Mockito.any(Incident.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/incidents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.incidentId").value("123"))
            .andExpect(jsonPath("$.description").value("Test incident"))
            .andExpect(jsonPath("$.aiStatus").value("ENRICHED"))
            .andExpect(jsonPath("$.severity").value("HIGH"))
            .andExpect(jsonPath("$.category").value("NETWORK"));
    }


    /**
    * Ensures retrieving an existing incident by ID returns
    * HTTP 200 OK and the expected incident data.
    */
    @Test
    void whenGetIncidentExists_thenReturnsIncident() throws Exception {
        Incident incident = new Incident();
        incident.setIncidentId("123");
        incident.setDescription("Test incident");
        incident.setReportedBy("user");
        incident.setCreatedAt(Instant.now().toString());
        incident.setAiStatus("ENRICHED");

        when(incidentService.getIncident("123")).thenReturn(incident);

        mockMvc.perform(get("/api/incidents/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incidentId").value("123"))
                .andExpect(jsonPath("$.description").value("Test incident"))
                .andExpect(jsonPath("$.aiStatus").value("ENRICHED"));
        }

     /**
     * Ensures that fetching a non-existent incident returns 404 NOT FOUND.
     */
    @Test
    void whenGetIncidentNotFound_thenReturns404() throws Exception {
        String incidentId = "nonexistent";

        // Mock service to return null
        when(incidentService.getIncident(incidentId)).thenReturn(null);

        mockMvc.perform(get("/api/incidents/{id}", incidentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof ResponseStatusException);
                    assertEquals("Incident not found",
                            ((ResponseStatusException) result.getResolvedException()).getReason());
                });
    }

    /**
     * Test fetching the first page of incidents filtered by severity.
     */
    @Test
    void whenQueryBySeverity_thenReturnsPagedResults() throws Exception {
        Incident incident1 = new Incident();
        incident1.setIncidentId("1");
        incident1.setDescription("Network outage");
        incident1.setSeverity("HIGH");
        incident1.setCategory("NETWORK");
        incident1.setCreatedAt(Instant.now().toString());

        Incident incident2 = new Incident();
        incident2.setIncidentId("2");
        incident2.setDescription("Database issue");
        incident2.setSeverity("HIGH");
        incident2.setCategory("NETWORK");
        incident2.setCreatedAt(Instant.now().toString());

        IncidentResponse response1 = IncidentResponse.builder()
                .incidentId(incident1.getIncidentId())
                .description(incident1.getDescription())
                .createdAt(incident1.getCreatedAt())
                .aiStatus(incident1.getAiStatus())
                .severity(incident1.getSeverity())
                .category(incident1.getCategory())
                .aiSummary(incident1.getAiSummary())
                .recommendedAction(incident1.getRecommendedAction())
                .build();

        IncidentResponse response2 = IncidentResponse.builder()
                .incidentId(incident2.getIncidentId())
                .description(incident2.getDescription())
                .createdAt(incident2.getCreatedAt())
                .aiStatus(incident2.getAiStatus())
                .severity(incident2.getSeverity())
                .category(incident2.getCategory())
                .aiSummary(incident2.getAiSummary())
                .recommendedAction(incident2.getRecommendedAction())
                .build();

        PagedIncidentResponse pagedResponse = PagedIncidentResponse.builder()
                .items(List.of(response1, response2))
                .lastKey("nextKey123")
                .build();


        when(incidentService.searchIncidents(Mockito.eq("HIGH"), Mockito.isNull(), Mockito.eq(10), Mockito.isNull()))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/incidents")
                        .param("severity", "HIGH")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].incidentId").value("1"))
                .andExpect(jsonPath("$.items[0].description").value("Network outage"))
                .andExpect(jsonPath("$.items[1].incidentId").value("2"))
                .andExpect(jsonPath("$.items[1].description").value("Database issue"))
                .andExpect(jsonPath("$.lastKey").value("nextKey123"));
    }

    /**
     * Ensures that calling search without any filters returns 400 BAD REQUEST.
     */
    @Test
    void whenNoFiltersProvided_thenReturns400() throws Exception {

        mockMvc.perform(get("/api/incidents")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof ResponseStatusException);
                    assertEquals("Either severity or category must be provided",
                            ((ResponseStatusException) result.getResolvedException()).getReason());
                });
    }
}