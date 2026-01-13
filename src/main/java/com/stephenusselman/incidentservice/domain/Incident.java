package com.stephenusselman.incidentservice.domain;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;


/**
 * Domain entity representing an Incident in the Smart Incident Service.
 * This class also maps to the DynamoDB table.
 * Primary key is {@code incidentId}. Secondary indexes are {@code severity} and {@code category}
 */
@DynamoDbBean
public class Incident {

    /** Unique identifier for the incident */
    private String incidentId;

    /** Short description of the incident */
    private String description;

    /** Severity of the incident (GSI) */
    private String severity;

    /** Category of the incident (GSI) */
    private String category;

    /** Name or identifier of the reporter submitting the incident */
    private String reportedBy;

    /** Timestamp when the incident was created */
    private String createdAt;

    /** AI enrichment status for the incident */
    private String aiStatus;

    /** AI-generated summary of the incident */
    private String aiSummary;

    /** AI-recommended remediation action */
    private String recommendedAction;

    /** Error message if AI enrichment fails */
    private String aiErrorMessage;

    /**
     * Gets the unique identifier of the incident.
     * This is the primary partition key for DynamoDB.
     *
     * @return the incident ID
     */
    @DynamoDbPartitionKey
    public String getIncidentId() {
        return incidentId;
    }

    /**
     * Sets the unique identifier of the incident.
     *
     * @param incidentId the incident ID
     */
    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    /**
     * Gets a short description of the incident.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

     /**
     * Sets a short description of the incident.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the severity of the incident.
     * Used as a GSI partition key ("severity-index").
     *
     * @return the severity
     */
    @DynamoDbSecondaryPartitionKey(indexNames = "severity-index")
    public String getSeverity() {
        return severity;
    }

    /**
     * Sets the severity of the incident.
     *
     * @param severity the severity
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * Gets the category of the incident.
     * Used as a GSI partition key ("category-index").
     *
     * @return the category
     */
    @DynamoDbSecondaryPartitionKey(indexNames = "category-index")
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the incident.
     *
     * @param category the category
     */
    public void setCategory(String category) {
        this.category = category;
    }

     /**
     * Gets the user who reported the incident.
     *
     * @return the reporter
     */
    public String getReportedBy() {
        return reportedBy;
    }

    /**
     * Sets the user who reported the incident.
     *
     * @param reportedBy the reporter
     */
    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    /**
     * Gets the creation timestamp of the incident.
     * Used as a GSI sort key for both severity and category indexes.
     *
     * @return the creation timestamp as a String
     */
    @DynamoDbSecondarySortKey(indexNames = {"severity-index", "category-index"})
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the incident.
     *
     * @param createdAt the creation timestamp as a String
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the AI enrichment status of the incident.
     *
     * @return the AI status
     */
    public String getAiStatus() {
        return aiStatus;
    }

    /**
     * Sets the AI enrichment status of the incident.
     *
     * @param aiStatus the AI status
     */
    public void setAiStatus(String aiStatus) {
        this.aiStatus = aiStatus;
    }

    /**
     * Gets the AI-generated summary.
     *
     * @return the AI summary
     */
    public String getAiSummary() {
        return aiSummary;
    }

    /**
     * Sets the AI-generated summary.
     *
     * @param aiSummary the AI summary
     */
    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    /**
     * Gets the AI-recommended remediation action.
     *
     * @return the recommended action
     */
    public String getRecommendedAction() {
        return recommendedAction;
    }

    /**
     * Sets the AI-recommended remediation action.
     *
     * @param recommendedAction the action
     */
    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }

    /**
     * Gets the AI error message if enrichment failed.
     *
     * @return the AI error message
     */
    public String getAiErrorMessage() {
        return aiErrorMessage;
    }

    /**
     * Sets the AI error message.
     *
     * @param aiErrorMessage the error message
     */
    public void setAiErrorMessage(String aiErrorMessage) {
        this.aiErrorMessage = aiErrorMessage;
    }
}
