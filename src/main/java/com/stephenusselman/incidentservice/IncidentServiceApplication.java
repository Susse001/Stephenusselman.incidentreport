package com.stephenusselman.incidentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot application class for the Smart Incident Service.
 */
@SpringBootApplication(scanBasePackages = "com.stephenusselman.incidentservice")
@EnableAsync
public class IncidentServiceApplication {

	/**
     * Main method to start the Spring Boot application.
     *
     * @param args command-line arguments (not used)
     */
	public static void main(String[] args) {
		SpringApplication.run(IncidentServiceApplication.class, args);
	}

}
