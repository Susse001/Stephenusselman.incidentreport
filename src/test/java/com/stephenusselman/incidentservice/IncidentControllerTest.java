package com.stephenusselman.incidentservice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stephenusselman.incidentservice.controller.IncidentController;
import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.CreateIncidentRequest;
import com.stephenusselman.incidentservice.service.IncidentService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(IncidentController.class)
public class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IncidentService incidentService;

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

    @Test
    void whenValidRequest_thenReturns200() throws Exception {
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setDescription("Test incident");
        request.setSeverity("HIGH");
        request.setCategory("NETWORK");
        request.setReportedBy("User123");

        Incident incident = new Incident();
        incident.setIncidentId("123"); 
        incident.setDescription(request.getDescription());
        incident.setSeverity(request.getSeverity());
        incident.setCategory(request.getCategory());
        incident.setReportedBy(request.getReportedBy());
        incident.setCreatedAt(Instant.now().toString());

        Mockito.when(incidentService.createIncident(Mockito.any(CreateIncidentRequest.class)))
           .thenReturn(incident);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/incidents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request)))
            .andExpect(status().isOk())   // 200
            .andExpect(jsonPath("$.incidentId").value("123"))
            .andExpect(jsonPath("$.description").value("Test incident"));
}
}