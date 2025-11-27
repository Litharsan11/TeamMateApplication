package com.teammate.util;

import com.teammate.model.Participant;
import com.teammate.model.Team;
import com.teammate.exception.FileProcessingException;
import com.teammate.exception.DataValidationException;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Handles CSV file reading and writing operations
 * Demonstrates file I/O with exception handling
 */
public class CSVFileHandler {
    private static final Logger LOGGER = Logger.getLogger(CSVFileHandler.class.getName());
    private static final String CSV_DELIMITER = ",";
    private static final String ALL_PARTICIPANTS_FILE = "allParticipants.csv";
    private static final String SAMPLE_FILE = "participants_sample.csv";

    /**
     * Gets the path to the resource directory
     */
    private static String getResourcePath(String filename) {
        try {
            String resourcePath = "src/main/resources/" + filename;
            File file = new File(resourcePath);
            file.getParentFile().mkdirs();
            return resourcePath;
        } catch (Exception e) {
            LOGGER.warning("Could not create resource path, using current directory: " + e.getMessage());
            return filename;
        }
    }

    /**
     * Loads participants automatically - first tries allParticipants.csv,
     * if empty or not found, loads from participants_sample.csv
     */
    public static List<Participant> loadParticipantsAutomatically() throws FileProcessingException {
        String allParticipantsPath = getResourcePath(ALL_PARTICIPANTS_FILE);

        // Check if allParticipants.csv exists and has content
        if (Files.exists(Paths.get(allParticipantsPath))) {
            try {
                List<Participant> participants = readParticipantsFromCSV(ALL_PARTICIPANTS_FILE);
                if (!participants.isEmpty()) {
                    LOGGER.info("Loaded " + participants.size() + " participants from " + ALL_PARTICIPANTS_FILE);
                    return participants;
                }
            } catch (FileProcessingException e) {
                LOGGER.warning("Failed to read " + ALL_PARTICIPANTS_FILE + ", falling back to sample file");
            }
        }

        // Fall back to participants_sample.csv
        LOGGER.info("Loading from " + SAMPLE_FILE);
        List<Participant> participants = readParticipantsFromCSV(SAMPLE_FILE);
        LOGGER.info("Loaded " + participants.size() + " participants from " + SAMPLE_FILE);
        return participants;
    }

    /**
     * Reads participants from CSV file in resources directory
     * Expected format: ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType
     */
    public static List<Participant> readParticipantsFromCSV(String filename)
            throws FileProcessingException {

        List<Participant> participants = new ArrayList<>();
        int skippedLines = 0;

        InputStream inputStream = CSVFileHandler.class.getClassLoader()
                .getResourceAsStream(filename);

        if (inputStream == null) {
            String resourcePath = getResourcePath(filename);
            Path path = Paths.get(resourcePath);

            if (!Files.exists(path)) {
                throw new FileProcessingException("File not found: " + filename +
                        " (searched in resources and at: " + resourcePath + ")");
            }

            if (!Files.isReadable(path)) {
                throw new FileProcessingException("File is not readable: " + resourcePath);
            }

            try {
                inputStream = Files.newInputStream(path);
            } catch (IOException e) {
                throw new FileProcessingException("Error opening file: " + resourcePath, e);
            }
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int lineNumber = 0;
            String headerLine = reader.readLine();
            lineNumber++;

            if (headerLine == null) {
                throw new FileProcessingException("CSV file is empty");
            }

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    Participant participant = parseParticipantLine(line, lineNumber);
                    participants.add(participant);
                    LOGGER.fine("Parsed participant: " + participant.getName());

                } catch (DataValidationException e) {
                    skippedLines++;
                    LOGGER.warning("Skipping invalid line " + lineNumber + ": " + e.getMessage());
                } catch (Exception e) {
                    skippedLines++;
                    LOGGER.warning("Error parsing line " + lineNumber + ": " + line + " - " + e.getMessage());
                }
            }

