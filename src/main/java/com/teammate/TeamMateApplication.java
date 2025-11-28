package com.teammate;

import com.teammate.model.*;
import com.teammate.service.*;
import com.teammate.util.*;
import com.teammate.exception.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.*;

/**
 * Main application class for TeamMate System
 */
public class TeamMateApplication {
    private static final Logger LOGGER = Logger.getLogger(TeamMateApplication.class.getName());
    private static final String OUTPUT_FILE = "formed_teams.csv";
    private static final String LOG_FILE = "teammate_application.log";
    private static final int DEFAULT_TEAM_SIZE = 5;
    private static final int MIN_TEAM_SIZE = 3;

    private final Scanner scanner;
    private final TeamBuilder teamBuilder;
    private final List<Participant> manualParticipants;
    private String userRole; // "MANAGEMENT" or "PARTICIPANT"

    private static final String[] SURVEY_QUESTIONS = {
            "I enjoy taking the lead and guiding others during group activities.",
            "I prefer analyzing situations and coming up with strategic solutions.",
            "I work well with others and enjoy collaborative teamwork.",
            "I am calm under pressure and can help maintain team morale.",
            "I like making quick decisions and adapting in dynamic situations."
    };

    public TeamMateApplication() {
        this.scanner = new Scanner(System.in);
        this.teamBuilder = new TeamBuilder();
        this.manualParticipants = new ArrayList<>();
        configureLogging();
    }

    /**
     * Configures logging to write to both console and file
     */
    private void configureLogging() {
        try {
            String resourcePath = "src/main/resources/" + LOG_FILE;
            Files.createDirectories(Paths.get("src/main/resources"));

            Logger rootLogger = Logger.getLogger("");
            rootLogger.setLevel(Level.INFO);

            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(consoleHandler);

            FileHandler fileHandler = new FileHandler(resourcePath, true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);

            LOGGER.info("TeamMate Application started - Logging to " + resourcePath);

        } catch (IOException e) {
            System.err.println("Failed to configure file logging: " + e.getMessage());
            System.err.println("Continuing with console logging only");
        }
    }

