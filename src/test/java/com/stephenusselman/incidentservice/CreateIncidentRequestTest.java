package com.stephenusselman.incidentservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.stephenusselman.incidentservice.dto.CreateIncidentRequest;

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

    @Test
    void testGettersAndSetters() {
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setDescription("Test incident");
        request.setSeverity("HIGH");
        request.setCategory("NETWORK");
        request.setReportedBy("User123");

        assertEquals("Test incident", request.getDescription());
        assertEquals("HIGH", request.getSeverity());
        assertEquals("NETWORK", request.getCategory());
        assertEquals("User123", request.getReportedBy());
    }

    @Test
    void testValidRequestPassesValidation() {
    CreateIncidentRequest request = new CreateIncidentRequest();
    request.setDescription("Valid description");
    request.setReportedBy("User123");
    Set<ConstraintViolation<CreateIncidentRequest>> violations = validator.validate(request);
    assertTrue(violations.isEmpty());
}
}