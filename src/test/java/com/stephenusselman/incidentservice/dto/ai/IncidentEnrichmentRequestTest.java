package com.stephenusselman.incidentservice.dto.ai;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IncidentEnrichmentRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    /**
    * Ensures a fully populated enrichment request passes validation
    * when all required fields are present.
    */
    @Test
    void validRequestPassesValidation() {
        IncidentEnrichmentRequest request = new IncidentEnrichmentRequest(
            "INC-123",
            "System outage affecting multiple services",
            "monitoring-service",
            "2024-01-01T12:00:00Z"
        );

        Set<ConstraintViolation<IncidentEnrichmentRequest>> violations =
            validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    /**
    * Ensures validation fails when the incidentId is missing,
    * enforcing the @NotBlank constraint.
    */
    @Test
    void missingIncidentIdFailsValidation() {
        IncidentEnrichmentRequest request = new IncidentEnrichmentRequest(
            null,
            "Description",
            "user",
            "2024-01-01T12:00:00Z"
        );

        Set<ConstraintViolation<IncidentEnrichmentRequest>> violations =
            validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("incidentId"))
        );
    }

    /**
    * Ensures validation fails when the description is blank,
    * enforcing the @NotBlank constraint.
    */
    @Test
    void blankDescriptionFailsValidation() {
        IncidentEnrichmentRequest request = new IncidentEnrichmentRequest(
            "INC-123",
            " ",
            "user",
            "2024-01-01T12:00:00Z"
        );

        Set<ConstraintViolation<IncidentEnrichmentRequest>> violations =
            validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"))
        );
    }

    /**
    * Ensures validation fails when the reportedBy field is missing.
    */
    @Test
    void missingReportedByFailsValidation() {
        IncidentEnrichmentRequest request = new IncidentEnrichmentRequest(
            "INC-123",
            "Valid description",
            null,
            "2024-01-01T12:00:00Z"
        );

        Set<ConstraintViolation<IncidentEnrichmentRequest>> violations =
            validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("reportedBy"))
        );
    }

    /**
    * Ensures validation fails when the createdAt timestamp is missing,
    * enforcing the @NotNull constraint.
    */
    @Test
    void missingCreatedAtFailsValidation() {
        IncidentEnrichmentRequest request = new IncidentEnrichmentRequest(
            "INC-123",
            "Valid description",
            "user",
            null
        );

        Set<ConstraintViolation<IncidentEnrichmentRequest>> violations =
            validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("createdAt"))
        );
    }
}