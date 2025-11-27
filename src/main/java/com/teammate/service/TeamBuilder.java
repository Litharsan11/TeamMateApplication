package com.teammate.service;

import com.teammate.model.*;
import com.teammate.exception.TeamFormationException;
import com.teammate.util.InputValidator;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service class responsible for building balanced teams
 * Implements the matching strategy for optimal team formation
 */
public class TeamBuilder {
    private static final Logger LOGGER = Logger.getLogger(TeamBuilder.class.getName());
    private static final int MAX_SAME_GAME_PER_TEAM = 2;
    private static final int MIN_ROLE_DIVERSITY = 3;
    private static final int MIN_TEAM_SIZE = 3;
    private static final int MAX_LEADERS_PER_TEAM = 2;

    /**
     * Forms balanced teams from a list of participants
     */
    public List<Team> formTeams(List<Participant> participants, int teamSize)
            throws TeamFormationException {

        // Validate inputs
        if (participants == null || participants.isEmpty()) {
            throw new TeamFormationException("No participants provided for team formation");
        }

        if (teamSize < MIN_TEAM_SIZE) {
            throw new TeamFormationException(
                    "Team size must be at least " + MIN_TEAM_SIZE + " members"
            );
        }

        if (!InputValidator.isValidTeamSize(teamSize, participants.size())) {
            throw new TeamFormationException(
                    "Invalid team size: " + teamSize + ". Must be between " + MIN_TEAM_SIZE + " and " +
                            participants.size() + " (total participants)"
            );
        }

        if (participants.size() < teamSize) {
            throw new TeamFormationException(
                    "Not enough participants (" + participants.size() + ") to form a team of size " +
                            teamSize + ". Please add at least " + (teamSize - participants.size()) +
                            " more participant(s) or reduce team size."
            );
        }

        // Calculate number of complete teams
        int numTeams = participants.size() / teamSize;

        // Check if participants can be evenly divided
        if (numTeams == 0 || participants.size() % teamSize != 0) {
            int remainder = participants.size() % teamSize;
            throw new TeamFormationException(
                    "Cannot form balanced teams: " + participants.size() + " participants cannot be evenly divided by team size " + teamSize + ".\n" +
                            "This would leave " + remainder + " participant(s) unassigned.\n" +
                            "Please choose a team size that divides evenly (e.g., " + getSuggestedTeamSizes(participants.size()) + ")"
            );
        }

        // Count available leaders
        long leaderCount = participants.stream()
                .filter(p -> p.getPersonalityType() == PersonalityType.LEADER)
                .count();

        if (leaderCount < numTeams) {
            throw new TeamFormationException(
                    "Not enough leaders (" + leaderCount + ") to form " + numTeams + " teams. " +
                            "Each team needs at least 1 leader. Please add more participants with leader personality type."
            );
        }

        // Check for all-leaders scenario
        if (leaderCount == participants.size()) {
            throw new TeamFormationException(
                    "Cannot form teams: All " + participants.size() + " participants are leaders.\n" +
                            "Teams require personality diversity. Please add participants with Balanced or Thinker personality types."
            );
        }

        // Reset all assignments
        for (Participant p : participants) {
            p.setAssignedTeam("Unassigned");
        }

        List<Team> teams = new ArrayList<>();

        // Create empty teams
        for (int i = 0; i < numTeams; i++) {
            teams.add(new Team("TEAM-" + (i + 1), "Team " + (i + 1), teamSize));
        }

        // Build balanced teams using improved algorithm
        List<Participant> availableParticipants = new ArrayList<>(participants);

        // Sort leaders by personality score (highest first)
        List<Participant> leaders = availableParticipants.stream()
                .filter(p -> p.getPersonalityType() == PersonalityType.LEADER)
                .sorted(Comparator.comparingInt(Participant::getPersonalityScore).reversed())
                .collect(Collectors.toList());

        // Assign one leader to each team first
        for (int i = 0; i < teams.size() && i < leaders.size(); i++) {
            teams.get(i).addMember(leaders.get(i));
        }

        // Build the rest of each team
        for (Team team : teams) {
            buildBalancedTeam(team, availableParticipants, teamSize);
        }

        // Validate all teams meet constraints
        for (Team team : teams) {
            long teamLeaders = team.getMembers().stream()
                    .filter(p -> p.getPersonalityType() == PersonalityType.LEADER)
                    .count();

            if (teamLeaders == 0) {
                throw new TeamFormationException(
                        "Team " + team.getTeamId() + " has no leaders. Each team must have at least 1 leader."
                );
            }

            if (teamLeaders > MAX_LEADERS_PER_TEAM) {
                throw new TeamFormationException(
                        "Team " + team.getTeamId() + " has " + teamLeaders + " leaders. Maximum allowed is " + MAX_LEADERS_PER_TEAM + ".\n" +
                                "Unable to form balanced teams with current participants. Please try a different team size."
                );
            }

            // Check if team is all leaders (team size > 2 and all are leaders)
            if (team.getCurrentSize() > 2 && teamLeaders == team.getCurrentSize()) {
                throw new TeamFormationException(
                        "Team " + team.getTeamId() + " cannot have all members as leaders. Teams must have personality diversity.\n" +
                                "Unable to form balanced teams. Please add more Balanced or Thinker participants."
                );
            }
        }

        return teams;
    }

