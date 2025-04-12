package it.andrearaponi.gender.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;

/**
 * Response DTO for gender classification results.
 * This class represents the structured output of the gender classification service,
 * containing the detected gender, confidence score, description and validation status.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenderClassificationResponse {

    /**
     * The detected gender code:
     * M = Male
     * F = Female
     * N = Not determined
     */
    @NotNull(message = "Gender is a required field")
    @Pattern(regexp = "^[MFNX]$", message = "Gender must be M, F, N, X")
    @JsonProperty("gender")
    private String gender;

    /**
     * Descriptive text about the classification result.
     * Provides additional human-readable information about the detection.
     */
    @JsonProperty("description")
    private String description;

    /**
     * Confidence score of the classification (0.0 to 1.0).
     * Higher values indicate greater confidence in the classification result.
     */
    @JsonProperty("confidence")
    private Double confidence;

    /**
     * Timestamp of when the classification was performed.
     * Automatically initialized to the current time when the object is created.
     */
    @JsonProperty("timestamp")
    private Instant timestamp = Instant.now();

    /**
     * Flag indicating if the result has been validated through Drools.
     * When true, the classification has passed validation rules.
     */
    @JsonProperty("validated")
    private boolean validated;


    /**
     * Default constructor required for JSON deserialization.
     */
    public GenderClassificationResponse() {
    }

    /**
     * Constructor with main fields.
     *
     * @param gender The detected gender code (M, F, or N)
     * @param description The description text explaining the classification
     */
    public GenderClassificationResponse(String gender, String description) {
        this.gender = gender;
        this.description = description;
    }

    /**
     * Gets the gender classification result.
     *
     * @return The gender code (M, F, or N)
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender classification result.
     *
     * @param gender The gender code to set (M, F, or N)
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets the descriptive text for the classification.
     *
     * @return The description of the classification
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the descriptive text for the classification.
     *
     * @param description The description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the confidence score of the classification.
     *
     * @return The confidence value between 0.0 and 1.0
     */
    public Double getConfidence() {
        return confidence;
    }

    /**
     * Sets the confidence score of the classification.
     *
     * @param confidence The confidence value to set (between 0.0 and 1.0)
     */
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    /**
     * Gets the timestamp when the classification was performed.
     *
     * @return The classification timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp when the classification was performed.
     *
     * @param timestamp The timestamp to set
     */
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Checks if the classification result has been validated.
     *
     * @return True if the result passed validation, false otherwise
     */
    public boolean isValidated() {
        return validated;
    }

    /**
     * Sets whether the classification result has been validated.
     *
     * @param validated The validation status to set
     */
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    /**
     * Returns a string representation of the classification result.
     *
     * @return A string containing all fields of this response
     */
    @Override
    public String toString() {
        return "GenderClassificationResponse{" +
                "gender='" + gender + '\'' +
                ", description='" + description + '\'' +
                ", confidence=" + confidence +
                ", validated=" + validated +
                ", timestamp=" + timestamp +
                '}';
    }
}