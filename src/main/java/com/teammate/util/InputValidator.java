package com.teammate.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Utility class for validating user inputs
 * Provides centralized validation logic
 */
public class InputValidator {
    private static final int MIN_TEAM_SIZE = 3;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Participant ID pattern (e.g., P001, P100)
    private static final Pattern PARTICIPANT_ID_PATTERN = Pattern.compile(
            "^P\\d{3,}$"
    );

    // Valid games
    private static final String[] VALID_GAMES = {
            "Valorant", "FIFA", "DOTA 2", "CS:GO", "Basketball", "Chess"
    };

    // Valid roles
    private static final String[] VALID_ROLES = {
            "Strategist", "Attacker", "Defender", "Supporter", "Coordinator"
    };

    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email.trim());
        return matcher.matches();
    }

    /**
     * Validates participant ID format
     */
    public static boolean isValidParticipantId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = PARTICIPANT_ID_PATTERN.matcher(id.trim());
        return matcher.matches();
    }

    /**
     * Validates name (non-empty, reasonable length)
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String trimmed = name.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 100;
    }

    /**
     * Validates skill level (1-10)
     */
    public static boolean isValidSkillLevel(int skill) {
        return skill >= 1 && skill <= 10;
    }

    /**
     * Validates personality score (50-100)
     */
    public static boolean isValidPersonalityScore(int score) {
        return score >= 50 && score <= 100;
    }

    /**
     * Validates game selection
     */
    public static boolean isValidGame(String game) {
        if (game == null || game.trim().isEmpty()) {
            return false;
        }
        for (String validGame : VALID_GAMES) {
            if (validGame.equalsIgnoreCase(game.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates role selection
     */
    public static boolean isValidRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return false;
        }
        for (String validRole : VALID_ROLES) {
            if (validRole.equalsIgnoreCase(role.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates team size (minimum 3 members)
     */
    public static boolean isValidTeamSize(int teamSize, int totalParticipants) {
        return teamSize >= MIN_TEAM_SIZE && teamSize <= totalParticipants;
    }

    /**
     * Gets formatted error message for email
     */
    public static String getEmailErrorMessage() {
        return "Invalid email format. Expected: username@domain.com";
    }

    /**
     * Gets formatted error message for participant ID
     */
    public static String getParticipantIdErrorMessage() {
        return "Invalid participant ID format. Expected: P001, P002, etc.";
    }

    /**
     * Gets formatted error message for name
     */
    public static String getNameErrorMessage() {
        return "Name must be between 2 and 100 characters";
    }

    /**
     * Gets formatted error message for skill level
     */
    public static String getSkillLevelErrorMessage() {
        return "Skill level must be between 1 and 10";
    }

    /**
     * Gets formatted error message for personality score
     */
    public static String getPersonalityScoreErrorMessage() {
        return "Personality score must be between 50 and 100";
    }

    /**
     * Gets formatted error message for game
     */
    public static String getGameErrorMessage() {
        return "Invalid game. Valid options: Valorant, FIFA, DOTA 2, CS:GO, Basketball, Chess";
    }

    /**
     * Gets formatted error message for role
     */
    public static String getRoleErrorMessage() {
        return "Invalid role. Valid options: Strategist, Attacker, Defender, Supporter, Coordinator";
    }

    /**
     * Gets valid games list
     */
    public static String[] getValidGames() {
        return VALID_GAMES.clone();
    }

    /**
     * Gets valid roles list
     */
    public static String[] getValidRoles() {
        return VALID_ROLES.clone();
    }

    /**
     * Gets minimum team size
     */
    public static int getMinTeamSize() {
        return MIN_TEAM_SIZE;
    }
}