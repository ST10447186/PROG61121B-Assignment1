package com.mycompany.prog61121b.assignment1;


import com.mycompany.prog61121b.assignment1.Inpatient;
import com.mycompany.prog61121b.assignment1.BedUnavailableException;

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
 * Oracle, 2024. Inheritance. The Java Tutorials. [Online]
 *     Available at: https://docs.oracle.com/javase/tutorial/java/IandI/subclasses.html
 *     [Accessed 6 September 2026].
 * Oracle, 2024. Using the Keyword super. The Java Tutorials. [Online]
 *     Available at: https://docs.oracle.com/javase/tutorial/java/IandI/super.html
 *     [Accessed 6 September 2026].
 * Oracle, 2024. Overriding and Hiding Methods. The Java Tutorials. [Online]
 *     Available at: https://docs.oracle.com/javase/tutorial/java/IandI/override.html
 *     [Accessed 6 September 2026].
 *
 * The Independent Institute of Education, 2026. PROG61121B Module Manual.
 *     Johannesburg: The Independent Institute of Education.
 */

/**
 * Represents the hospital ward and its 20 beds, arranged in a 4 x 5 grid.
 *
 * The grid is a two-dimensional array of Strings. Each cell holds the Patient ID
 * of the inpatient occupying that bed, or null if the bed is free.
 *
 *   Bed numbers run left to right, top to bottom:
 *
 *      B01  B02  B03  B04  B05
 *      B06  B07  B08  B09  B10
 *      B11  B12  B13  B14  B15
 *      B16  B17  B18  B19  B20
 *
 *   bed number n  ->  row = (n - 1) / COLUMNS,  column = (n - 1) % COLUMNS
 *
 * This class never reads from or writes to the console. It returns values and
 * throws exceptions, which keeps it fully unit-testable.
 */
public class Ward {

    public static final int ROWS = 4;
    public static final int COLUMNS = 5;
    public static final int TOTAL_BEDS = ROWS * COLUMNS;

    private final int wardNumber;
    private final String[][] beds;

    public Ward(int wardNumber) {
        if (wardNumber < 1) {
            throw new IllegalArgumentException("Ward number must be 1 or greater.");
        }
        this.wardNumber = wardNumber;
        this.beds = new String[ROWS][COLUMNS];
    }

    public int getWardNumber() {
        return wardNumber;
    }

    // ----- Helpers --------------------------------------------------------

    /**
     * Converts a bed number (1..20) into the label used on screen, e.g. B07.
     */
    public static String bedLabel(int bedNumber) {
        return String.format("B%02d", bedNumber);
    }

    private void validateBedNumber(int bedNumber) {
        if (bedNumber < 1 || bedNumber > TOTAL_BEDS) {
            throw new IllegalArgumentException(
                    "Bed number must be between 1 and " + TOTAL_BEDS + ".");
        }
    }

    private int rowOf(int bedNumber) {
        return (bedNumber - 1) / COLUMNS;
    }

    private int columnOf(int bedNumber) {
        return (bedNumber - 1) % COLUMNS;
    }

    // ----- Queries --------------------------------------------------------

    public boolean isOccupied(int bedNumber) {
        validateBedNumber(bedNumber);
        return beds[rowOf(bedNumber)][columnOf(bedNumber)] != null;
    }

    /**
     * @return the Patient ID occupying the bed, or null if the bed is free
     */
    public String getOccupant(int bedNumber) {
        validateBedNumber(bedNumber);
        return beds[rowOf(bedNumber)][columnOf(bedNumber)];
    }

    public int getOccupiedBedCount() {
        int count = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (beds[row][column] != null) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getAvailableBedCount() {
        return TOTAL_BEDS - getOccupiedBedCount();
    }

    public boolean isFull() {
        return getAvailableBedCount() == 0;
    }

    public double getOccupancyPercentage() {
        return (getOccupiedBedCount() * 100.0) / TOTAL_BEDS;
    }

    /**
     * @return the number of the first free bed, or -1 if the ward is full
     */
    public int findFirstAvailableBed() {
        for (int bedNumber = 1; bedNumber <= TOTAL_BEDS; bedNumber++) {
            if (!isOccupied(bedNumber)) {
                return bedNumber;
            }
        }
        return -1;
    }

    /**
     * @return the bed number occupied by the given Patient ID, or -1 if none
     */
    public int findBedOf(String patientId) {
        if (patientId == null) {
            return -1;
        }
        String target = patientId.trim().toUpperCase();
        for (int bedNumber = 1; bedNumber <= TOTAL_BEDS; bedNumber++) {
            String occupant = getOccupant(bedNumber);
            if (occupant != null && occupant.equals(target)) {
                return bedNumber;
            }
        }
        return -1;
    }

    // ----- Allocation and release -----------------------------------------

    /**
     * Allocates a specific bed to an inpatient and updates the patient record.
     *
     * @throws WardFullException        if every bed is occupied
     * @throws BedUnavailableException  if the requested bed is taken, or the
     *                                  patient already occupies another bed
     */
    public void allocateBed(int bedNumber, Inpatient patient)
            throws BedUnavailableException {

        validateBedNumber(bedNumber);
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null.");
        }
        if (isFull()) {
            throw new WardFullException(
                    "No beds available - all " + TOTAL_BEDS + " beds are occupied.");
        }
        if (isOccupied(bedNumber)) {
            throw new BedUnavailableException("Bed " + bedLabel(bedNumber)
                    + " is already occupied by patient "
                    + getOccupant(bedNumber) + ".");
        }
        if (patient.hasBed()) {
            throw new BedUnavailableException("Patient " + patient.getPatientId()
                    + " already occupies bed " + patient.getBedLabel() + ".");
        }

        beds[rowOf(bedNumber)][columnOf(bedNumber)] = patient.getPatientId();
        patient.setBedNumber(bedNumber);
    }

