package com.stephenusselman.incidentservice.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateIncidentRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
    * Ensures a fully valid request passes Jakarta Bean Validation
    * when all required fields are present and constraints are met.
    */
    @Test
    void testValidRequestPassesValidation() {
    CreateIncidentRequest request = new CreateIncidentRequest();
    request.setDescription("Valid description");
    request.setReportedBy("User123");
    Set<ConstraintViolation<CreateIncidentRequest>> violations = validator.validate(request);
    assertTrue(violations.isEmpty());
    }

    /**
    * Ensures validation fails when the description field is missing,
    * enforcing the @NotBlank constraint.
    */
    @Test
    void testMissingDescriptionFailsValidation() {
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setReportedBy("User123");

        Set<ConstraintViolation<CreateIncidentRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"))
        );
    }

    /**
    * Ensures validation fails when the description exceeds the
    * maximum allowed length.
    */
    @Test
    void testDescriptionTooLongFailsValidation() {
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setDescription("a".repeat(501));
        request.setReportedBy("User123");

        Set<ConstraintViolation<CreateIncidentRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"))
        );
    }

    /**
    * Ensures validation fails when the reportedBy field is missing,
    * enforcing the @NotBlank constraint.
    */
    @Test
    void testMissingReportedByFailsValidation() {
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setDescription("Valid description");

        Set<ConstraintViolation<CreateIncidentRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("reportedBy"))
        );
    }

    /**
    * Verifies that optional fields (severity and category) may be null
    * without triggering validation errors.
    */
    @Test
    void testOptionalFieldsCanBeNull() {
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setDescription("Valid description");
        request.setReportedBy("User123");

        request.setSeverity(null);
        request.setCategory(null);

        Set<ConstraintViolation<CreateIncidentRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}