package com.stephenusselman.incidentservice.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.stephenusselman.incidentservice.domain.Incident;
import com.stephenusselman.incidentservice.dto.CreateIncidentRequest;
import com.stephenusselman.incidentservice.dto.PagedIncidentResponse;
import com.stephenusselman.incidentservice.repository.IncidentRepository;
import com.stephenusselman.incidentservice.service.IncidentService;
import com.stephenusselman.incidentservice.service.ai.AiEnrichmentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {"spring.profiles.active=local"})
public class IncidentServiceIntegrationTest {

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private IncidentRepository repository;

    @MockitoBean
    private AiEnrichmentService aiEnrichmentService;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    /**
     * Tests that creating an incident via the service persists the entity
     * with initial fields such as aiStatus and reportedBy correctly set.
     */
    @Test
    void createIncident_shouldSaveIncidentWithPendingStatus() {
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setDescription("Database outage affecting multiple services");
        request.setReportedBy("TestUser");

        Incident incident = incidentService.createIncident(request);

        assertThat(incident.getIncidentId()).isNotNull();
        assertThat(incident.getAiStatus()).isEqualTo("PENDING");

        Incident fromDb = repository.findById(incident.getIncidentId());
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getReportedBy()).isEqualTo("TestUser");
        assertThat(fromDb.getAiStatus()).isEqualTo("PENDING");
    }

    /**
     * Tests that creating an incident triggers asynchronous AI enrichment.
     * Ensures the entity eventually gets AI fields populated.
     */
    @Test
    void createIncident_shouldTriggerAiEnrichment() throws Exception {
        Mockito.when(aiEnrichmentService.enrichIncident(Mockito.any()))
               .thenAnswer(invocation -> {
                   var req = invocation.getArgument(0);
                   return new com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentResult(
                       "HIGH", "PERFORMANCE", "summary", "action"
                   );
               });

        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setDescription("Test AI enrichment");
        request.setReportedBy("Tester");

        Incident incident = incidentService.createIncident(request);

        // Wait a short while for async enrichment
        Thread.sleep(500);

        Incident fromDb = repository.findById(incident.getIncidentId());
        assertThat(fromDb.getSeverity()).isEqualTo("HIGH");
        assertThat(fromDb.getCategory()).isEqualTo("PERFORMANCE");
        assertThat(fromDb.getAiStatus()).isEqualTo("ENRICHED");
    }

    /**
     * Tests that querying incidents by severity returns the correct results.
     * Inserts multiple incidents and verifies that the severity GSI returns
     * only matching incidents.
     */
    @Test
    void queryBySeverity_shouldReturnCorrectIncidents() {
        Incident incident1 = createTestIncident("HIGH", "PERFORMANCE");
        Incident incident2 = createTestIncident("LOW", "NETWORK");

        repository.save(incident1);
        repository.save(incident2);

        List<Incident> highSeverity = repository.findBySeverity("HIGH");
        assertThat(highSeverity).hasSize(1);
        assertThat(highSeverity.get(0).getSeverity()).isEqualTo("HIGH");
    }

    /**
     * Tests that querying incidents by category using the repository returns
     * only incidents with the specified category via the category GSI.
     */
    @Test
    public void queryByCategory_shouldReturnCorrectIncidents() {
        Incident cat1 = createTestIncident("HIGH", "SECURITY");
        Incident cat2 = createTestIncident("LOW", "NETWORK");
        repository.save(cat1);
        repository.save(cat2);

        List<Incident> securityIncidents = repository.findByCategory("SECURITY");

        assertThat(securityIncidents).hasSize(1);
        assertThat(securityIncidents.get(0).getCategory()).isEqualTo("SECURITY");
        assertThat(securityIncidents.get(0).getSeverity()).isEqualTo("HIGH");
    }


    /**
     * Tests that searching incidents via the service correctly maps DynamoDB
     * entities to response DTOs and handles pagination.
     */
    @Test
    public void searchIncidents_shouldReturnMappedPagedResults() {
        Incident incident = new Incident();
        incident.setIncidentId(UUID.randomUUID().toString());
        incident.setDescription("Network outage");
        incident.setSeverity("MEDIUM");
        incident.setCategory("NETWORK");
        incident.setReportedBy("seed-bot");
        incident.setCreatedAt(Instant.now().toString());
        incident.setAiStatus("ENRICHED");
        repository.save(incident);

        PagedIncidentResponse response = incidentService.searchIncidents("MEDIUM", null, 10, null);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getSeverity()).isEqualTo("MEDIUM");
        assertThat(response.getItems().get(0).getCategory()).isEqualTo("NETWORK");
        assertThat(response.getItems().get(0).getReportedBy()).isEqualTo("seed-bot");
    }

    /**
     * Creates a sample Incident instance for use in tests.
     * Populates the incident with a unique ID, description, reporter, 
     * timestamp, severity, category, and initial AI status.
     *
     * @param severity the severity level to assign (e.g., HIGH, MEDIUM, LOW)
     * @param category the category to assign (e.g., NETWORK, PERFORMANCE)
     * @return a new Incident object initialized with the given severity and category
     */
    private Incident createTestIncident(String severity, String category) {
        Incident incident = new Incident();
        incident.setIncidentId(UUID.randomUUID().toString());
        incident.setDescription("Test Incident");
        incident.setReportedBy("TestUser");
        incident.setCreatedAt(java.time.Instant.now().toString());
        incident.setSeverity(severity);
        incident.setCategory(category);
        incident.setAiStatus("PENDING");
        return incident;
    }
}