package com.stephenusselman.incidentservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * HealthController provides a simple endpoint to verify that the
 * Incident Service is running correctly.
 */
@RestController
@RequestMapping("/api")
public class HealthController {
    
    /**
     * GET endpoint to check if the service is alive.
     *
     * @return a simple message confirming service availability
     */
    @GetMapping("/health")
    public String health() {
        return "Incident Service is running";
    }

}
