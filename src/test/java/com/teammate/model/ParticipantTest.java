package com.teammate.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Participant class
 */
class ParticipantTest {

    @Test
    @DisplayName("Test valid participant creation with score")
    void testValidParticipantCreation() {
        Participant participant = new Participant(
                "P001", "Alice Johnson", "alice@uni.edu", "Valorant",
                "Strategist", 95, 8
        );

        assertNotNull(participant);
        assertEquals("P001", participant.getParticipantId());
        assertEquals("Alice Johnson", participant.getName());
        assertEquals("alice@uni.edu", participant.getEmail());
        assertEquals("Valorant", participant.getPreferredGame());
        assertEquals("Strategist", participant.getPreferredRole());
        assertEquals(95, participant.getPersonalityScore());
        assertEquals(8, participant.getSkillLevel());
        assertEquals(PersonalityType.LEADER, participant.getPersonalityType());
    }

    @Test
    @DisplayName("Test participant creation with survey answers")
    void testParticipantCreationWithSurvey() {
        int[] surveyAnswers = {5, 5, 4, 4, 5}; // Total: 23, Score: 92
        Participant participant = new Participant(
                "P001", "Alice Johnson", "alice@uni.edu", "Valorant",
                "Strategist", surveyAnswers, 8
        );

        assertNotNull(participant);
        assertEquals(92, participant.getPersonalityScore());
        assertEquals(PersonalityType.LEADER, participant.getPersonalityType());
    }

    @Test
    @DisplayName("Test personality score calculation from survey")
    void testPersonalityScoreCalculation() {
        int[] surveyAnswers1 = {5, 5, 5, 5, 5}; // Total: 25, Score: 100
        Participant p1 = new Participant("P001", "Test1", "test1@uni.edu",
                "Valorant", "Strategist", surveyAnswers1, 8);
        assertEquals(100, p1.getPersonalityScore());
        assertEquals(PersonalityType.LEADER, p1.getPersonalityType());

        int[] surveyAnswers2 = {4, 4, 4, 4, 4}; // Total: 20, Score: 80
        Participant p2 = new Participant("P002", "Test2", "test2@uni.edu",
                "FIFA", "Attacker", surveyAnswers2, 7);
        assertEquals(80, p2.getPersonalityScore());
        assertEquals(PersonalityType.BALANCED, p2.getPersonalityType());

        int[] surveyAnswers3 = {3, 3, 3, 3, 3}; // Total: 15, Score: 60
        Participant p3 = new Participant("P003", "Test3", "test3@uni.edu",
                "DOTA 2", "Defender", surveyAnswers3, 6);
        assertEquals(60, p3.getPersonalityScore());
        assertEquals(PersonalityType.THINKER, p3.getPersonalityType());
    }