    public static void main(String[] args) {
        TeamMateApplication app = new TeamMateApplication();
        try {
            app.run();
        } catch (Exception e) {
            LOGGER.severe("Application error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            app.cleanup();
        }
    }

    /** Runs the main application loop, handling user role checks and menu operations.
     *  Manages authorized actions, logs activity, and exits when requested. */

    public void run() {
        printWelcomeBanner();

        // User role verification
        if (!verifyUserRole()) {
            System.out.println("\nExiting application.");
            return;
        }

        LOGGER.info("Application main menu loaded for user role: " + userRole);
        boolean running = true;
        while (running) {
            try {
                printMainMenu();
                int choice = getUserChoice();
                LOGGER.info("User selected menu option: " + choice);

                if (!isAuthorizedForOption(choice)) {
                    System.out.println("\nAccess Denied: You do not have permission for this option.");
                    LOGGER.warning("Unauthorized access attempt to option " + choice + " by " + userRole);
                    continue;
                }

                switch (choice) {
                    case 1: loadAndFormTeams(); break;
                    case 2: createSampleData(); break;
                    case 3: displayStatistics(); break;
                    case 4: manualParticipantEntry(); break;
                    case 5: formTeamsFromManualEntry(); break;
                    case 6:
                        running = false;
                        LOGGER.info("User exited application");
                        System.out.println("\nThank you for using TeamMate!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        LOGGER.warning("Invalid menu choice: " + choice);
                }
            } catch (Exception e) {
                LOGGER.warning("Error in main loop: " + e.getMessage());
                System.out.println("\nAn error occurred. Please try again.");
            }
        }
    }

    /**
     * Verifies user role at application startup
     */
    private boolean verifyUserRole() {
        System.out.println("\n======= USER VERIFICATION =======");
        System.out.println("Please select your role:");
        System.out.println("1. Management");
        System.out.println("2. Participant");
        System.out.println("3. Exit");
        System.out.print("Enter your choice (1-3): ");

        int choice = getUserChoice();

        switch (choice) {
            case 1:
                userRole = "MANAGEMENT";
                System.out.println("\nWelcome, Management User!");
                LOGGER.info("User logged in as MANAGEMENT");
                return true;
            case 2:
                userRole = "PARTICIPANT";
                System.out.println("\nWelcome, Participant!");
                LOGGER.info("User logged in as PARTICIPANT");
                return true;
            case 3:
                return false;
            default:
                System.out.println("Invalid choice. Please restart the application.");
                return false;
        }
    }

    /**
     * Checks if user is authorized for a menu option
     */
    private boolean isAuthorizedForOption(int option) {
        if (userRole.equals("MANAGEMENT")) {
            return true; // Management has access to all options
        }

        // Participant can only access option 4 (Manual Entry) and 6 (Exit)
        return option == 4 || option == 6;
    }

    private void printWelcomeBanner() {
        System.out.println("============================================================");
        System.out.println("           TEAMMATE - Team Formation System                 ");
        System.out.println("     Intelligent Balanced Team Creation for Gaming Club     ");
        System.out.println("============================================================\n");
    }

    private void printMainMenu() {
        System.out.println("\n================ MAIN MENU ================");
        System.out.println("Current Role: " + userRole);
        System.out.println("-------------------------------------------");

        if (userRole.equals("MANAGEMENT")) {
            System.out.println("1. Load Participants and Form Teams");
            System.out.println("2. Create Sample Data File");
            System.out.println("3. Display Team Statistics");
        } else {
            System.out.println("1. [Management Only]");
            System.out.println("2. [Management Only]");
            System.out.println("3. [Management Only]");
        }

        System.out.println("4. Manual Participant Entry");

        if (userRole.equals("MANAGEMENT")) {
            System.out.println("5. Form Teams from Manual Entries");
        } else {
            System.out.println("5. [Management Only]");
        }

        System.out.println("6. Exit");
        System.out.println("===========================================");
        System.out.print("Enter your choice: ");
    }
    /** Reads user input and converts it to an integer.
     *  Returns -1 if the input is invalid or not a number. */

    private int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    /** Loads participant data automatically and forms balanced teams with error handling.
     *  Handles team size validation, CSV saving, and user decisions during failures. */

    private void loadAndFormTeams() {
        System.out.println("\n======= TEAM FORMATION =======");
        LOGGER.info("Starting team formation process");

        boolean formationSuccessful = false;

        while (!formationSuccessful) {
            try {
                System.out.println("\nLoading participants automatically...");
                System.out.println("(Checks allParticipants.csv first, then participants_sample.csv)");

                List<Participant> participants = CSVFileHandler.loadParticipantsAutomatically();
                System.out.println("Successfully loaded " + participants.size() + " participants");
                LOGGER.info("Loaded " + participants.size() + " participants");

                int teamSize = getValidatedTeamSize(participants.size());
                LOGGER.info("Team size selected: " + teamSize);

                System.out.println("\nForming balanced teams...");
                long startTime = System.currentTimeMillis();
                List<Team> teams = teamBuilder.formTeams(participants, teamSize);
                long endTime = System.currentTimeMillis();

                System.out.println("Teams formed successfully in " + (endTime - startTime) + "ms");
                LOGGER.info("Teams formed: " + teams.size() + " teams in " + (endTime - startTime) + "ms");
                displayTeams(teams);

                System.out.print("\nSave teams to CSV? (y/n): ");
                if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    System.out.print("Enter output filename (or press Enter for '" + OUTPUT_FILE + "'): ");
                    String outputName = scanner.nextLine().trim();
                    if (outputName.isEmpty()) outputName = OUTPUT_FILE;
                    CSVFileHandler.writeTeamsToCSV(teams, outputName);
                    System.out.println("Teams saved to src/main/resources/" + outputName);
                    LOGGER.info("Teams saved to " + outputName);
                }

                formationSuccessful = true;

            } catch (FileProcessingException e) {
                System.out.println("\nFile Error: " + e.getMessage());
                LOGGER.severe("File processing error: " + e.getMessage());
                formationSuccessful = true;

            } catch (TeamFormationException e) {
                System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                System.out.println("║             TEAM FORMATION ERROR                             ║");
                System.out.println("╚═══════════════════════════════════════════════════════════╝");
                System.out.println("\n" + e.getMessage());
                System.out.println("\n────────────────────────────────────────────────────────────");
                LOGGER.severe("Team formation error: " + e.getMessage());

                System.out.print("\nWould you like to:\n");
                System.out.println("  1. Try a different team size");
                System.out.println("  2. Return to main menu");
                System.out.print("Enter choice (1-2): ");

                int choice = getUserChoice();
                if (choice != 1) {
                    formationSuccessful = true;
                }

            } catch (Exception e) {
                System.out.println("\nUnexpected error: " + e.getMessage());
                LOGGER.log(Level.SEVERE, "Unexpected error in loadAndFormTeams", e);
                formationSuccessful = true;
            }
        }
    }

    private int getValidatedTeamSize(int totalParticipants) {
        while (true) {
            try {
                System.out.print("\nEnter desired team size (minimum " + MIN_TEAM_SIZE + ", default " + DEFAULT_TEAM_SIZE + "): ");
                String input = scanner.nextLine().trim();

                int teamSize = input.isEmpty() ? DEFAULT_TEAM_SIZE : Integer.parseInt(input);

                if (teamSize < MIN_TEAM_SIZE) {
                    System.out.println("Team size must be at least " + MIN_TEAM_SIZE);
                    LOGGER.warning("Invalid team size entered: " + teamSize);
                    continue;
                }

                if (InputValidator.isValidTeamSize(teamSize, totalParticipants)) {
                    return teamSize;
                } else {
                    System.out.println("Invalid team size. Must be between " + MIN_TEAM_SIZE + " and " + totalParticipants);
                    System.out.println("Using default: " + DEFAULT_TEAM_SIZE);
                    LOGGER.warning("Invalid team size, using default: " + DEFAULT_TEAM_SIZE);
                    return DEFAULT_TEAM_SIZE;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Using default: " + DEFAULT_TEAM_SIZE);
                LOGGER.warning("Non-numeric team size input, using default");
                return DEFAULT_TEAM_SIZE;
            }
        }
    }

    private void createSampleData() {
        System.out.println("\n======= CREATE SAMPLE DATA =======");
        LOGGER.info("Creating sample data file");
        try {
            String filename = "participants_sample.csv";
            CSVFileHandler.createSampleCSV(filename);
            System.out.println("Sample data file created: src/main/resources/" + filename);
            LOGGER.info("Sample data file created successfully");
            System.out.println("\nContains 12 participants with:");
            System.out.println("  - Games: Valorant, FIFA, DOTA 2, CS:GO, Basketball, Chess");
            System.out.println("  - Roles: Strategist, Attacker, Defender, Supporter, Coordinator");
            System.out.println("  - Personality Types: Leader, Balanced, Thinker");
            System.out.println("  - Skill Levels: 6-9");
        } catch (FileProcessingException e) {
            System.out.println("\nError: " + e.getMessage());
            LOGGER.severe("Error creating sample data: " + e.getMessage());
        }
    }

    private void displayStatistics() {
        System.out.println("\n======= TEAM STATISTICS =======");
        LOGGER.info("Displaying team statistics");
        try {
            System.out.print("Enter formed teams CSV filename (or press Enter for '" + OUTPUT_FILE + "'): ");
            String fileName = scanner.nextLine().trim();
            if (fileName.isEmpty()) fileName = OUTPUT_FILE;

            Map<String, Team> teamsMap = parseFormedTeamsFile(fileName);
            if (teamsMap.isEmpty()) {
                System.out.println("\nNo teams found. Please form teams first (Option 1 or 5).");
                LOGGER.warning("No teams found in file: " + fileName);
                return;
            }
            displayDetailedStatistics(new ArrayList<>(teamsMap.values()));
            LOGGER.info("Statistics displayed for " + teamsMap.size() + " teams");
        } catch (FileProcessingException e) {
            System.out.println("\nFile Error: " + e.getMessage());
            LOGGER.severe("Error reading statistics file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            LOGGER.severe("Error displaying statistics: " + e.getMessage());
        }
    }

    private void manualParticipantEntry() {
        System.out.println("\n======= MANUAL PARTICIPANT ENTRY =======");
        System.out.println("Current participants: " + manualParticipants.size());
        LOGGER.info("Manual participant entry mode - Current count: " + manualParticipants.size());

        boolean entering = true;
        while (entering) {
            System.out.println("\n1. Add New Participant");
            System.out.println("2. View Current Participants");

            if (userRole.equals("MANAGEMENT")) {
                System.out.println("3. Clear All Participants");
            } else {
                System.out.println("3. [Management Only]");
            }

            System.out.println("4. Return to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getUserChoice();

            // Check authorization for option 3
            if (choice == 3 && userRole.equals("PARTICIPANT")) {
                System.out.println("\nAccess Denied: Only Management can clear participants.");
                continue;
            }

            switch (choice) {
                case 1: addManualParticipant(); break;
                case 2: viewManualParticipants(); break;
                case 3:
                    manualParticipants.clear();
                    System.out.println("All participants cleared.");
                    LOGGER.info("All manual participants cleared");
                    break;
                case 4:
                    entering = false;
                    LOGGER.info("Exiting manual participant entry mode");
                    break;
                default: System.out.println("Invalid choice.");
            }
        }
    }
    /** Collects and validates user-entered participant details, including survey and skill data.
     *  Creates the participant object, logs it, and optionally saves it to CSV. */

    private void addManualParticipant() {
        System.out.println("\n--- Add New Participant ---");
        LOGGER.info("Adding new manual participant");
        try {
            String id = CSVFileHandler.getNextParticipantId();
            System.out.println("Auto-generated ID: " + id);

            String name = getValidatedInput("Name: ",
                    InputValidator::isValidName,
                    InputValidator.getNameErrorMessage());

            String email = getValidatedInput("Email: ",
                    InputValidator::isValidEmail,
                    InputValidator.getEmailErrorMessage());

            System.out.println("Valid games: " + String.join(", ", InputValidator.getValidGames()));
            String game = getValidatedInput("Preferred Game: ",
                    InputValidator::isValidGame,
                    InputValidator.getGameErrorMessage());

            int skill = getValidatedIntInput("Skill Level (1-10): ",
                    input -> InputValidator.isValidSkillLevel(Integer.parseInt(input)),
                    InputValidator.getSkillLevelErrorMessage());

            System.out.println("Valid roles: " + String.join(", ", InputValidator.getValidRoles()));
            String role = getValidatedInput("Preferred Role: ",
                    InputValidator::isValidRole,
                    InputValidator.getRoleErrorMessage());

            System.out.println("\n=== PERSONALITY SURVEY ===");
            System.out.println("Rate each statement from 1 (Strongly Disagree) to 5 (Strongly Agree)");
            int[] surveyAnswers = new int[5];

            for (int i = 0; i < SURVEY_QUESTIONS.length; i++) {
                System.out.println("\nQ" + (i + 1) + ": " + SURVEY_QUESTIONS[i]);
                surveyAnswers[i] = getValidatedIntInput("Your rating (1-5): ",
                        input -> {
                            int val = Integer.parseInt(input);
                            return val >= 1 && val <= 5;
                        },
                        "Rating must be between 1 and 5");
            }

            Participant participant = new Participant(id, name, email, game, role, surveyAnswers, skill);

            System.out.println("\n=== PERSONALITY ASSESSMENT ===");
            System.out.println("Personality Score: " + participant.getPersonalityScore());
            System.out.println("Personality Type: " + participant.getPersonalityType());
            System.out.println("Description: " + PersonalityClassifier.getPersonalityDescription(participant.getPersonalityScore()));

            LOGGER.info("New participant created: " + id + " - " + name +
                    " (Type: " + participant.getPersonalityType() + ", Score: " + participant.getPersonalityScore() + ")");

            manualParticipants.add(participant);

            System.out.print("\nSave participant to allParticipants.csv? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                CSVFileHandler.appendParticipantToAll(participant);
                System.out.println("Participant saved to allParticipants.csv");
                LOGGER.info("Participant " + id + " saved to allParticipants.csv");
            }

            System.out.println("\nParticipant added successfully!");
            System.out.println("Total manual participants: " + manualParticipants.size());

        } catch (Exception e) {
            System.out.println("Error adding participant: " + e.getMessage());
            LOGGER.severe("Error adding manual participant: " + e.getMessage());
        }
    }

    private String getValidatedInput(String prompt, Predicate<String> validator, String errorMsg) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (validator.test(input)) {
                return input;
            }
            System.out.println("Error: " + errorMsg);
        }
    }

    private int getValidatedIntInput(String prompt, Predicate<String> validator, String errorMsg) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                if (validator.test(input)) {
                    return Integer.parseInt(input);
                }
                System.out.println("Error: " + errorMsg);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number");
            }
        }
    }

    private void viewManualParticipants() {
        if (manualParticipants.isEmpty()) {
            System.out.println("\nNo participants entered yet.");
            return;
        }
        System.out.println("\n======= MANUALLY ENTERED PARTICIPANTS =======");
        for (int i = 0; i < manualParticipants.size(); i++) {
            System.out.println((i + 1) + ". " + manualParticipants.get(i).toString());
        }
        System.out.println("Total: " + manualParticipants.size());
        LOGGER.info("Displayed " + manualParticipants.size() + " manual participants");
    }

    private void formTeamsFromManualEntry() {
        System.out.println("\n======= FORM TEAMS FROM MANUAL ENTRIES =======");
        LOGGER.info("Forming teams from manual entries");

        if (manualParticipants.isEmpty()) {
            System.out.println("No participants. Please use Option 4 to add participants.");
            LOGGER.warning("No manual participants available for team formation");
            return;
        }

        System.out.println("Total participants: " + manualParticipants.size());

        boolean formationSuccessful = false;

        while (!formationSuccessful) {
            try {
                int teamSize = getValidatedTeamSize(manualParticipants.size());

                System.out.println("\nForming balanced teams...");
                long startTime = System.currentTimeMillis();
                List<Team> teams = teamBuilder.formTeams(new ArrayList<>(manualParticipants), teamSize);
                long endTime = System.currentTimeMillis();

                System.out.println("Teams formed successfully in " + (endTime - startTime) + "ms");
                LOGGER.info("Manual entry teams formed: " + teams.size() + " teams in " + (endTime - startTime) + "ms");
                displayTeams(teams);

                System.out.print("\nSave teams to CSV? (y/n): ");
                if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    System.out.print("Enter output filename (or press Enter for 'manual_teams.csv'): ");
                    String outputName = scanner.nextLine().trim();
                    if (outputName.isEmpty()) outputName = "manual_teams.csv";
                    CSVFileHandler.writeTeamsToCSV(teams, outputName);
                    System.out.println("Teams saved to src/main/resources/" + outputName);
                    LOGGER.info("Manual teams saved to " + outputName);
                }

                formationSuccessful = true;

            } catch (TeamFormationException e) {
                System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                System.out.println("║             TEAM FORMATION ERROR                             ║");
                System.out.println("╚═══════════════════════════════════════════════════════════╝");
                System.out.println("\n" + e.getMessage());
                System.out.println("\n────────────────────────────────────────────────────────────");
                LOGGER.severe("Manual team formation error: " + e.getMessage());

                System.out.print("\nWould you like to:\n");
                System.out.println("  1. Try a different team size");
                System.out.println("  2. Return to main menu");
                System.out.print("Enter choice (1-2): ");

                int choice = getUserChoice();
                if (choice != 1) {
                    formationSuccessful = true;
                }

            } catch (Exception e) {
                System.out.println("\nUnexpected error: " + e.getMessage());
                LOGGER.log(Level.SEVERE, "Unexpected error in manual team formation", e);
                formationSuccessful = true;
            }
        }
    }

    private Map<String, Team> parseFormedTeamsFile(String fileName) throws FileProcessingException {
        Map<String, Team> teamsMap = new HashMap<>();
        java.io.InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            String resourcePath = "src/main/resources/" + fileName;
            java.nio.file.Path path = java.nio.file.Paths.get(resourcePath);
            if (!java.nio.file.Files.exists(path)) {
                throw new FileProcessingException("File not found: " + fileName);
            }
            try {
                inputStream = java.nio.file.Files.newInputStream(path);
            } catch (java.io.IOException e) {
                throw new FileProcessingException("Error reading file: " + fileName, e);
            }
        }

        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",");
                if (fields.length < 10) continue;

                try {
                    String teamId = fields[0].trim();
                    String teamName = fields[1].trim();
                    String participantId = fields[2].trim();
                    String participantName = fields[3].trim();
                    String email = fields[4].trim();
                    String preferredGame = fields[5].trim();
                    int skillLevel = Integer.parseInt(fields[6].trim());
                    String role = fields[7].trim();
                    int personalityScore = Integer.parseInt(fields[8].trim());

                    Team team = teamsMap.get(teamId);
                    if (team == null) {
                        team = new Team(teamId, teamName, 10);
                        teamsMap.put(teamId, team);
                    }

                    Participant participant = new Participant(
                            participantId, participantName, email, preferredGame,
                            role, personalityScore, skillLevel
                    );
                    team.addMember(participant);
                } catch (Exception e) {
                    LOGGER.warning("Error parsing line: " + e.getMessage());
                }
            }
        } catch (java.io.IOException e) {
            throw new FileProcessingException("Error parsing file: " + fileName, e);
        }
        return teamsMap;
    }

    private void displayDetailedStatistics(List<Team> teams) {
        System.out.println("\n============================================================");
        System.out.println("                  DETAILED TEAM STATISTICS                  ");
        System.out.println("============================================================");

        int totalParticipants = teams.stream().mapToInt(Team::getCurrentSize).sum();
        double avgTeamSize = teams.stream().mapToInt(Team::getCurrentSize).average().orElse(0);
        double avgDiversity = teams.stream().mapToDouble(Team::calculateDiversityScore).average().orElse(0);
        double avgBalance = teams.stream().mapToDouble(Team::calculateBalanceScore).average().orElse(0);
        double avgSkill = teams.stream().mapToDouble(Team::getAverageSkillLevel).average().orElse(0);

        System.out.println("\nOVERALL STATISTICS:");
        System.out.println("  Total Teams: " + teams.size());
        System.out.println("  Total Participants: " + totalParticipants);
        System.out.println("  Average Team Size: " + formatDouble(avgTeamSize, 1));
        System.out.println("  Average Diversity: " + formatDouble(avgDiversity, 2));
        System.out.println("  Average Balance: " + formatDouble(avgBalance, 2));
        System.out.println("  Average Skill: " + formatDouble(avgSkill, 2));

        System.out.println("\nINDIVIDUAL TEAM BREAKDOWN:");
        System.out.println("------------------------------------------------------------");

        for (Team team : teams) {
            System.out.println("\n" + team.getTeamName() + " (" + team.getTeamId() + ")");
            System.out.println("  Members: " + team.getCurrentSize());
            System.out.println("  Diversity: " + formatDouble(team.calculateDiversityScore(), 2));
            System.out.println("  Balance: " + formatDouble(team.calculateBalanceScore(), 2));
            System.out.println("  Avg Skill: " + formatDouble(team.getAverageSkillLevel(), 2));
            System.out.println("  Role Diversity: " + (team.hasRoleDiversity() ? "Yes" : "No"));
            System.out.println("  Game Variety: " + (team.hasGameVariety() ? "Yes" : "No"));

            long leaders = team.getMembers().stream()
                    .filter(p -> p.getPersonalityType() == PersonalityType.LEADER).count();
            long balanced = team.getMembers().stream()
                    .filter(p -> p.getPersonalityType() == PersonalityType.BALANCED).count();
            long thinkers = team.getMembers().stream()
                    .filter(p -> p.getPersonalityType() == PersonalityType.THINKER).count();

            System.out.println("  Personality: " + leaders + " Leaders, " +
                    balanced + " Balanced, " + thinkers + " Thinkers");
        }
        System.out.println("\n------------------------------------------------------------");
    }

    private void displayTeams(List<Team> teams) {
        System.out.println("\n============================================================");
        System.out.println("                    FORMED TEAMS                            ");
        System.out.println("============================================================");

        for (Team team : teams) {
            System.out.println("\n" + team.getDetailedSummary());
            System.out.println("------------------------------------------------------------");
        }

        System.out.println("\n============== OVERALL STATISTICS ==============");
        System.out.println("  Total Teams: " + teams.size());
        System.out.println("  Avg Team Size: " + formatDouble(
                teams.stream().mapToInt(Team::getCurrentSize).average().orElse(0), 1));
        System.out.println("  Avg Diversity: " + formatDouble(
                teams.stream().mapToDouble(Team::calculateDiversityScore).average().orElse(0), 2));
        System.out.println("  Avg Balance: " + formatDouble(
                teams.stream().mapToDouble(Team::calculateBalanceScore).average().orElse(0), 2));
        System.out.println("============================================================");
    }

    private String formatDouble(double value, int decimals) {
        if (decimals == 1) {
            return String.format("%.1f", value);
        } else if (decimals == 2) {
            return String.format("%.2f", value);
        }
        return String.valueOf(value);
    }
    private void cleanup() {
        if (scanner != null) scanner.close();
        LOGGER.info("Application shutdown complete");
    }
}