    /**
     * Allocates the first available bed to an inpatient.
     *
     * @return the bed number that was allocated
     */
    public int allocateFirstAvailableBed(Inpatient patient)
            throws BedUnavailableException {

        int bedNumber = findFirstAvailableBed();
        if (bedNumber == -1) {
            throw new WardFullException(
                    "No beds available - all " + TOTAL_BEDS + " beds are occupied.");
        }
        allocateBed(bedNumber, patient);
        return bedNumber;
    }

    /**
     * Releases the bed occupied by the given inpatient (on discharge).
     *
     * @return the bed number that was freed
     * @throws BedUnavailableException if the patient does not occupy a bed
     */
    public int releaseBed(Inpatient patient) throws BedUnavailableException {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null.");
        }
        if (!patient.hasBed()) {
            throw new BedUnavailableException("Patient " + patient.getPatientId()
                    + " does not currently occupy a bed.");
        }

        int bedNumber = patient.getBedNumber();
        beds[rowOf(bedNumber)][columnOf(bedNumber)] = null;
        patient.clearBed();
        return bedNumber;
    }

    /**
     * Releases a bed by bed number, used when a bed is cleared directly.
     *
     * @throws BedUnavailableException if the bed is already free
     */
    public String releaseBed(int bedNumber) throws BedUnavailableException {
        validateBedNumber(bedNumber);
        String occupant = getOccupant(bedNumber);
        if (occupant == null) {
            throw new BedUnavailableException(
                    "Bed " + bedLabel(bedNumber) + " is already free.");
        }
        beds[rowOf(bedNumber)][columnOf(bedNumber)] = null;
        return occupant;
    }

    // ----- Display --------------------------------------------------------

    /**
     * Builds the full ward layout using nested loops.
     * Free beds show their label, occupied beds show [ID].
     */
    public String getLayout() {
        StringBuilder layout = new StringBuilder();
        layout.append("WARD ").append(wardNumber)
              .append(" - BED LAYOUT (").append(ROWS)
              .append(" x ").append(COLUMNS).append(")\n");
        layout.append("--------------------------------------------------\n");

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                int bedNumber = (row * COLUMNS) + column + 1;
                String occupant = beds[row][column];
                String cell = (occupant == null)
                        ? bedLabel(bedNumber)
                        : "[" + occupant + "]";
                layout.append(String.format("%-12s", cell));
            }
            layout.append("\n");
        }

        layout.append("--------------------------------------------------\n");
        layout.append("Free bed = bed number, occupied bed = [Patient ID]");
        return layout.toString();
    }

    /**
     * Builds a list of the free beds using nested loops.
     */
    public String getAvailableBedsReport() {
        StringBuilder report = new StringBuilder();
        report.append("AVAILABLE BEDS (").append(getAvailableBedCount())
              .append(" of ").append(TOTAL_BEDS).append(")\n");
        report.append("--------------------------------------------------\n");

        if (getAvailableBedCount() == 0) {
            report.append("There are no available beds.");
            return report.toString();
        }

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (beds[row][column] == null) {
                    int bedNumber = (row * COLUMNS) + column + 1;
                    report.append(String.format("%-8s", bedLabel(bedNumber)));
                }
            }
        }
        return report.toString().trim();
    }

    /**
     * Builds a list of the occupied beds and their occupants using nested loops.
     */
    public String getOccupiedBedsReport() {
        StringBuilder report = new StringBuilder();
        report.append("OCCUPIED BEDS (").append(getOccupiedBedCount())
              .append(" of ").append(TOTAL_BEDS).append(")\n");
        report.append("--------------------------------------------------\n");

        if (getOccupiedBedCount() == 0) {
            report.append("There are no occupied beds.");
            return report.toString();
        }

        report.append(String.format("%-8s %-12s%n", "BED", "PATIENT ID"));
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (beds[row][column] != null) {
                    int bedNumber = (row * COLUMNS) + column + 1;
                    report.append(String.format("%-8s %-12s%n",
                            bedLabel(bedNumber), beds[row][column]));
                }
            }
        }
        return report.toString().trim();
    }

    /**
     * Builds the occupancy summary used by the reports menu.
     */
    public String getOccupancyReport() {
        return String.format(
                "WARD OCCUPANCY REPORT%n"
                + "--------------------------------------------------%n"
                + "Total beds     : %d%n"
                + "Occupied beds  : %d%n"
                + "Available beds : %d%n"
                + "Occupancy      : %.1f%%",
                TOTAL_BEDS, getOccupiedBedCount(),
                getAvailableBedCount(), getOccupancyPercentage());
    }
}
