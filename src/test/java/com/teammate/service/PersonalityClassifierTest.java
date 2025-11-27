package com.teammate.service;

import com.teammate.model.PersonalityType;
import com.teammate.exception.InvalidPersonalityScoreException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PersonalityClassifier service
 */
class PersonalityClassifierTest {

    @Test
    @DisplayName("Test classify Leader personality type - high score")
    void testClassifyLeader() {
        PersonalityType type = PersonalityClassifier.classifyPersonality(95);
        assertEquals(PersonalityType.LEADER, type);
    }

    @Test
    @DisplayName("Test classify Leader personality type - boundary minimum")
    void testClassifyLeaderBoundaryMin() {
        PersonalityType type = PersonalityClassifier.classifyPersonality(90);
        assertEquals(PersonalityType.LEADER, type);
    }

    @Test
    @DisplayName("Test classify Leader personality type - boundary maximum")
    void testClassifyLeaderBoundaryMax() {
        PersonalityType type = PersonalityClassifier.classifyPersonality(100);
        assertEquals(PersonalityType.LEADER, type);
    }

    @Test
    @DisplayName("Test classify Balanced personality type - mid score")
    void testClassifyBalanced() {
        PersonalityType type = PersonalityClassifier.classifyPersonality(80);
        assertEquals(PersonalityType.BALANCED, type);
    }

    @Test
    @DisplayName("Test classify Balanced personality type - boundary minimum")
    void testClassifyBalancedBoundaryMin() {
        PersonalityType type = PersonalityClassifier.classifyPersonality(70);
        assertEquals(PersonalityType.BALANCED, type);
    }

    @Test
    @DisplayName("Test classify Balanced personality type - boundary maximum")
    void testClassifyBalancedBoundaryMax() {
        PersonalityType type = PersonalityClassifier.classifyPersonality(89);
        assertEquals(PersonalityType.BALANCED, type);
    }

    @Test
    @DisplayName("Test classify Thinker personality type - low score")
    void testClassifyThinker() {
        PersonalityType type = PersonalityClassifier.classifyPersonality(60);
        assertEquals(PersonalityType.THINKER, type);
    }

    @Test
    @DisplayName("Test classify Thinker personality type - boundary minimum")
    void testClassifyThinkerBoundaryMin() {
        PersonalityType type = PersonalityClassifier.classifyPersonality(50);
        assertEquals(PersonalityType.THINKER, type);
    }

    @Test
    @DisplayName("Test classify Thinker personality type - boundary maximum")
    void testClassifyThinkerBoundaryMax() {
        PersonalityType type = PersonalityClassifier.classifyPersonality(69);
        assertEquals(PersonalityType.THINKER, type);
    }

    @Test
    @DisplayName("Test classify with score below minimum throws exception")
    void testClassifyBelowMinimum() {
        assertThrows(InvalidPersonalityScoreException.class, () -> {
            PersonalityClassifier.classifyPersonality(-1);
        });
    }

    @Test
    @DisplayName("Test classify with score above maximum throws exception")
    void testClassifyAboveMaximum() {
        assertThrows(InvalidPersonalityScoreException.class, () -> {
            PersonalityClassifier.classifyPersonality(101);
        });
    }

    @Test
    @DisplayName("Test classify with very negative score throws exception")
    void testClassifyVeryNegative() {
        assertThrows(InvalidPersonalityScoreException.class, () -> {
            PersonalityClassifier.classifyPersonality(-100);
        });
    }

    @Test
    @DisplayName("Test classify with score below valid range throws exception")
    void testClassifyBelowValidRange() {
        assertThrows(InvalidPersonalityScoreException.class, () -> {
            PersonalityClassifier.classifyPersonality(49);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100})
    @DisplayName("Test all Leader range scores")
    void testAllLeaderScores(int score) {
        PersonalityType type = PersonalityClassifier.classifyPersonality(score);
        assertEquals(PersonalityType.LEADER, type);
    }

    @ParameterizedTest
    @ValueSource(ints = {70, 75, 80, 85, 89})
    @DisplayName("Test all Balanced range scores")
    void testAllBalancedScores(int score) {
        PersonalityType type = PersonalityClassifier.classifyPersonality(score);
        assertEquals(PersonalityType.BALANCED, type);
    }

    @ParameterizedTest
    @ValueSource(ints = {50, 55, 60, 65, 69})
    @DisplayName("Test all Thinker range scores")
    void testAllThinkerScores(int score) {
        PersonalityType type = PersonalityClassifier.classifyPersonality(score);
        assertEquals(PersonalityType.THINKER, type);
    }

    @Test
    @DisplayName("Test isValidScore returns true for valid scores")
    void testIsValidScoreTrue() {
        assertTrue(PersonalityClassifier.isValidScore(50));
        assertTrue(PersonalityClassifier.isValidScore(75));
        assertTrue(PersonalityClassifier.isValidScore(100));
    }

    @Test
    @DisplayName("Test isValidScore returns false for invalid scores")
    void testIsValidScoreFalse() {
        assertFalse(PersonalityClassifier.isValidScore(-1));
        assertFalse(PersonalityClassifier.isValidScore(49));
        assertFalse(PersonalityClassifier.isValidScore(101));
        assertFalse(PersonalityClassifier.isValidScore(200));
    }

    @Test
    @DisplayName("Test getPersonalityDescription for Leader")
    void testGetPersonalityDescriptionLeader() {
        String description = PersonalityClassifier.getPersonalityDescription(95);
        assertNotNull(description);
        assertTrue(description.length() > 0);
    }

    @Test
    @DisplayName("Test getPersonalityDescription for Balanced")
    void testGetPersonalityDescriptionBalanced() {
        String description = PersonalityClassifier.getPersonalityDescription(80);
        assertNotNull(description);
        assertTrue(description.length() > 0);
    }

    @Test
    @DisplayName("Test getPersonalityDescription for Thinker")
    void testGetPersonalityDescriptionThinker() {
        String description = PersonalityClassifier.getPersonalityDescription(60);
        assertNotNull(description);
        assertTrue(description.length() > 0);
    }

    @Test
    @DisplayName("Test getPersonalityDescription for invalid score")
    void testGetPersonalityDescriptionInvalid() {
        String description = PersonalityClassifier.getPersonalityDescription(-10);
        assertNotNull(description);
        assertTrue(description.contains("Invalid") || description.contains("unable"));
    }

    @Test
    @DisplayName("Test exception message contains score information")
    void testExceptionMessageContainsScore() {
        Exception exception = assertThrows(InvalidPersonalityScoreException.class, () -> {
            PersonalityClassifier.classifyPersonality(150);
        });

        String message = exception.getMessage();
        assertTrue(message.contains("150"));
    }

    @Test
    @DisplayName("Test boundary between Thinker and Balanced")
    void testBoundaryThinkerToBalanced() {
        assertEquals(PersonalityType.THINKER, PersonalityClassifier.classifyPersonality(69));
        assertEquals(PersonalityType.BALANCED, PersonalityClassifier.classifyPersonality(70));
    }

    @Test
    @DisplayName("Test boundary between Balanced and Leader")
    void testBoundaryBalancedToLeader() {
        assertEquals(PersonalityType.BALANCED, PersonalityClassifier.classifyPersonality(89));
        assertEquals(PersonalityType.LEADER, PersonalityClassifier.classifyPersonality(90));
    }
}