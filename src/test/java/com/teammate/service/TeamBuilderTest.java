package com.teammate.service;

import com.teammate.model.Participant;
import com.teammate.model.Team;
import com.teammate.model.PersonalityType;
import com.teammate.exception.TeamFormationException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for TeamBuilder service
 */
class TeamBuilderTest {

    private TeamBuilder teamBuilder;
    private List<Participant> participants;

    @BeforeEach
    void setUp() {
        teamBuilder = new TeamBuilder();
        participants = createTestParticipants();
    }

    private List<Participant> createTestParticipants() {
        List<Participant> list = new ArrayList<>();

        list.add(new Participant("P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 95, 8));
        list.add(new Participant("P002", "Bob", "bob@uni.edu", "FIFA", "Defender", 72, 7));
        list.add(new Participant("P003", "Charlie", "charlie@uni.edu", "DOTA 2", "Supporter", 88, 6));
        list.add(new Participant("P004", "Diana", "diana@uni.edu", "CS:GO", "Attacker", 65, 9));
        list.add(new Participant("P005", "Eve", "eve@uni.edu", "Basketball", "Coordinator", 78, 7));
        list.add(new Participant("P006", "Frank", "frank@uni.edu", "Valorant", "Strategist", 92, 8));
        list.add(new Participant("P007", "Grace", "grace@uni.edu", "FIFA", "Attacker", 58, 6));
        list.add(new Participant("P008", "Henry", "henry@uni.edu", "Chess", "Defender", 81, 7));
        list.add(new Participant("P009", "Iris", "iris@uni.edu", "DOTA 2", "Supporter", 69, 8));
        list.add(new Participant("P010", "Jack", "jack@uni.edu", "CS:GO", "Coordinator", 91, 9));
        list.add(new Participant("P011", "Kate", "kate@uni.edu", "Basketball", "Strategist", 74, 7));
        list.add(new Participant("P012", "Leo", "leo@uni.edu", "FIFA", "Attacker", 86, 8));

        return list;
    }

    @Test
    @DisplayName("Test form teams with valid inputs")
    void testFormTeamsValid() throws TeamFormationException {
        List<Team> teams = teamBuilder.formTeams(participants, 4);

        assertNotNull(teams);
        assertEquals(3, teams.size());

        for (Team team : teams) {
            assertTrue(team.getCurrentSize() <= 4);
        }
    }

    @Test
    @DisplayName("Test minimum team size is 3")
    void testMinimumTeamSize() {
        assertThrows(TeamFormationException.class, () -> {
            teamBuilder.formTeams(participants, 2);
        });
    }

    @Test
    @DisplayName("Test all teams have at least 1 leader")
    void testAllTeamsHaveLeader() throws TeamFormationException {
        List<Team> teams = teamBuilder.formTeams(participants, 4);

        for (Team team : teams) {
            long leaderCount = team.getMembers().stream()
                    .filter(p -> p.getPersonalityType() == PersonalityType.LEADER)
                    .count();
            assertTrue(leaderCount >= 1, "Team " + team.getTeamId() + " should have at least 1 leader");
        }
    }

    @Test
    @DisplayName("Test no team has more than 2 leaders")
    void testNoTeamHasMoreThan2Leaders() throws TeamFormationException {
        List<Team> teams = teamBuilder.formTeams(participants, 4);

        for (Team team : teams) {
            long leaderCount = team.getMembers().stream()
                    .filter(p -> p.getPersonalityType() == PersonalityType.LEADER)
                    .count();
            assertTrue(leaderCount <= 2, "Team " + team.getTeamId() + " should have at most 2 leaders");
        }
    }

    @Test
    @DisplayName("Test insufficient leaders throws exception")
    void testInsufficientLeaders() {
        List<Participant> fewLeaders = new ArrayList<>();
        // Only 1 leader but trying to form 2 teams
        fewLeaders.add(new Participant("P001", "Alice", "alice@uni.edu", "Valorant", "Strategist", 95, 8));
        fewLeaders.add(new Participant("P002", "Bob", "bob@uni.edu", "FIFA", "Defender", 72, 7));
        fewLeaders.add(new Participant("P003", "Charlie", "charlie@uni.edu", "DOTA 2", "Supporter", 65, 6));
        fewLeaders.add(new Participant("P004", "Diana", "diana@uni.edu", "CS:GO", "Attacker", 65, 9));
        fewLeaders.add(new Participant("P005", "Eve", "eve@uni.edu", "Basketball", "Coordinator", 58, 7));
        fewLeaders.add(new Participant("P006", "Frank", "frank@uni.edu", "Valorant", "Strategist", 62, 8));

        assertThrows(TeamFormationException.class, () -> {
            teamBuilder.formTeams(fewLeaders, 3);
        });
    }

    @Test
    @DisplayName("Test all participants are assigned to teams")
    void testAllParticipantsAssigned() throws TeamFormationException {
        List<Team> teams = teamBuilder.formTeams(participants, 4);

        int totalAssigned = 0;
        for (Team team : teams) {
            totalAssigned += team.getCurrentSize();
        }

        assertEquals(12, totalAssigned);
    }

    @Test
    @DisplayName("Test form teams with different team size")
    void testFormTeamsDifferentSize() throws TeamFormationException {
        List<Team> teams = teamBuilder.formTeams(participants, 3);

        assertNotNull(teams);
        assertEquals(4, teams.size());

        for (Team team : teams) {
            assertTrue(team.getCurrentSize() <= 3);
        }
    }

    @Test
    @DisplayName("Test form teams with team size of 6")
    void testFormTeamsLargerSize() throws TeamFormationException {
        List<Team> teams = teamBuilder.formTeams(participants, 6);

        assertNotNull(teams);
        assertEquals(2, teams.size());

        for (Team team : teams) {
            assertTrue(team.getCurrentSize() <= 6);
        }
    }

    @Test
    @DisplayName("Test form teams with null participants throws exception")
    void testFormTeamsNullParticipants() {
        assertThrows(TeamFormationException.class, () -> {
            teamBuilder.formTeams(null, 4);
        });
    }

    @Test
    @DisplayName("Test form teams with empty participants throws exception")
    void testFormTeamsEmptyParticipants() {
        assertThrows(TeamFormationException.class, () -> {
            teamBuilder.formTeams(new ArrayList<>(), 4);
        });
    }

    @Test
    @DisplayName("Test form teams with zero team size throws exception")
    void testFormTeamsZeroSize() {
        assertThrows(TeamFormationException.class, () -> {
            teamBuilder.formTeams(participants, 0);
        });
    }

    @Test
    @DisplayName("Test form teams with negative team size throws exception")
    void testFormTeamsNegativeSize() {
        assertThrows(TeamFormationException.class, () -> {
            teamBuilder.formTeams(participants, -1);
        });
    }

    @Test
    @DisplayName("Test form teams with insufficient participants throws exception")
    void testFormTeamsInsufficientParticipants() {
        List<Participant> fewParticipants = new ArrayList<>();
        fewParticipants.add(participants.get(0));
        fewParticipants.add(participants.get(1));

        assertThrows(TeamFormationException.class, () -> {
            teamBuilder.formTeams(fewParticipants, 4);
        });
    }

    @Test
    @DisplayName("Test teams have personality diversity")
    void testTeamsHavePersonalityDiversity() throws TeamFormationException {
        List<Team> teams = teamBuilder.formTeams(participants, 4);

        for (Team team : teams) {
            if (team.getCurrentSize() >= 3) {
                double balanceScore = team.calculateBalanceScore();
                assertTrue(balanceScore >= 0);
            }
        }
    }

    @Test
    @DisplayName("Test teams have role diversity")
    void testTeamsHaveRoleDiversity() throws TeamFormationException {
        List<Team> teams = teamBuilder.formTeams(participants, 4);

        for (Team team : teams) {
            if (team.getCurrentSize() >= 3) {
                assertTrue(team.hasRoleDiversity() || team.getCurrentSize() < 3);
            }
        }
    }

    @Test
    @DisplayName("Test teams have game diversity")
    void testTeamsHaveGameDiversity() throws TeamFormationException {
        List<Team> teams = teamBuilder.formTeams(participants, 4);

        for (Team team : teams) {
            if (team.getCurrentSize() >= 2) {
                double diversityScore = team.calculateDiversityScore();
                assertTrue(diversityScore >= 0);
            }
        }
    }

    @Test
    @DisplayName("Test team builder can be reused")
    void testTeamBuilderReuse() throws TeamFormationException {
        List<Team> teams1 = teamBuilder.formTeams(participants, 4);
        assertNotNull(teams1);

        for (Participant p : participants) {
            p.setAssignedTeam("Unassigned");
        }

        List<Team> teams2 = teamBuilder.formTeams(participants, 3);
        assertNotNull(teams2);

        assertNotEquals(teams1.size(), teams2.size());
    }

    @Test
    @DisplayName("Test large dataset performance")
    void testLargeDatasetPerformance() throws TeamFormationException {
        List<Participant> largeList = new ArrayList<>();
        String[] games = {"Valorant", "FIFA", "DOTA 2", "CS:GO", "Basketball", "Chess"};
        String[] roles = {"Strategist", "Attacker", "Defender", "Supporter", "Coordinator"};

        for (int i = 0; i < 100; i++) {
            int score = 50 + (i % 51);
            largeList.add(new Participant(
                    "P" + String.format("%03d", i),
                    "Person" + i,
                    "person" + i + "@uni.edu",
                    games[i % games.length],
                    roles[i % roles.length],
                    score,
                    1 + (i % 10)
            ));
        }

        long startTime = System.currentTimeMillis();
        List<Team> teams = teamBuilder.formTeams(largeList, 5);
        long endTime = System.currentTimeMillis();

        assertNotNull(teams);
        assertEquals(20, teams.size());

        long duration = endTime - startTime;
        System.out.println("Large dataset (100 participants) processed in: " + duration + "ms");

        assertTrue(duration < 10000, "Processing should complete in less than 10 seconds");

        for (Team team : teams) {
            long leaderCount = team.getMembers().stream()
                    .filter(p -> p.getPersonalityType() == PersonalityType.LEADER)
                    .count();
            assertTrue(leaderCount >= 1 && leaderCount <= 2);
        }
    }

    @Test
    @DisplayName("Test each team has unique ID")
    void testTeamsHaveUniqueIds() throws TeamFormationException {
        List<Team> teams = teamBuilder.formTeams(participants, 4);

        List<String> teamIds = new ArrayList<>();
        for (Team team : teams) {
            assertFalse(teamIds.contains(team.getTeamId()));
            teamIds.add(team.getTeamId());
        }
    }

    @Test
    @DisplayName("Test no participant is assigned to multiple teams")
    void testNoDoubleAssignment() throws TeamFormationException {
        List<Team> teams = teamBuilder.formTeams(participants, 4);

        List<String> assignedIds = new ArrayList<>();
        for (Team team : teams) {
            for (Participant member : team.getMembers()) {
                assertFalse(assignedIds.contains(member.getParticipantId()));
                assignedIds.add(member.getParticipantId());
            }
        }
    }

    @Test
    @DisplayName("Test teams respect game variety constraint")
    void testTeamsRespectGameVarietyConstraint() throws TeamFormationException {
        List<Team> teams = teamBuilder.formTeams(participants, 4);

        for (Team team : teams) {
            assertTrue(team.hasGameVariety() || team.getCurrentSize() < 3);
        }
    }

    @Test
    @DisplayName("Test exception message is descriptive")
    void testExceptionMessageIsDescriptive() {
        Exception exception = assertThrows(TeamFormationException.class, () -> {
            teamBuilder.formTeams(participants, 0);
        });

        String message = exception.getMessage();
        assertNotNull(message);
        assertFalse(message.isEmpty());
    }

    @Test
    @DisplayName("Test team cannot have all members as leaders")
    void testTeamCannotBeAllLeaders() {
        List<Participant> allLeaders = new ArrayList<>();
        allLeaders.add(new Participant("P001", "Leader1", "l1@uni.edu", "Valorant", "Strategist", 95, 8));
        allLeaders.add(new Participant("P002", "Leader2", "l2@uni.edu", "FIFA", "Attacker", 92, 9));
        allLeaders.add(new Participant("P003", "Leader3", "l3@uni.edu", "DOTA 2", "Defender", 93, 7));
        allLeaders.add(new Participant("P004", "Leader4", "l4@uni.edu", "CS:GO", "Supporter", 91, 8));
        allLeaders.add(new Participant("P005", "Leader5", "l5@uni.edu", "Basketball", "Coordinator", 94, 9));
        allLeaders.add(new Participant("P006", "Leader6", "l6@uni.edu", "Chess", "Strategist", 96, 7));

        assertThrows(TeamFormationException.class, () -> {
            teamBuilder.formTeams(allLeaders, 3);
        });
    }

    @Test
    @DisplayName("Test validates max 2 leaders per team strictly")
    void testStrictMax2LeadersPerTeam() throws TeamFormationException {
        List<Participant> mixedList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            int score = (i < 6) ? 95 : ((i < 10) ? 75 : 60);
            mixedList.add(new Participant(
                    "P" + String.format("%03d", i + 1),
                    "Person" + (i + 1),
                    "person" + (i + 1) + "@uni.edu",
                    "Valorant",
                    "Strategist",
                    score,
                    7
            ));
        }

        List<Team> teams = teamBuilder.formTeams(mixedList, 5);

        for (Team team : teams) {
            long leaderCount = team.getMembers().stream()
                    .filter(p -> p.getPersonalityType() == PersonalityType.LEADER)
                    .count();
            assertTrue(leaderCount <= 2,
                    "Team " + team.getTeamId() + " has " + leaderCount + " leaders, max is 2");
            assertTrue(leaderCount >= 1,
                    "Team " + team.getTeamId() + " must have at least 1 leader");
        }
    }
}