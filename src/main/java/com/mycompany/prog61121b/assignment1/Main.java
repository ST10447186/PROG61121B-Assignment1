package com.mycompany.prog61121b.assignment1;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*
 * PROG61121B - Assignment 1
 * MediCare Hospital Patient Admission System
 *
 * Author:         Mikyle Daniel Naraidu
 * Student number: ST10447186
 * Date:          08 September 2026
 *
 * References:
 * Oracle, 2024. Class Scanner. Java Platform SE API Specification. [Online]
 *     Available at: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Scanner.html
 *     [Accessed 06 September 2026].
 * Oracle, 2024. The switch Statement. The Java Tutorials. [Online]
 *     Available at: https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html
 *     [Accessed 06 September 2026].
 * Oracle, 2024. Catching and Handling Exceptions. The Java Tutorials. [Online]
 *     Available at: https://docs.oracle.com/javase/tutorial/essential/exceptions/handling.html
 *     [Accessed 06 September 2026].
 *
 * The Independent Institute of Education, 2026. PROG61121B Module Manual.
 *     Johannesburg: The Independent Institute of Education.
 */

import com.mycompany.prog61121b.assignment1.Inpatient;
import com.mycompany.prog61121b.assignment1.HospitalSystem;
import com.mycompany.prog61121b.assignment1.DuplicatePatientException;
import com.mycompany.prog61121b.assignment1.BedUnavailableException;
import java.util.Scanner;

/**
 * Console entry point for the MediCare Hospital Patient Admission System.
 *
 * This class is responsible for all console input and output. It reads the
 * user's choices, calls HospitalSystem and Ward, and prints the results or the
 * error messages carried by the exceptions those classes throw.
 *
 * All input is read with nextLine() and converted afterwards, which avoids the
 * leftover-newline problem caused by mixing nextInt() and nextLine().
 */
public class Main {

    private static final int WARD_NUMBER = 1;
    private static final String LINE =
            "==================================================";

    private static final Scanner INPUT = new Scanner(System.in);
    private static final HospitalSystem SYSTEM = new HospitalSystem(WARD_NUMBER);

