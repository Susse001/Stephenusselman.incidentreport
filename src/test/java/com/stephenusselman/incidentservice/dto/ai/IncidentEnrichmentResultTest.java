package com.stephenusselman.incidentservice.dto.ai;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IncidentEnrichmentResultTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
    * Ensures a fully populated enrichment result passes validation
    * when all required AI-generated fields are present.
    */
    @Test
    void validResultPassesValidation() {
        IncidentEnrichmentResult result = new IncidentEnrichmentResult(
            "HIGH",
            "INFRASTRUCTURE",
            "Critical outage affecting customer access",
            "Restart affected services and monitor"
        );

        Set<ConstraintViolation<IncidentEnrichmentResult>> violations =
            validator.validate(result);

        assertTrue(violations.isEmpty());
    }

    /**
    * Ensures validation fails when the severity field is missing.
    */
    @Test
    void missingSeverityFailsValidation() {
        IncidentEnrichmentResult result = new IncidentEnrichmentResult(
            null,
            "INFRASTRUCTURE",
            "Summary",
            "Action"
        );

        Set<ConstraintViolation<IncidentEnrichmentResult>> violations =
            validator.validate(result);

        assertFalse(violations.isEmpty());
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("severity"))
        );
    }

    /**
    * Ensures validation fails when the category field is blank.
    */
    @Test
    void blankCategoryFailsValidation() {
        IncidentEnrichmentResult result = new IncidentEnrichmentResult(
            "HIGH",
            " ",
            "Summary",
            "Action"
        );

        Set<ConstraintViolation<IncidentEnrichmentResult>> violations =
            validator.validate(result);

        assertFalse(violations.isEmpty());
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("category"))
        );
    }

    /**
    * Ensures validation fails when the summary field is missing.
    */
    @Test
    void missingSummaryFailsValidation() {
        IncidentEnrichmentResult result = new IncidentEnrichmentResult(
            "HIGH",
            "SECURITY",
            null,
            "Action"
        );

        Set<ConstraintViolation<IncidentEnrichmentResult>> violations =
            validator.validate(result);

        assertFalse(violations.isEmpty());
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("summary"))
        );
    }

    /**
    * Ensures validation fails when the recommendedAction field is missing.
    */
    @Test
    void missingRecommendedActionFailsValidation() {
        IncidentEnrichmentResult result = new IncidentEnrichmentResult(
            "HIGH",
            "SECURITY",
            "Summary",
            null
        );

        Set<ConstraintViolation<IncidentEnrichmentResult>> violations =
            validator.validate(result);

        assertFalse(violations.isEmpty());
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("recommendedAction"))
        );
    }
}