    /**
     * Suggests valid team sizes based on total participants
     */
    private String getSuggestedTeamSizes(int totalParticipants) {
        List<Integer> validSizes = new ArrayList<>();
        for (int size = MIN_TEAM_SIZE; size <= totalParticipants; size++) {
            if (totalParticipants % size == 0 && totalParticipants / size >= 1) {
                validSizes.add(size);
            }
        }
        return validSizes.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    /**
     * Builds a single balanced team using the matching algorithm
     * Ensures diversity in games, roles, and personality types
     */
    private void buildBalancedTeam(Team team, List<Participant> availableParticipants, int teamSize) {
        while (!team.isFull()) {
            // Get current unassigned participants
            List<Participant> currentUnassigned = availableParticipants.stream()
                    .filter(p -> "Unassigned".equals(p.getAssignedTeam()))
                    .collect(Collectors.toList());

            if (currentUnassigned.isEmpty()) {
                break;
            }

            // Score each candidate
            Participant bestCandidate = null;
            double bestScore = -1;

            for (Participant candidate : currentUnassigned) {
                double score = calculateFitScore(candidate, team);
                if (score > bestScore) {
                    bestScore = score;
                    bestCandidate = candidate;
                }
            }

            if (bestCandidate != null) {
                team.addMember(bestCandidate);
            } else {
                break;
            }
        }
    }

    /**
     * Calculates how well a participant fits into the current team
     * Based on matching strategy criteria
     */
    private double calculateFitScore(Participant candidate, Team team) {
        double score = 0.0;
        List<Participant> currentMembers = team.getMembers();

        if (currentMembers.isEmpty()) {
            return candidate.getPersonalityScore();
        }

        // 1. Game variety score (30% weight) - avoid too many from same game
        double gameScore = calculateGameVarietyScore(candidate, currentMembers);
        score += gameScore * 0.30;

        // 2. Role diversity score (25% weight) - ensure diverse roles
        double roleScore = calculateRoleDiversityScore(candidate, currentMembers);
        score += roleScore * 0.25;

        // 3. Personality balance score (30% weight) - ideal mix with leader constraint
        double personalityScore = calculatePersonalityBalanceScore(candidate, currentMembers);
        score += personalityScore * 0.30;

        // 4. Skill level balance (15% weight)
        double skillScore = calculateSkillBalanceScore(candidate, currentMembers);
        score += skillScore * 0.15;

        return score;
    }

    /**
     * Calculates game variety contribution
     * Enforces max 2 players from same game per team
     */
    private double calculateGameVarietyScore(Participant candidate, List<Participant> team) {
        long sameGameCount = team.stream()
                .filter(m -> m.getPreferredGame().equalsIgnoreCase(candidate.getPreferredGame()))
                .count();

        if (sameGameCount >= MAX_SAME_GAME_PER_TEAM) {
            return 0.0;
        } else if (sameGameCount == 1) {
            return 50.0;
        } else {
            return 100.0;
        }
    }

    /**
     * Calculates role diversity contribution
     */
    private double calculateRoleDiversityScore(Participant candidate, List<Participant> team) {
        long sameRoleCount = team.stream()
                .filter(m -> m.getPreferredRole().equalsIgnoreCase(candidate.getPreferredRole()))
                .count();

        if (sameRoleCount == 0) {
            return 100.0;
        } else if (sameRoleCount == 1) {
            return 50.0;
        } else {
            return 20.0;
        }
    }

    /**
     * Calculates personality type balance contribution
     * Enforces: at least 1 leader, max 2 leaders per team, no all-leader teams
     */
    private double calculatePersonalityBalanceScore(Participant candidate, List<Participant> team) {
        Map<PersonalityType, Long> typeCounts = team.stream()
                .collect(Collectors.groupingBy(
                        Participant::getPersonalityType,
                        Collectors.counting()
                ));

        long leaderCount = typeCounts.getOrDefault(PersonalityType.LEADER, 0L);
        long thinkerCount = typeCounts.getOrDefault(PersonalityType.THINKER, 0L);
        long balancedCount = typeCounts.getOrDefault(PersonalityType.BALANCED, 0L);

        PersonalityType candidateType = candidate.getPersonalityType();
        int teamSize = team.size();

        // Apply ideal personality mix strategy with strict leader constraints
        if (candidateType == PersonalityType.LEADER) {
            // Don't add leaders if it would make all members leaders
            if (leaderCount > 0 && (leaderCount + 1) == (teamSize + 1)) {
                return 0.0; // Would create all-leader team
            }

            if (leaderCount == 0) return 100.0; // Need at least 1 leader
            if (leaderCount == 1) return 60.0;  // 2 leaders is acceptable
            return 0.0; // More than 2 leaders not allowed
        } else if (candidateType == PersonalityType.THINKER) {
            if (thinkerCount == 0) return 90.0;
            if (thinkerCount == 1) return 70.0;
            return 30.0;
        } else { // BALANCED
            return 80.0;
        }
    }

    /**
     * Calculates skill level balance contribution
     * Aims for similar average skill across teams
     */
    private double calculateSkillBalanceScore(Participant candidate, List<Participant> team) {
        double avgTeamSkill = team.stream()
                .mapToInt(Participant::getSkillLevel)
                .average()
                .orElse(5.0);

        double skillDiff = Math.abs(avgTeamSkill - candidate.getSkillLevel());
        return Math.max(0, 100 - (skillDiff * 10));
    }
}