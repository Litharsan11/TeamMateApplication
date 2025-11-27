package com.teammate.model;

import com.teammate.service.PersonalityClassifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a club member participating in team formation
 * Demonstrates encapsulation with private fields and public accessors
 */
public class Participant {
    private String participantId;
    private String name;
    private String email;
    private String preferredGame;
    private String preferredRole;
    private int personalityScore;
    private PersonalityType personalityType;
    private int skillLevel;
    private String assignedTeam;

    // Constructor with validation (for CSV loading with pre-calculated scores)
    public Participant(String participantId, String name, String email,
                       String preferredGame, String preferredRole,
                       int personalityScore, int skillLevel) throws IllegalArgumentException {

        // Input validation with exceptions
        if (participantId == null || participantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (preferredGame == null || preferredGame.trim().isEmpty()) {
            throw new IllegalArgumentException("Preferred game cannot be empty");
        }
        if (preferredRole == null || preferredRole.trim().isEmpty()) {
            throw new IllegalArgumentException("Preferred role cannot be empty");
        }
        if (personalityScore < 50 || personalityScore > 100) {
            throw new IllegalArgumentException("Personality score must be between 50 and 100");
        }
        if (skillLevel < 1 || skillLevel > 10) {
            throw new IllegalArgumentException("Skill level must be between 1 and 10");
        }

        this.participantId = participantId;
        this.name = name;
        this.email = email;
        this.preferredGame = preferredGame;
        this.preferredRole = preferredRole;
        this.personalityScore = personalityScore;
        this.skillLevel = skillLevel;
        this.assignedTeam = "Unassigned";

        // Classify personality type based on score
        this.personalityType = PersonalityClassifier.classifyPersonality(personalityScore);
    }

    // Constructor for manual entry with survey answers
    public Participant(String participantId, String name, String email,
                       String preferredGame, String preferredRole,
                       int[] surveyAnswers, int skillLevel) throws IllegalArgumentException {

        // Input validation
        if (participantId == null || participantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (preferredGame == null || preferredGame.trim().isEmpty()) {
            throw new IllegalArgumentException("Preferred game cannot be empty");
        }
        if (preferredRole == null || preferredRole.trim().isEmpty()) {
            throw new IllegalArgumentException("Preferred role cannot be empty");
        }
        if (surveyAnswers == null || surveyAnswers.length != 5) {
            throw new IllegalArgumentException("Survey must have exactly 5 answers");
        }
        for (int i = 0; i < surveyAnswers.length; i++) {
            if (surveyAnswers[i] < 1 || surveyAnswers[i] > 5) {
                throw new IllegalArgumentException("Survey answer " + (i + 1) + " must be between 1 and 5");
            }
        }
        if (skillLevel < 1 || skillLevel > 10) {
            throw new IllegalArgumentException("Skill level must be between 1 and 10");
        }

        this.participantId = participantId;
        this.name = name;
        this.email = email;
        this.preferredGame = preferredGame;
        this.preferredRole = preferredRole;
        this.skillLevel = skillLevel;
        this.assignedTeam = "Unassigned";

        // Calculate personality score from survey
        this.personalityScore = calculatePersonalityScore(surveyAnswers);
        this.personalityType = PersonalityClassifier.classifyPersonality(this.personalityScore);
    }

    /**
     * Calculates personality score from 5-question survey
     * Each question rated 1-5, total multiplied by 4 to get 0-100 scale
     */
    private int calculatePersonalityScore(int[] answers) {
        int total = 0;
        for (int answer : answers) {
            total += answer;
        }
        return total * 4;
    }

    // Getters demonstrating encapsulation
    public String getParticipantId() {
        return participantId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPreferredGame() {
        return preferredGame;
    }

    public String getPreferredRole() {
        return preferredRole;
    }

    public int getPersonalityScore() {
        return personalityScore;
    }

    public PersonalityType getPersonalityType() {
        return personalityType;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public String getAssignedTeam() {
        return assignedTeam;
    }

    public void setAssignedTeam(String teamName) {
        this.assignedTeam = teamName;
    }

    // Check if participant has a specific game preference
    public boolean hasGamePreference(String game) {
        return preferredGame.equalsIgnoreCase(game);
    }

    // Override toString for easy display
    @Override
    public String toString() {
        return "Participant[ID=" + participantId +
                ", Name=" + name +
                ", Game=" + preferredGame +
                ", Role=" + preferredRole +
                ", Type=" + personalityType.toString() +
                ", Score=" + personalityScore +
                ", Skills=" + skillLevel + "]";
    }

    // Convert to CSV format for file output
    public String toCSV() {
        return participantId + "," +
                name + "," +
                email + "," +
                preferredGame + "," +
                skillLevel + "," +
                preferredRole + "," +
                personalityScore + "," +
                personalityType.toString();
    }
}