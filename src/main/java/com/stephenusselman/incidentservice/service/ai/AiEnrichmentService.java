package com.stephenusselman.incidentservice.service.ai;

import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentRequest;
import com.stephenusselman.incidentservice.dto.ai.IncidentEnrichmentResult;

import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

@Service
/**
 * Service interface for enriching incidents using AI.
 * 
 * The AI model is expected to return a JSON object that strictly matches
 * the {@link IncidentEnrichmentResult} schema. Any deviation will result
 * in a failure.
 */
public class AiEnrichmentService {

    /** Classpath location of the enrichment prompt template */
    private static final String PROMPT_PATH = "ai/prompts/incident-enrichment-v1.txt";      

    /** OpenAI client injected by Spring */
    private final OpenAIClient openAIClient;

    /** JSON mapper for parsing AI responses */
    private final ObjectMapper objectMapper;

    /** Cached prompt template loaded at startup */
    private final String promptTemplate;

    /**
     * Constructs the AI enrichment service.
     *
     * @param openAIClient the OpenAI client
     * @param objectMapper Jackson object mapper
     */
    public AiEnrichmentService(OpenAIClient openAIClient, ObjectMapper objectMapper) {
        this.openAIClient = openAIClient;
        this.objectMapper = objectMapper;
        this.promptTemplate = loadPromptTemplate();
    }

   /**
     * Enriches an incident by sending it to the AI model for analysis.
     *
     * @param request the incident enrichment request
     * @return the AI-generated enrichment result
     * @throws IllegalStateException if the AI response cannot be parsed
     */
    public IncidentEnrichmentResult enrichIncident(IncidentEnrichmentRequest request) {

        String prompt = buildPrompt(request);

        Response response = openAIClient.responses().create(
                ResponseCreateParams.builder()
                        .model("gpt-5-nano")
                        .input(prompt)
                        .build()
        );

        String rawJson = extractOutputText(response);

        return parseJsonResult(rawJson);
    }

    /**
     * Builds the final prompt by substituting request values
     * into the loaded prompt template.
     *
     * @param request the enrichment request
     * @return the fully constructed prompt
     */
    private String buildPrompt(IncidentEnrichmentRequest request) {
        return promptTemplate
                .replace("{{incidentId}}", request.getIncidentId())
                .replace("{{description}}", request.getDescription())
                .replace("{{reportedBy}}", request.getReportedBy())
                .replace("{{createdAt}}", request.getCreatedAt());
    }

    /**
     * Parses the AI response JSON into an {@link IncidentEnrichmentResult}.
     *
     * @param json the raw JSON returned by the AI model
     * @return the parsed enrichment result
     * @throws IllegalStateException if parsing fails
     */
    private IncidentEnrichmentResult parseJsonResult(String json) {
        try {
            return objectMapper.readValue(
                    json, IncidentEnrichmentResult.class);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to parse AI enrichment response: " + json, e);
        }
    }

    /**
     * Loads the prompt template from the classpath.
     *
     * @return the prompt template as a string
     * @throws IllegalStateException if the prompt cannot be loaded
     */
    private String loadPromptTemplate() {
        try {
            ClassPathResource resource =
                    new ClassPathResource(PROMPT_PATH);

            return new String(
                    resource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load AI prompt template: " + PROMPT_PATH, e);
        }
    }

    /**
     * Helper function designed to extract the message content from the AI response
     * @param response the AI response from the OpenAI API call
     * @return the message content from the AI response as a JSON
     */
    private String extractOutputText(Response response) {
    return response.output().stream()
            .flatMap(item -> item.message().stream())
            .flatMap(msg -> msg.content().stream())
            .flatMap(content -> content.outputText().stream())
            .map(t -> t.text())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                    "No text output returned from AI response"));
    }
}