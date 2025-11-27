package com.teammate.service;

import com.teammate.model.PersonalityType;
import com.teammate.exception.InvalidPersonalityScoreException;

/**
 * Service class for classifying personality types based on survey scores
 * Demonstrates single responsibility principle
 */
public class PersonalityClassifier {

    /**
     * Classifies a participant's personality type based on their survey score
     * @param score The personality score (0-100)
     * @return The corresponding PersonalityType
     * @throws InvalidPersonalityScoreException if score is out of valid range
     */
    public static PersonalityType classifyPersonality(int score)
            throws InvalidPersonalityScoreException {

        // Validate input
        if (score < 0 || score > 100) {
            throw new InvalidPersonalityScoreException(
                    "Invalid personality score: " + score + ". Must be between 0 and 100"
            );
        }

        // Check against each personality type's range
        for (PersonalityType type : PersonalityType.values()) {
            if (type.matchesScore(score)) {
                return type;
            }
        }

        // Scores 0-49 are not valid based on personality classification
        // This indicates a data quality issue
        throw new InvalidPersonalityScoreException(
                "Score " + score + " falls outside all defined personality ranges (50-100)"
        );
    }

    /**
     * Validates if a personality score is within acceptable range
     * @param score The score to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidScore(int score) {
        return score >= 50 && score <= 100;
    }

    /**
     * Gets personality type description for a given score
     * @param score The personality score
     * @return Description of the personality type
     */
    public static String getPersonalityDescription(int score) {
        try {
            PersonalityType type = classifyPersonality(score);
            return type.getDescription();
        } catch (InvalidPersonalityScoreException e) {
            return "Invalid score - unable to determine personality type";
        }
    }
}