    @Test
    @DisplayName("Test survey with invalid number of answers")
    void testInvalidSurveyAnswerCount() {
        int[] surveyAnswers = {5, 5, 5}; // Only 3 answers instead of 5
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("P001", "Alice", "alice@uni.edu", "Valorant",
                    "Strategist", surveyAnswers, 8);
        });
    }

    @Test
    @DisplayName("Test survey with out of range answers")
    void testInvalidSurveyAnswerRange() {
        int[] surveyAnswers = {5, 5, 6, 5, 5}; // 6 is out of range
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("P001", "Alice", "alice@uni.edu", "Valorant",
                    "Strategist", surveyAnswers, 8);
        });
    }

    @Test
    @DisplayName("Test participant with null ID throws exception")
    void testNullParticipantId() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant(null, "Alice", "alice@uni.edu", "Valorant", "Strategist", 95, 8);
        });
    }

    @Test
    @DisplayName("Test participant with empty ID throws exception")
    void testEmptyParticipantId() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("", "Alice", "alice@uni.edu", "Valorant", "Strategist", 95, 8);
        });
    }

    @Test
    @DisplayName("Test participant with null name throws exception")
    void testNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("P001", null, "alice@uni.edu", "Valorant", "Strategist", 95, 8);
        });
    }

    @Test
    @DisplayName("Test participant with null email throws exception")
    void testNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("P001", "Alice", null, "Valorant", "Strategist", 95, 8);
        });
    }

    @Test
    @DisplayName("Test participant with null game throws exception")
    void testNullGame() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("P001", "Alice", "alice@uni.edu", null, "Strategist", 95, 8);
        });
    }

    @Test
    @DisplayName("Test participant with null role throws exception")
    void testNullRole() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("P001", "Alice", "alice@uni.edu", "Valorant", null, 95, 8);
        });
    }

    @Test
    @DisplayName("Test personality score below minimum throws exception")
    void testPersonalityScoreTooLow() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 49, 8);
        });
    }

    @Test
    @DisplayName("Test personality score above maximum throws exception")
    void testPersonalityScoreTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 101, 8);
        });
    }

    @Test
    @DisplayName("Test valid boundary personality score 50")
    void testMinimumValidPersonalityScore() {
        Participant participant = new Participant(
                "P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 50, 8
        );
        assertEquals(50, participant.getPersonalityScore());
    }

    @Test
    @DisplayName("Test valid boundary personality score 100")
    void testMaximumValidPersonalityScore() {
        Participant participant = new Participant(
                "P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 100, 8
        );
        assertEquals(100, participant.getPersonalityScore());
    }

    @Test
    @DisplayName("Test skill level below minimum throws exception")
    void testSkillLevelTooLow() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 95, 0);
        });
    }

    @Test
    @DisplayName("Test skill level above maximum throws exception")
    void testSkillLevelTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 95, 11);
        });
    }

    @Test
    @DisplayName("Test valid boundary skill level 1")
    void testMinimumValidSkillLevel() {
        Participant participant = new Participant(
                "P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 95, 1
        );
        assertEquals(1, participant.getSkillLevel());
    }

    @Test
    @DisplayName("Test valid boundary skill level 10")
    void testMaximumValidSkillLevel() {
        Participant participant = new Participant(
                "P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 95, 10
        );
        assertEquals(10, participant.getSkillLevel());
    }

    @Test
    @DisplayName("Test hasGamePreference returns true for matching game")
    void testHasGamePreferenceTrue() {
        Participant participant = new Participant(
                "P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 95, 8
        );
        assertTrue(participant.hasGamePreference("Valorant"));
        assertTrue(participant.hasGamePreference("valorant")); // case insensitive
    }

    @Test
    @DisplayName("Test hasGamePreference returns false for non-matching game")
    void testHasGamePreferenceFalse() {
        Participant participant = new Participant(
                "P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 95, 8
        );
        assertFalse(participant.hasGamePreference("FIFA"));
    }

    @Test
    @DisplayName("Test assigned team initialization")
    void testInitialTeamAssignment() {
        Participant participant = new Participant(
                "P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 95, 8
        );
        assertEquals("Unassigned", participant.getAssignedTeam());
    }

    @Test
    @DisplayName("Test setting assigned team")
    void testSetAssignedTeam() {
        Participant participant = new Participant(
                "P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 95, 8
        );
        participant.setAssignedTeam("Team Alpha");
        assertEquals("Team Alpha", participant.getAssignedTeam());
    }

    @Test
    @DisplayName("Test personality type classification for Leader")
    void testLeaderPersonalityType() {
        Participant participant = new Participant(
                "P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 95, 8
        );
        assertEquals(PersonalityType.LEADER, participant.getPersonalityType());
    }

    @Test
    @DisplayName("Test personality type classification for Balanced")
    void testBalancedPersonalityType() {
        Participant participant = new Participant(
                "P002", "Bob", "bob@uni.edu", "FIFA", "Defender", 75, 7
        );
        assertEquals(PersonalityType.BALANCED, participant.getPersonalityType());
    }

    @Test
    @DisplayName("Test personality type classification for Thinker")
    void testThinkerPersonalityType() {
        Participant participant = new Participant(
                "P003", "Charlie", "charlie@uni.edu", "Dota", "Support", 60, 6
        );
        assertEquals(PersonalityType.THINKER, participant.getPersonalityType());
    }

    @Test
    @DisplayName("Test toString format")
    void testToStringFormat() {
        Participant participant = new Participant(
                "P001", "Alice Johnson", "alice@uni.edu", "Valorant", "Strategist", 95, 8
        );
        String str = participant.toString();

        assertTrue(str.contains("P001"));
        assertTrue(str.contains("Alice Johnson"));
        assertTrue(str.contains("Valorant"));
        assertTrue(str.contains("Strategist"));
    }

    @Test
    @DisplayName("Test toCSV method format")
    void testToCSVFormat() {
        Participant participant = new Participant(
                "P001", "Alice Johnson", "alice@uni.edu", "Valorant", "Strategist", 95, 8
        );
        String csv = participant.toCSV();

        assertNotNull(csv);
        assertTrue(csv.contains("P001"));
        assertTrue(csv.contains("Alice Johnson"));
        assertTrue(csv.contains("alice@uni.edu"));
        assertTrue(csv.contains("Valorant"));
        assertTrue(csv.contains("Strategist"));
        assertTrue(csv.contains("95"));
        assertTrue(csv.contains("8"));
    }
}