package com.teammate.model;

/**
 * Enumeration for personality types
 * Demonstrates use of enums in OOP
 */
public enum PersonalityType {
    LEADER("Leader", 90, 100,
            "Natural leadership qualities, takes initiative, confident decision maker"),
    BALANCED("Balanced", 70, 89,
            "Well-rounded team player, adaptable, cooperative"),
    THINKER("Thinker", 50, 69,
            "Analytical, strategic thinking, detail-oriented");

    private final String displayName;
    private final int minScore;
    private final int maxScore;
    private final String description;

    // Constructor for enum values
    PersonalityType(String displayName, int minScore, int maxScore, String description) {
        this.displayName = displayName;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinScore() {
        return minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public String getDescription() {
        return description;
    }

    // Check if a score falls within this personality type's range
    public boolean matchesScore(int score) {
        return score >= minScore && score <= maxScore;
    }

    @Override
    public String toString() {
        return displayName;
    }
}