            if (skippedLines > 0) {
                LOGGER.warning("WARNING: Skipped " + skippedLines + " invalid lines during import");
            }

        } catch (IOException e) {
            throw new FileProcessingException("Error reading file: " + filename, e);
        }

        if (participants.isEmpty()) {
            throw new FileProcessingException("No valid participants found in file. Check data format and validation rules.");
        }

        return participants;
    }

    /**
     * Parses a single CSV line into a Participant object
     * Format: ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType
     */
    private static Participant parseParticipantLine(String line, int lineNumber)
            throws DataValidationException {

        String[] fields = line.split(CSV_DELIMITER);

        if (fields.length < 7) {
            throw new DataValidationException(
                    "Line " + lineNumber + ": Expected at least 7 fields, found " + fields.length
            );
        }

        try {
            String participantId = fields[0].trim();
            String name = fields[1].trim();
            String email = fields[2].trim();
            String preferredGame = fields[3].trim();
            int skillLevel = Integer.parseInt(fields[4].trim());
            String preferredRole = fields[5].trim();
            int personalityScore = Integer.parseInt(fields[6].trim());

            return new Participant(participantId, name, email, preferredGame,
                    preferredRole, personalityScore, skillLevel);

        } catch (NumberFormatException e) {
            throw new DataValidationException(
                    "Line " + lineNumber + ": Invalid number format - " + e.getMessage()
            );
        } catch (IllegalArgumentException e) {
            throw new DataValidationException(
                    "Line " + lineNumber + ": Validation error - " + e.getMessage()
            );
        }
    }

    /**
     * Appends a new participant to allParticipants.csv
     * If file doesn't exist, creates it with header and all existing participants from sample file
     */
    public static void appendParticipantToAll(Participant participant) throws FileProcessingException {
        String allParticipantsPath = getResourcePath(ALL_PARTICIPANTS_FILE);
        Path path = Paths.get(allParticipantsPath);

        try {
            // If file doesn't exist, create it with header and copy all existing participants
            if (!Files.exists(path)) {
                createAllParticipantsFile();
            }

            // Append the new participant
            try (BufferedWriter writer = Files.newBufferedWriter(path,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(participant.toCSV());
                writer.newLine();
            }

            LOGGER.info("Participant " + participant.getParticipantId() + " added to " + ALL_PARTICIPANTS_FILE);

        } catch (IOException e) {
            throw new FileProcessingException("Error appending participant to file: " + allParticipantsPath, e);
        }
    }

    /**
     * Creates allParticipants.csv with header and copies existing participants from sample file
     */
    private static void createAllParticipantsFile() throws FileProcessingException {
        String allParticipantsPath = getResourcePath(ALL_PARTICIPANTS_FILE);
        Path path = Paths.get(allParticipantsPath);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            // Write header
            writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType");
            writer.newLine();

            // Copy all existing participants from sample file if it exists
            try {
                List<Participant> existingParticipants = readParticipantsFromCSV(SAMPLE_FILE);
                for (Participant p : existingParticipants) {
                    writer.write(p.toCSV());
                    writer.newLine();
                }
                LOGGER.info("Copied " + existingParticipants.size() + " participants from " + SAMPLE_FILE);
            } catch (FileProcessingException e) {
                LOGGER.info("No sample file found, starting with empty participant list");
            }

        } catch (IOException e) {
            throw new FileProcessingException("Error creating " + ALL_PARTICIPANTS_FILE, e);
        }
    }

    /**
     * Writes formed teams to CSV file in resources directory
     */
    public static void writeTeamsToCSV(List<Team> teams, String filename)
            throws FileProcessingException {

        String resourcePath = getResourcePath(filename);
        Path path = Paths.get(resourcePath);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            // Write header
            writer.write("TeamID,TeamName,ParticipantID,ParticipantName,Email,");
            writer.write("PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType");
            writer.newLine();

            // Write team data
            for (Team team : teams) {
                for (Participant member : team.getMembers()) {
                    writer.write(formatTeamMemberCSV(team, member));
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            throw new FileProcessingException("Error writing to file: " + resourcePath, e);
        }
    }

    /**
     * Formats team member data as CSV line
     */
    private static String formatTeamMemberCSV(Team team, Participant member) {
        return team.getTeamId() + "," +
                team.getTeamName() + "," +
                member.getParticipantId() + "," +
                member.getName() + "," +
                member.getEmail() + "," +
                member.getPreferredGame() + "," +
                member.getSkillLevel() + "," +
                member.getPreferredRole() + "," +
                member.getPersonalityScore() + "," +
                member.getPersonalityType().toString();
    }

    /**
     * Creates a sample CSV file with test data in resources directory
     */
    public static void createSampleCSV(String filename) throws FileProcessingException {
        String resourcePath = getResourcePath(filename);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(resourcePath))) {
            writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType");
            writer.newLine();

            // Sample data matching the format
            String[] sampleData = {
                    "P001,Alice Johnson,alice@university.edu,Valorant,8,Strategist,95,Leader",
                    "P002,Bob Smith,bob@university.edu,FIFA,7,Attacker,72,Balanced",
                    "P003,Charlie Lee,charlie@university.edu,DOTA 2,6,Defender,88,Balanced",
                    "P004,Diana Prince,diana@university.edu,CS:GO,9,Supporter,65,Thinker",
                    "P005,Eve Adams,eve@university.edu,Basketball,7,Coordinator,78,Balanced",
                    "P006,Frank Zhang,frank@university.edu,Valorant,8,Strategist,92,Leader",
                    "P007,Grace Kim,grace@university.edu,FIFA,6,Attacker,58,Thinker",
                    "P008,Henry Davis,henry@university.edu,Chess,7,Defender,81,Balanced",
                    "P009,Iris Wilson,iris@university.edu,DOTA 2,8,Supporter,69,Thinker",
                    "P010,Jack Brown,jack@university.edu,CS:GO,9,Coordinator,91,Leader",
                    "P011,Kate Miller,kate@university.edu,Basketball,7,Strategist,74,Balanced",
                    "P012,Leo Garcia,leo@university.edu,FIFA,8,Attacker,86,Balanced"
            };

            for (String data : sampleData) {
                writer.write(data);
                writer.newLine();
            }

        } catch (IOException e) {
            throw new FileProcessingException("Error creating sample file: " + resourcePath, e);
        }
    }

    /**
     * Validates CSV file format
     */
    public static boolean validateCSVFormat(String filename) {
        try {
            InputStream inputStream = CSVFileHandler.class.getClassLoader()
                    .getResourceAsStream(filename);

            if (inputStream == null) {
                String resourcePath = getResourcePath(filename);
                Path path = Paths.get(resourcePath);
                if (!Files.exists(path)) {
                    return false;
                }
                inputStream = Files.newInputStream(path);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String header = reader.readLine();
                if (header == null) {
                    return false;
                }

                // Check if header contains required fields
                return header.contains("ID") &&
                        header.contains("Name") &&
                        header.contains("PreferredGame") &&
                        header.contains("PersonalityScore");
            }

        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Gets the next available participant ID
     */
    public static String getNextParticipantId() throws FileProcessingException {
        int maxId = 0;

        try {
            List<Participant> participants = loadParticipantsAutomatically();
            for (Participant p : participants) {
                String id = p.getParticipantId();
                if (id.startsWith("P")) {
                    try {
                        int num = Integer.parseInt(id.substring(1));
                        if (num > maxId) {
                            maxId = num;
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid IDs
                    }
                }
            }
        } catch (FileProcessingException e) {
            // No existing participants, start from 0
        }

        return String.format("P%03d", maxId + 1);
    }
}