    public static void main(String[] args) {
        printHeader("MEDICARE HOSPITAL - PATIENT ADMISSION SYSTEM");
        System.out.println("Ward " + WARD_NUMBER + " | " + Ward.TOTAL_BEDS + " beds");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    patientMenu();
                    break;
                case 2:
                    bedMenu();
                    break;
                case 3:
                    reportMenu();
                    break;
                case 0:
                    running = false;
                    System.out.println("\nShutting down. Goodbye.");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 0, 1, 2 or 3.");
            }
        }
        INPUT.close();
    }

    // ================= MAIN MENU =========================================

    private static void printMainMenu() {
        System.out.println("\n" + LINE);
        System.out.println("MAIN MENU");
        System.out.println(LINE);
        System.out.println("1. Patient Management");
        System.out.println("2. Bed Management");
        System.out.println("3. Reports");
        System.out.println("0. Exit");
        System.out.println(LINE);
    }

    // ================= PATIENT MANAGEMENT ================================

    private static void patientMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + LINE);
            System.out.println("PATIENT MANAGEMENT");
            System.out.println(LINE);
            System.out.println("1. Register a new patient");
            System.out.println("2. Search for a patient");
            System.out.println("3. Update a patient's details");
            System.out.println("4. Delete a patient");
            System.out.println("5. Display all patients");
            System.out.println("0. Back to main menu");
            System.out.println(LINE);

            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1: registerPatient(); break;
                case 2: searchPatient();   break;
                case 3: updatePatient();   break;
                case 4: deletePatient();   break;
                case 5: System.out.println("\n" + SYSTEM.getPatientReport()); break;
                case 0: back = true; break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void registerPatient() {
        printHeader("REGISTER NEW PATIENT");
        try {
            String patientId = readText("Patient ID          : ");
            if (SYSTEM.patientExists(patientId)) {
                System.out.println("A patient with ID " + patientId.toUpperCase()
                        + " is already registered. Registration cancelled.");
                return;
            }

            String firstName = readText("First name          : ");
            String lastName = readText("Last name           : ");
            int age = readInt("Age                 : ");
            String gender = readText("Gender              : ");
            String condition = readText("Medical condition   : ");

            System.out.println("Category: 1 = Inpatient, 2 = Outpatient, 3 = Emergency");
            PatientCategory category = PatientCategory.fromChoice(
                    readInt("Choose category     : "));

            Patient patient;
            if (category == PatientCategory.INPATIENT) {
                patient = new Inpatient(patientId, firstName, lastName, age,
                        gender, condition, WARD_NUMBER);
            } else {
                patient = new Patient(patientId, firstName, lastName, age,
                        gender, condition, category);
            }

            SYSTEM.registerPatient(patient);
            System.out.println("\nPatient registered successfully.");
            patient.displayDetails();

            if (patient instanceof Inpatient) {
                offerBedAllocation((Inpatient) patient);
            }

        } catch (DuplicatePatientException e) {
            System.out.println("Registration failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private static void offerBedAllocation(Inpatient inpatient) {
        if (SYSTEM.getWard().isFull()) {
            System.out.println("\nNote: the ward is full, so no bed could be offered.");
            return;
        }
        String answer = readText("\nAllocate a bed now? (Y/N): ");
        if (!answer.equalsIgnoreCase("Y")) {
            return;
        }
        try {
            int bedNumber = SYSTEM.allocateFirstAvailableBed(inpatient.getPatientId());
            System.out.println("Bed " + Ward.bedLabel(bedNumber)
                    + " allocated to " + inpatient.getPatientId() + ".");
        } catch (PatientNotFoundException | BedUnavailableException e) {
            System.out.println("Bed allocation failed: " + e.getMessage());
        }
    }

    private static void searchPatient() {
        printHeader("SEARCH FOR A PATIENT");
        try {
            Patient patient = SYSTEM.findPatient(readText("Patient ID: "));
            patient.displayDetails();
        } catch (PatientNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private static void updatePatient() {
        printHeader("UPDATE PATIENT DETAILS");
        try {
            String patientId = readText("Patient ID: ");
            Patient existing = SYSTEM.findPatient(patientId);

            System.out.println("\nCurrent details:");
            existing.displayDetails();
            System.out.println("\nEnter the new details:");

            String firstName = readText("First name          : ");
            String lastName = readText("Last name           : ");
            int age = readInt("Age                 : ");
            String gender = readText("Gender              : ");
            String condition = readText("Medical condition   : ");

            SYSTEM.updatePatient(patientId, firstName, lastName, age, gender, condition);
            System.out.println("\nPatient updated successfully.");
            SYSTEM.findPatient(patientId).displayDetails();

        } catch (PatientNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    private static void deletePatient() {
        printHeader("DELETE A PATIENT");
        try {
            String patientId = readText("Patient ID: ");
            Patient patient = SYSTEM.findPatient(patientId);
            patient.displayDetails();

            String answer = readText("\nDelete this patient permanently? (Y/N): ");
            if (!answer.equalsIgnoreCase("Y")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            SYSTEM.deletePatient(patientId);
            System.out.println("Patient " + patientId.toUpperCase()
                    + " deleted. Any bed held has been released.");

        } catch (PatientNotFoundException | BedUnavailableException e) {
            System.out.println("Deletion failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    // ================= BED MANAGEMENT ====================================

    private static void bedMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + LINE);
            System.out.println("BED MANAGEMENT");
            System.out.println(LINE);
            System.out.println("1. Allocate a specific bed");
            System.out.println("2. Allocate the first available bed");
            System.out.println("3. Release a bed (discharge)");
            System.out.println("4. Display the ward layout");
            System.out.println("5. Display available beds");
            System.out.println("6. Display occupied beds");
            System.out.println("0. Back to main menu");
            System.out.println(LINE);

            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1: allocateSpecificBed(); break;
                case 2: allocateNextBed();     break;
                case 3: releaseBed();          break;
                case 4: System.out.println("\n" + SYSTEM.getWard().getLayout()); break;
                case 5: System.out.println("\n" + SYSTEM.getWard().getAvailableBedsReport()); break;
                case 6: System.out.println("\n" + SYSTEM.getWard().getOccupiedBedsReport()); break;
                case 0: back = true; break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void allocateSpecificBed() {
        printHeader("ALLOCATE A SPECIFIC BED");
        System.out.println(SYSTEM.getWard().getLayout());
        try {
            String patientId = readText("\nPatient ID : ");
            int bedNumber = readInt("Bed number (1-" + Ward.TOTAL_BEDS + ") : ");

            SYSTEM.allocateBed(patientId, bedNumber);
            System.out.println("Bed " + Ward.bedLabel(bedNumber)
                    + " allocated to " + patientId.toUpperCase() + ".");

        } catch (PatientNotFoundException | BedUnavailableException e) {
            System.out.println("Allocation failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private static void allocateNextBed() {
        printHeader("ALLOCATE THE FIRST AVAILABLE BED");
        try {
            String patientId = readText("Patient ID: ");
            int bedNumber = SYSTEM.allocateFirstAvailableBed(patientId);
            System.out.println("Bed " + Ward.bedLabel(bedNumber)
                    + " allocated to " + patientId.toUpperCase() + ".");

        } catch (PatientNotFoundException | BedUnavailableException e) {
            System.out.println("Allocation failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private static void releaseBed() {
        printHeader("RELEASE A BED (DISCHARGE)");
        try {
            String patientId = readText("Patient ID: ");
            int bedNumber = SYSTEM.releaseBed(patientId);
            System.out.println("Bed " + Ward.bedLabel(bedNumber)
                    + " released. It is now available.");

        } catch (PatientNotFoundException | BedUnavailableException e) {
            System.out.println("Release failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    // ================= REPORTS ===========================================

    private static void reportMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + LINE);
            System.out.println("REPORTS");
            System.out.println(LINE);
            System.out.println("1. All registered patients (sorted by surname)");
            System.out.println("2. All registered patients (sorted by Patient ID)");
            System.out.println("3. Available beds");
            System.out.println("4. Occupied beds");
            System.out.println("5. Ward occupancy report");
            System.out.println("6. Full ward summary");
            System.out.println("0. Back to main menu");
            System.out.println(LINE);

            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1: System.out.println("\n" + SYSTEM.getPatientReport()); break;
                case 2: printSortedById(); break;
                case 3: System.out.println("\n" + SYSTEM.getWard().getAvailableBedsReport()); break;
                case 4: System.out.println("\n" + SYSTEM.getWard().getOccupiedBedsReport()); break;
                case 5: System.out.println("\n" + SYSTEM.getWard().getOccupancyReport()); break;
                case 6: System.out.println("\n" + SYSTEM.getWardSummary()); break;
                case 0: back = true; break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void printSortedById() {
        Patient[] sorted = SYSTEM.getPatientsSortedById();
        printHeader("REGISTERED PATIENTS (SORTED BY PATIENT ID)");

        if (sorted.length == 0) {
            System.out.println("There are no registered patients.");
            return;
        }

        System.out.printf("%-10s %-25s %-5s %-10s %-15s %-12s %-12s%n",
                "ID", "NAME", "AGE", "GENDER", "CONDITION", "CATEGORY", "BED");
        for (int i = 0; i < sorted.length; i++) {
            System.out.println(sorted[i].toSummaryLine());
        }
    }

    // ================= INPUT HELPERS =====================================

    /**
     * Reads a non-empty line of text, re-prompting until one is given.
     */
    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = INPUT.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be left blank.");
        }
    }

    /**
     * Reads a whole number, re-prompting until the input parses.
     * Catching NumberFormatException here means a typed letter never crashes
     * the program.
     */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = INPUT.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static void printHeader(String title) {
        System.out.println("\n" + LINE);
        System.out.println(title);
        System.out.println(LINE);
    }
}
