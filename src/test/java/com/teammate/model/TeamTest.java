package com.teammate.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for Team class
 */
class TeamTest {

    private Team team;
    private Participant participant1;
    private Participant participant2;
    private Participant participant3;

    @BeforeEach
    void setUp() {
        team = new Team("T001", "Alpha Team", 4);

        participant1 = new Participant(
                "P001", "Alice", "alice@uni.edu", "Valorant",
                "Strategist", 95, 8
        );

        participant2 = new Participant(
                "P002", "Bob", "bob@uni.edu", "FIFA",
                "Defender", 72, 7
        );

        participant3 = new Participant(
                "P003", "Charlie", "charlie@uni.edu", "DOTA 2",
                "Supporter", 88, 6
        );
    }

    @Test
    @DisplayName("Test valid team creation")
    void testValidTeamCreation() {
        assertNotNull(team);
        assertEquals("T001", team.getTeamId());
        assertEquals("Alpha Team", team.getTeamName());
        assertEquals(4, team.getMaxSize());
        assertEquals(0, team.getCurrentSize());
    }

    @Test
    @DisplayName("Test team with null ID throws exception")
    void testNullTeamId() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Team(null, "Alpha", 4);
        });
    }

    @Test
    @DisplayName("Test team with empty ID throws exception")
    void testEmptyTeamId() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Team("", "Alpha", 4);
        });
    }

    @Test
    @DisplayName("Test team with null name throws exception")
    void testNullTeamName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Team("T001", null, 4);
        });
    }

    @Test
    @DisplayName("Test team with zero size throws exception")
    void testZeroTeamSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Team("T001", "Alpha", 0);
        });
    }

    @Test
    @DisplayName("Test team with negative size throws exception")
    void testNegativeTeamSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Team("T001", "Alpha", -1);
        });
    }

    @Test
    @DisplayName("Test adding member to team")
    void testAddMember() {
        boolean added = team.addMember(participant1);

        assertTrue(added);
        assertEquals(1, team.getCurrentSize());
        assertEquals("Alpha Team", participant1.getAssignedTeam());
    }

    @Test
    @DisplayName("Test adding multiple members to team")
    void testAddMultipleMembers() {
        team.addMember(participant1);
        team.addMember(participant2);
        team.addMember(participant3);

        assertEquals(3, team.getCurrentSize());
        assertFalse(team.isFull());
    }

    @Test
    @DisplayName("Test team is full after reaching max size")
    void testTeamIsFull() {
        team.addMember(participant1);
        team.addMember(participant2);
        team.addMember(participant3);

        Participant participant4 = new Participant(
                "P004", "Diana", "diana@uni.edu", "CS:GO",
                "Attacker", 65, 9
        );
        team.addMember(participant4);

        assertTrue(team.isFull());
        assertEquals(4, team.getCurrentSize());
    }

    @Test
    @DisplayName("Test cannot add member to full team")
    void testCannotAddToFullTeam() {
        team.addMember(participant1);
        team.addMember(participant2);
        team.addMember(participant3);

        Participant participant4 = new Participant(
                "P004", "Diana", "diana@uni.edu", "CS:GO",
                "Attacker", 65, 9
        );
        team.addMember(participant4);

        Participant participant5 = new Participant(
                "P005", "Eve", "eve@uni.edu", "Basketball",
                "Coordinator", 78, 7
        );
        boolean added = team.addMember(participant5);

        assertFalse(added);
        assertEquals(4, team.getCurrentSize());
        assertEquals("Unassigned", participant5.getAssignedTeam());
    }

    @Test
    @DisplayName("Test calculate diversity score with diverse games")
    void testCalculateDiversityScoreHighDiversity() {
        team.addMember(participant1); // Valorant
        team.addMember(participant2); // FIFA
        team.addMember(participant3); // DOTA 2

        double diversityScore = team.calculateDiversityScore();

        assertEquals(1.0, diversityScore, 0.01); // 3 unique games / 3 members = 1.0
    }

    @Test
    @DisplayName("Test calculate diversity score with same games")
    void testCalculateDiversityScoreLowDiversity() {
        Participant p2 = new Participant("P002", "Bob", "bob@uni.edu", "Valorant", "Defender", 72, 7);
        Participant p3 = new Participant("P003", "Charlie", "charlie@uni.edu", "Valorant", "Supporter", 88, 6);

        team.addMember(participant1); // Valorant
        team.addMember(p2);          // Valorant
        team.addMember(p3);          // Valorant

        double diversityScore = team.calculateDiversityScore();

        assertEquals(0.33, diversityScore, 0.01); // 1 unique game / 3 members
    }

    @Test
    @DisplayName("Test calculate diversity score with empty team")
    void testCalculateDiversityScoreEmptyTeam() {
        double diversityScore = team.calculateDiversityScore();
        assertEquals(0.0, diversityScore);
    }

    @Test
    @DisplayName("Test calculate balance score with mixed personality types")
    void testCalculateBalanceScoreMixedTypes() {
        team.addMember(participant1); // LEADER (95)
        team.addMember(participant2); // BALANCED (72)

        Participant thinker = new Participant(
                "P004", "Diana", "diana@uni.edu", "CS:GO",
                "Attacker", 65, 9
        );
        team.addMember(thinker); // THINKER (65)

        double balanceScore = team.calculateBalanceScore();

        assertTrue(balanceScore > 0.5);
    }

    @Test
    @DisplayName("Test calculate balance score with same personality types")
    void testCalculateBalanceScoreSameTypes() {
        Participant leader1 = new Participant(
                "P004", "Diana", "diana@uni.edu", "CS:GO",
                "Attacker", 92, 9
        );

        Participant leader2 = new Participant(
                "P005", "Eve", "eve@uni.edu", "Basketball",
                "Coordinator", 91, 7
        );

        team.addMember(participant1); // LEADER (95)
        team.addMember(leader1);      // LEADER (92)
        team.addMember(leader2);      // LEADER (91)

        double balanceScore = team.calculateBalanceScore();

        assertTrue(balanceScore < 0.5);
    }

    @Test
    @DisplayName("Test get role count")
    void testGetRoleCount() {
        team.addMember(participant1); // Strategist

        Participant strategist2 = new Participant(
                "P004", "Diana", "diana@uni.edu", "CS:GO",
                "Strategist", 92, 9
        );
        team.addMember(strategist2); // Strategist
        team.addMember(participant2); // Defender

        assertEquals(2, team.getRoleCount("Strategist"));
        assertEquals(1, team.getRoleCount("Defender"));
        assertEquals(0, team.getRoleCount("Attacker"));
    }

    @Test
    @DisplayName("Test get game count")
    void testGetGameCount() {
        team.addMember(participant1); // Valorant
        team.addMember(participant2); // FIFA

        Participant valorant2 = new Participant(
                "P004", "Diana", "diana@uni.edu", "Valorant",
                "Attacker", 92, 9
        );
        team.addMember(valorant2); // Valorant

        assertEquals(2, team.getGameCount("Valorant"));
        assertEquals(1, team.getGameCount("FIFA"));
        assertEquals(0, team.getGameCount("CS:GO"));
    }

    @Test
    @DisplayName("Test has role diversity with diverse roles")
    void testHasRoleDiversityTrue() {
        team.addMember(participant1); // Strategist
        team.addMember(participant2); // Defender
        team.addMember(participant3); // Supporter

        assertTrue(team.hasRoleDiversity());
    }

    @Test
    @DisplayName("Test has role diversity with same roles")
    void testHasRoleDiversityFalse() {
        Participant defender2 = new Participant(
                "P004", "Diana", "diana@uni.edu", "CS:GO",
                "Defender", 92, 9
        );

        Participant defender3 = new Participant(
                "P005", "Eve", "eve@uni.edu", "Basketball",
                "Defender", 91, 7
        );

        team.addMember(participant2); // Defender
        team.addMember(defender2);    // Defender
        team.addMember(defender3);    // Defender

        assertFalse(team.hasRoleDiversity());
    }

    @Test
    @DisplayName("Test has game variety with diverse games")
    void testHasGameVarietyTrue() {
        team.addMember(participant1); // Valorant
        team.addMember(participant2); // FIFA
        team.addMember(participant3); // DOTA 2

        assertTrue(team.hasGameVariety());
    }

    @Test
    @DisplayName("Test has game variety with too many same games")
    void testHasGameVarietyFalse() {
        Participant val2 = new Participant("P004", "Diana", "diana@uni.edu", "Valorant", "Attacker", 92, 9);
        Participant val3 = new Participant("P005", "Eve", "eve@uni.edu", "Valorant", "Coordinator", 91, 7);

        team.addMember(participant1); // Valorant
        team.addMember(val2);         // Valorant
        team.addMember(val3);         // Valorant

        assertFalse(team.hasGameVariety()); // 3 from same game exceeds max of 2
    }

    @Test
    @DisplayName("Test get average skill level")
    void testGetAverageSkillLevel() {
        team.addMember(participant1); // Skill: 8
        team.addMember(participant2); // Skill: 7
        team.addMember(participant3); // Skill: 6

        double avgSkill = team.getAverageSkillLevel();

        assertEquals(7.0, avgSkill, 0.01);
    }

    @Test
    @DisplayName("Test get average skill level for empty team")
    void testGetAverageSkillLevelEmptyTeam() {
        double avgSkill = team.getAverageSkillLevel();
        assertEquals(0.0, avgSkill);
    }

    @Test
    @DisplayName("Test get members returns defensive copy")
    void testGetMembersDefensiveCopy() {
        team.addMember(participant1);

        List<Participant> members = team.getMembers();
        members.clear();

        assertEquals(1, team.getCurrentSize());
    }

    @Test
    @DisplayName("Test toString format")
    void testToStringFormat() {
        team.addMember(participant1);
        team.addMember(participant2);

        String teamString = team.toString();

        assertTrue(teamString.contains("T001"));
        assertTrue(teamString.contains("Alpha Team"));
        assertTrue(teamString.contains("2"));
        assertTrue(teamString.contains("4"));
    }

    @Test
    @DisplayName("Test detailed summary contains all information")
    void testDetailedSummary() {
        team.addMember(participant1);
        team.addMember(participant2);

        String summary = team.getDetailedSummary();

        assertTrue(summary.contains("Alpha Team"));
        assertTrue(summary.contains("Alice"));
        assertTrue(summary.contains("Bob"));
        assertTrue(summary.contains("Strategist"));
        assertTrue(summary.contains("Defender"));
    }
}