package com.stephenusselman.incidentservice.service.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AiEnrichmentServiceTest {

    private AiEnrichmentService aiEnrichmentService;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        aiEnrichmentService = new AiEnrichmentService(null, mapper); // openAIClient null because we won't call it
    }

    /**
     * Verifies that the prompt template is loaded from classpath and is not empty.
     */
    @Test
    public void loadPromptTemplate_shouldLoadNonEmptyString() throws Exception {
        Method loadPrompt = AiEnrichmentService.class.getDeclaredMethod("loadPromptTemplate");
        loadPrompt.setAccessible(true);

        String prompt = (String) loadPrompt.invoke(aiEnrichmentService);
        assertThat(prompt).isNotNull().isNotEmpty();
    }

    /**
     * Ensures that the buildPrompt method correctly interpolates all fields
     * of an IncidentEnrichmentRequest into the prompt template.
     */
    @Test
    public void buildPrompt_shouldReplacePlaceholders() throws Exception {
        IncidentEnrichmentRequest request = new IncidentEnrichmentRequest(
                "123", "Test incident", "Tester", "2026-01-19T18:00:00Z"
        );

        Method buildPrompt = AiEnrichmentService.class.getDeclaredMethod("buildPrompt", IncidentEnrichmentRequest.class);
        buildPrompt.setAccessible(true);

        String prompt = (String) buildPrompt.invoke(aiEnrichmentService, request);
        assertThat(prompt).contains("123", "Test incident", "Tester", "2026-01-19T18:00:00Z");
    }

    /**
     * Verifies that parseJsonResult correctly parses a well-formed JSON string
     * into an IncidentEnrichmentResult object.
     */
    @Test
    public void parseJsonResult_shouldParseValidJson() throws Exception {
        String json = """
                {
                  "severity": "HIGH",
                  "category": "PERFORMANCE",
                  "summary": "Test summary",
                  "recommendedAction": "Take action"
                }
                """;

        Method parseJsonResult = AiEnrichmentService.class.getDeclaredMethod("parseJsonResult", String.class);
        parseJsonResult.setAccessible(true);

        Object result = parseJsonResult.invoke(aiEnrichmentService, json);

        assertThat(result).isNotNull();
        assertThat(result).hasFieldOrPropertyWithValue("severity", "HIGH");
        assertThat(result).hasFieldOrPropertyWithValue("category", "PERFORMANCE");
        assertThat(result).hasFieldOrPropertyWithValue("summary", "Test summary");
        assertThat(result).hasFieldOrPropertyWithValue("recommendedAction", "Take action");
    }

    /**
     * Verifies that parseJsonResult throws an exception on invalid JSON input.
     */
    @Test
    public void parseJsonResult_shouldThrowOnInvalidJson() throws Exception {
        String invalidJson = "{ invalid json }";

        Method parseJsonResult = AiEnrichmentService.class.getDeclaredMethod("parseJsonResult", String.class);
        parseJsonResult.setAccessible(true);

        InvocationTargetException exception = assertThrows(
            InvocationTargetException.class,
            () -> parseJsonResult.invoke(aiEnrichmentService, invalidJson)
    );
        assertThat(exception.getCause()).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to parse AI enrichment response");
        }
}
