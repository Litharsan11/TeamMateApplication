package com.teammate.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a team formed from participants
 * Demonstrates composition (Team has Participants)
 */
public class Team {
    private String teamId;
    private String teamName;
    private List<Participant> members;
    private int maxSize;

    // Constructor
    public Team(String teamId, String teamName, int maxSize) {
        if (teamId == null || teamId.trim().isEmpty()) {
            throw new IllegalArgumentException("Team ID cannot be empty");
        }
        if (teamName == null || teamName.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be empty");
        }
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Team size must be positive");
        }

        this.teamId = teamId;
        this.teamName = teamName;
        this.maxSize = maxSize;
        this.members = new ArrayList<>();
    }

    /**
     * Adds a participant to the team
     * @param participant The participant to add
     * @return true if added successfully, false if team is full
     */
    public boolean addMember(Participant participant) {
        if (isFull()) {
            return false;
        }
        members.add(participant);
        participant.setAssignedTeam(this.teamName);
        return true;
    }

    /**
     * Checks if team has reached maximum capacity
     */
    public boolean isFull() {
        return members.size() >= maxSize;
    }

    /**
     * Gets the current number of members
     */
    public int getCurrentSize() {
        return members.size();
    }

    /**
     * Calculates team diversity score based on different games
     * Higher score means more diverse game preferences
     */
    public double calculateDiversityScore() {
        if (members.isEmpty()) {
            return 0.0;
        }

        Set<String> uniqueGames = members.stream()
                .map(Participant::getPreferredGame)
                .collect(Collectors.toSet());

        // Diversity is ratio of unique games to team size
        return (double) uniqueGames.size() / members.size();
    }

    /**
     * Calculates team balance score based on personality type distribution
     * Balanced teams have mix of Leaders, Balanced, and Thinkers
     */
    public double calculateBalanceScore() {
        if (members.isEmpty()) {
            return 0.0;
        }

        long leaders = members.stream()
                .filter(p -> p.getPersonalityType() == PersonalityType.LEADER)
                .count();
        long balanced = members.stream()
                .filter(p -> p.getPersonalityType() == PersonalityType.BALANCED)
                .count();
        long thinkers = members.stream()
                .filter(p -> p.getPersonalityType() == PersonalityType.THINKER)
                .count();

        // Perfect balance would have equal distribution
        double variance = Math.abs(leaders - balanced) + Math.abs(balanced - thinkers) + Math.abs(thinkers - leaders);
        return 1.0 - (variance / (members.size() * 2.0));
    }

    /**
     * Gets count of members with a specific role
     */
    public long getRoleCount(String role) {
        return members.stream()
                .filter(p -> p.getPreferredRole().equalsIgnoreCase(role))
                .count();
    }

    /**
     * Gets count of members with a specific game preference
     */
    public long getGameCount(String game) {
        return members.stream()
                .filter(p -> p.getPreferredGame().equalsIgnoreCase(game))
                .count();
    }

    /**
     * Checks if team has diverse roles (at least 3 different roles)
     */
    public boolean hasRoleDiversity() {
        Set<String> uniqueRoles = members.stream()
                .map(Participant::getPreferredRole)
                .collect(Collectors.toSet());

        return uniqueRoles.size() >= Math.min(3, members.size());
    }

    /**
     * Checks if game variety constraint is satisfied (max 2 per game)
     */
    public boolean hasGameVariety() {
        Map<String, Long> gameCounts = members.stream()
                .collect(Collectors.groupingBy(
                        Participant::getPreferredGame,
                        Collectors.counting()
                ));

        return gameCounts.values().stream().allMatch(count -> count <= 2);
    }

    /**
     * Calculates average skill level of team
     */
    public double getAverageSkillLevel() {
        if (members.isEmpty()) {
            return 0.0;
        }
        return members.stream()
                .mapToInt(Participant::getSkillLevel)
                .average()
                .orElse(0.0);
    }

    // Getters
    public String getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public List<Participant> getMembers() {
        return new ArrayList<>(members); // Return copy
    }

    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public String toString() {
        return String.format("Team[%s - %s] Members: %d/%d, Diversity: %.2f, Balance: %.2f",
                teamId, teamName, members.size(), maxSize,
                calculateDiversityScore(), calculateBalanceScore());
    }

    /**
     * Gets detailed team summary
     */
    public String getDetailedSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== %s (%s) ===\n", teamName, teamId));
        sb.append(String.format("Size: %d/%d\n", members.size(), maxSize));
        sb.append(String.format("Average Skill: %.2f\n", getAverageSkillLevel()));
        sb.append(String.format("Diversity Score: %.2f\n", calculateDiversityScore()));
        sb.append(String.format("Balance Score: %.2f\n", calculateBalanceScore()));
        sb.append(String.format("Role Diversity: %s\n", hasRoleDiversity() ? "Yes" : "No"));
        sb.append(String.format("Game Variety: %s\n", hasGameVariety() ? "Yes" : "No"));
        sb.append("\nMembers:\n");
        for (Participant p : members) {
            sb.append(String.format("  - %s (%s, %s, %s, Skill: %d)\n",
                    p.getName(), p.getPreferredGame(), p.getPreferredRole(),
                    p.getPersonalityType(), p.getSkillLevel()));
        }
        return sb.toString();
    }
}