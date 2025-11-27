package com.teammate.util;

import com.teammate.model.Participant;
import com.teammate.model.Team;
import com.teammate.exception.FileProcessingException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Unit tests for CSVFileHandler utility
 */
class CSVFileHandlerTest {

    private static final String TEST_INPUT_FILE = "test_participants.csv";
    private static final String TEST_OUTPUT_FILE = "test_teams_output.csv";
    private static final String TEST_ALL_PARTICIPANTS = "test_allParticipants.csv";
    private static final String INVALID_FILE = "nonexistent.csv";

    @BeforeEach
    void setUp() {
        deleteTestFiles();
    }

    @AfterEach
    void tearDown() {
        deleteTestFiles();
    }

    private void deleteTestFiles() {
        try {
            Files.deleteIfExists(Paths.get("src/main/resources/" + TEST_INPUT_FILE));
            Files.deleteIfExists(Paths.get("src/main/resources/" + TEST_OUTPUT_FILE));
            Files.deleteIfExists(Paths.get("src/main/resources/" + TEST_ALL_PARTICIPANTS));
        } catch (IOException e) {
            // Ignore
        }
    }

    @Test
    @DisplayName("Test create sample CSV file")
    void testCreateSampleCSV() throws FileProcessingException {
        CSVFileHandler.createSampleCSV(TEST_INPUT_FILE);

        File file = new File("src/main/resources/" + TEST_INPUT_FILE);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    @DisplayName("Test read participants from valid CSV")
    void testReadParticipantsFromCSV() throws FileProcessingException {
        CSVFileHandler.createSampleCSV(TEST_INPUT_FILE);

        List<Participant> participants = CSVFileHandler.readParticipantsFromCSV(TEST_INPUT_FILE);

        assertNotNull(participants);
        assertFalse(participants.isEmpty());
        assertEquals(12, participants.size());
    }

    @Test
    @DisplayName("Test read participants validates data correctly")
    void testReadParticipantsValidation() throws FileProcessingException {
        CSVFileHandler.createSampleCSV(TEST_INPUT_FILE);
        List<Participant> participants = CSVFileHandler.readParticipantsFromCSV(TEST_INPUT_FILE);

        for (Participant p : participants) {
            assertNotNull(p.getParticipantId());
            assertNotNull(p.getName());
            assertNotNull(p.getEmail());
            assertNotNull(p.getPreferredGame());
            assertNotNull(p.getPreferredRole());
            assertTrue(p.getPersonalityScore() >= 50 && p.getPersonalityScore() <= 100);
            assertTrue(p.getSkillLevel() >= 1 && p.getSkillLevel() <= 10);
        }
    }

    @Test
    @DisplayName("Test read from nonexistent file throws exception")
    void testReadNonexistentFile() {
        assertThrows(FileProcessingException.class, () -> {
            CSVFileHandler.readParticipantsFromCSV(INVALID_FILE);
        });
    }

    @Test
    @DisplayName("Test append participant to allParticipants.csv")
    void testAppendParticipantToAll() throws FileProcessingException {
        CSVFileHandler.createSampleCSV("participants_sample.csv");

        Participant newParticipant = new Participant(
                "P999", "Test User", "test@uni.edu",
                "Valorant", "Strategist", 95, 8
        );

        CSVFileHandler.appendParticipantToAll(newParticipant);

        File file = new File("src/main/resources/allParticipants.csv");
        assertTrue(file.exists());

        List<Participant> all = CSVFileHandler.readParticipantsFromCSV("allParticipants.csv");
        assertTrue(all.size() >= 13);

        boolean found = all.stream()
                .anyMatch(p -> p.getParticipantId().equals("P999"));
        assertTrue(found);
    }

    @Test
    @DisplayName("Test get next participant ID")
    void testGetNextParticipantId() throws FileProcessingException {
        CSVFileHandler.createSampleCSV("participants_sample.csv");

        String nextId = CSVFileHandler.getNextParticipantId();
        assertNotNull(nextId);
        assertTrue(nextId.startsWith("P"));
    }

    @Test
    @DisplayName("Test load participants automatically prefers allParticipants")
    void testLoadParticipantsAutomatically() throws FileProcessingException {
        CSVFileHandler.createSampleCSV("participants_sample.csv");

        Participant newP = new Participant("P999", "Auto Test", "auto@uni.edu",
                "FIFA", "Attacker", 80, 7);
        CSVFileHandler.appendParticipantToAll(newP);

        List<Participant> loaded = CSVFileHandler.loadParticipantsAutomatically();
        assertTrue(loaded.size() >= 13);
    }

    @Test
    @DisplayName("Test write teams to CSV")
    void testWriteTeamsToCSV() throws FileProcessingException {
        Team team1 = new Team("T001", "Alpha", 4);
        Team team2 = new Team("T002", "Beta", 4);

        Participant p1 = new Participant("P001", "Alice", "alice@uni.edu",
                "Valorant", "Strategist", 95, 8);
        Participant p2 = new Participant("P002", "Bob", "bob@uni.edu",
                "FIFA", "Defender", 72, 7);

        team1.addMember(p1);
        team2.addMember(p2);

        CSVFileHandler.writeTeamsToCSV(List.of(team1, team2), TEST_OUTPUT_FILE);

        File file = new File("src/main/resources/" + TEST_OUTPUT_FILE);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    @DisplayName("Test write teams creates properly formatted CSV")
    void testWriteTeamsFormat() throws FileProcessingException, IOException {
        Team team = new Team("T001", "Alpha", 4);
        Participant p = new Participant("P001", "Alice", "alice@uni.edu",
                "Valorant", "Strategist", 95, 8);
        team.addMember(p);

        CSVFileHandler.writeTeamsToCSV(List.of(team), TEST_OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/" + TEST_OUTPUT_FILE));

        assertTrue(lines.size() >= 2);

        String header = lines.get(0);
        assertTrue(header.contains("TeamID"));
        assertTrue(header.contains("ParticipantName"));
        assertTrue(header.contains("PersonalityType"));

        String dataLine = lines.get(1);
        assertTrue(dataLine.contains("T001"));
        assertTrue(dataLine.contains("Alpha"));
        assertTrue(dataLine.contains("P001"));
        assertTrue(dataLine.contains("Alice"));
    }

    @Test
    @DisplayName("Test validate CSV format with valid file")
    void testValidateCSVFormatValid() throws FileProcessingException {
        CSVFileHandler.createSampleCSV(TEST_INPUT_FILE);

        boolean isValid = CSVFileHandler.validateCSVFormat(TEST_INPUT_FILE);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Test validate CSV format with nonexistent file")
    void testValidateCSVFormatNonexistent() {
        boolean isValid = CSVFileHandler.validateCSVFormat(INVALID_FILE);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Test validate CSV format with empty file")
    void testValidateCSVFormatEmpty() throws IOException {
        Files.createDirectories(Paths.get("src/main/resources"));
        Files.createFile(Paths.get("src/main/resources/" + TEST_INPUT_FILE));

        boolean isValid = CSVFileHandler.validateCSVFormat(TEST_INPUT_FILE);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Test read CSV with malformed data skips invalid lines")
    void testReadCSVWithMalformedData() throws IOException, FileProcessingException {
        Files.createDirectories(Paths.get("src/main/resources"));
        String csvContent = "ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore\n" +
                "P001,Alice,alice@uni.edu,Valorant,8,Strategist,95\n" +
                "P002,Bob,bob@uni.edu,FIFA,INVALID,Defender,72\n" +
                "P003,Charlie,charlie@uni.edu,DOTA 2,6,Supporter,88\n";

        Files.write(Paths.get("src/main/resources/" + TEST_INPUT_FILE), csvContent.getBytes());

        List<Participant> participants = CSVFileHandler.readParticipantsFromCSV(TEST_INPUT_FILE);

        assertEquals(2, participants.size());
    }

    @Test
    @DisplayName("Test write empty teams list")
    void testWriteEmptyTeamsList() throws FileProcessingException {
        CSVFileHandler.writeTeamsToCSV(List.of(), TEST_OUTPUT_FILE);

        File file = new File("src/main/resources/" + TEST_OUTPUT_FILE);
        assertTrue(file.exists());
    }

    @Test
    @DisplayName("Test write teams with multiple members")
    void testWriteTeamsMultipleMembers() throws FileProcessingException, IOException {
        Team team = new Team("T001", "Alpha", 4);

        Participant p1 = new Participant("P001", "Alice", "alice@uni.edu",
                "Valorant", "Strategist", 95, 8);
        Participant p2 = new Participant("P002", "Bob", "bob@uni.edu",
                "FIFA", "Defender", 72, 7);
        Participant p3 = new Participant("P003", "Charlie", "charlie@uni.edu",
                "DOTA 2", "Supporter", 88, 6);

        team.addMember(p1);
        team.addMember(p2);
        team.addMember(p3);

        CSVFileHandler.writeTeamsToCSV(List.of(team), TEST_OUTPUT_FILE);

        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/" + TEST_OUTPUT_FILE));
        assertEquals(4, lines.size());
    }

    @Test
    @DisplayName("Test read CSV trims whitespace from fields")
    void testReadCSVTrimsWhitespace() throws IOException, FileProcessingException {
        Files.createDirectories(Paths.get("src/main/resources"));
        String csvContent = "ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore\n" +
                "  P001  , Alice Johnson ,alice@uni.edu, Valorant ,8, Strategist ,95\n";

        Files.write(Paths.get("src/main/resources/" + TEST_INPUT_FILE), csvContent.getBytes());

        List<Participant> participants = CSVFileHandler.readParticipantsFromCSV(TEST_INPUT_FILE);

        assertEquals(1, participants.size());
        Participant p = participants.get(0);
        assertEquals("P001", p.getParticipantId());
        assertEquals("Alice Johnson", p.getName());
    }

    @Test
    @DisplayName("Test exception message contains file path")
    void testExceptionContainsFilePath() {
        Exception exception = assertThrows(FileProcessingException.class, () -> {
            CSVFileHandler.readParticipantsFromCSV(INVALID_FILE);
        });

        String message = exception.getMessage();
        assertTrue(message.contains(INVALID_FILE));
    }
}