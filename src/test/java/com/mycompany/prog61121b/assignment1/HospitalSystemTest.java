package com.mycompany.prog61121b.assignment1;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

/*
 * PROG61121B - Assignment 1
 * MediCare Hospital Patient Admission System
 *
 * Author:         Mikyle Naraidu
 * Student number: ST10447186
 * Date:           September 2026
 *
 * References:
 * JUnit Team, 2024. JUnit 5 User Guide. [Online]
 *     Available at: https://junit.org/junit5/docs/current/user-guide/
 *     [Accessed 6 September 2026].
 * JUnit Team, 2024. Class Assertions. JUnit 5 API Documentation. [Online]
 *     Available at: https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/jupiter/api/Assertions.html
 *     [Accessed 6 September 2026].
 *
 * The Independent Institute of Education, 2026. PROG61121B Module Manual.
 *     Johannesburg: The Independent Institute of Education.
 */

import com.mycompany.prog61121b.assignment1.Ward;
import com.mycompany.prog61121b.assignment1.PatientNotFoundException;
import com.mycompany.prog61121b.assignment1.PatientCategory;
import com.mycompany.prog61121b.assignment1.Patient;
import com.mycompany.prog61121b.assignment1.Inpatient;
import com.mycompany.prog61121b.assignment1.HospitalSystem;
import com.mycompany.prog61121b.assignment1.DuplicatePatientException;
import com.mycompany.prog61121b.assignment1.BedUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for the Hospital Patient Admission System.
 *
 * Covers the ten scenarios listed in Feature 5:
 *   register, search, update, delete, allocate a bed, release a bed,
 *   prevent duplicate Patient IDs, prevent allocating an occupied bed,
 *   prevent allocation when the ward is full, and sorting.
 */
class HospitalSystemTest {

    private HospitalSystem system;

    @BeforeEach
    void setUp() {
        system = new HospitalSystem(1);
    }

    // ----- Helpers --------------------------------------------------------

    private Inpatient newInpatient(String id, String first, String last) {
        return new Inpatient(id, first, last, 40, "Female", "Pneumonia", 1);
    }

    private Patient newOutpatient(String id, String first, String last) {
        return new Patient(id, first, last, 30, "Male", "Sprain",
                PatientCategory.OUTPATIENT);
    }

    // ================= CRUD OPERATION TESTS ==============================

    @Test
    @DisplayName("A new patient can be registered and is stored")
    void registerPatientAddsPatient() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));

        assertEquals(1, system.getTotalPatients());
        assertTrue(system.patientExists("P001"));
    }

    @Test
    @DisplayName("A registered patient can be found by Patient ID")
    void searchPatientReturnsCorrectPatient() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));
        system.registerPatient(newOutpatient("P002", "Riaan", "Botha"));

        Patient found = system.findPatient("P002");

        assertEquals("P002", found.getPatientId());
        assertEquals("Riaan Botha", found.getFullName());
    }

    @Test
    @DisplayName("Searching is case insensitive and trims spaces")
    void searchPatientIgnoresCaseAndSpaces() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));

        assertEquals("P001", system.findPatient("  p001  ").getPatientId());
    }

    @Test
    @DisplayName("Searching for an unknown ID throws PatientNotFoundException")
    void searchUnknownPatientThrows() {
        assertThrows(PatientNotFoundException.class,
                () -> system.findPatient("P999"));
    }

    @Test
    @DisplayName("An existing patient's details can be updated")
    void updatePatientChangesDetails() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));

        system.updatePatient("P001", "Ayanda", "Ndlovu", 35, "Female", "Asthma");
        Patient updated = system.findPatient("P001");

        assertEquals("Ndlovu", updated.getLastName());
        assertEquals(35, updated.getAge());
        assertEquals("Asthma", updated.getMedicalCondition());
    }

    @Test
    @DisplayName("Updating an unknown patient throws PatientNotFoundException")
    void updateUnknownPatientThrows() {
        assertThrows(PatientNotFoundException.class,
                () -> system.updatePatient("P999", "A", "B", 20, "Male", "Flu"));
    }

    @Test
    @DisplayName("A patient can be deleted and is removed from the system")
    void deletePatientRemovesPatient() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));

        system.deletePatient("P001");

        assertEquals(0, system.getTotalPatients());
        assertFalse(system.patientExists("P001"));
    }

    @Test
    @DisplayName("Deleting an inpatient releases the bed they occupied")
    void deletePatientReleasesBed() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));
        system.allocateBed("P001", 5);

        system.deletePatient("P001");

        assertFalse(system.getWard().isOccupied(5));
        assertEquals(0, system.getWard().getOccupiedBedCount());
    }

    // ================= BED MANAGEMENT TESTS ==============================

    @Test
    @DisplayName("A bed can be allocated to an inpatient")
    void allocateBedMarksBedOccupied() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));

        system.allocateBed("P001", 7);
        Inpatient patient = (Inpatient) system.findPatient("P001");

        assertTrue(system.getWard().isOccupied(7));
        assertEquals("P001", system.getWard().getOccupant(7));
        assertEquals(7, patient.getBedNumber());
        assertTrue(patient.hasBed());
    }

    @Test
    @DisplayName("The first available bed is allocated in order, starting at bed 1")
    void allocateFirstAvailableBedUsesLowestFreeBed() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));
        system.registerPatient(newInpatient("P002", "Thabo", "Mokoena"));

        assertEquals(1, system.allocateFirstAvailableBed("P001"));
        assertEquals(2, system.allocateFirstAvailableBed("P002"));
    }

    @Test
    @DisplayName("A bed can be released and becomes available again")
    void releaseBedFreesBed() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));
        system.allocateBed("P001", 3);

        int freed = system.releaseBed("P001");
        Inpatient patient = (Inpatient) system.findPatient("P001");

        assertEquals(3, freed);
        assertFalse(system.getWard().isOccupied(3));
        assertFalse(patient.hasBed());
        assertEquals(Inpatient.NO_BED, patient.getBedNumber());
    }

    @Test
    @DisplayName("Releasing a bed for a patient without one throws")
    void releaseBedWithoutAllocationThrows() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));

        assertThrows(BedUnavailableException.class,
                () -> system.releaseBed("P001"));
    }

    @Test
    @DisplayName("Occupancy percentage is calculated correctly")
    void occupancyPercentageIsCorrect() throws Exception {
        for (int i = 1; i <= 5; i++) {
            String id = String.format("P%03d", i);
            system.registerPatient(newInpatient(id, "First" + i, "Last" + i));
            system.allocateFirstAvailableBed(id);
        }

        assertEquals(5, system.getWard().getOccupiedBedCount());
        assertEquals(15, system.getWard().getAvailableBedCount());
        assertEquals(25.0, system.getWard().getOccupancyPercentage(), 0.001);
    }

    // ================= VALIDATION AND BOUNDARY TESTS =====================

    @Test
    @DisplayName("Registering a duplicate Patient ID throws DuplicatePatientException")
    void duplicatePatientIdIsRejected() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));

        assertThrows(DuplicatePatientException.class,
                () -> system.registerPatient(newOutpatient("P001", "Someone", "Else")));
        assertEquals(1, system.getTotalPatients());
    }

    @Test
    @DisplayName("Duplicate detection ignores letter case")
    void duplicateDetectionIsCaseInsensitive() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));

        assertThrows(DuplicatePatientException.class,
                () -> system.registerPatient(newOutpatient("p001", "Someone", "Else")));
    }

    @Test
    @DisplayName("Allocating an already occupied bed throws BedUnavailableException")
    void occupiedBedCannotBeAllocatedAgain() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));
        system.registerPatient(newInpatient("P002", "Thabo", "Mokoena"));
        system.allocateBed("P001", 4);

        assertThrows(BedUnavailableException.class,
                () -> system.allocateBed("P002", 4));
        assertEquals("P001", system.getWard().getOccupant(4));
    }

    @Test
    @DisplayName("An inpatient cannot occupy two beds at once")
    void inpatientCannotHoldTwoBeds() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));
        system.allocateBed("P001", 4);

        assertThrows(BedUnavailableException.class,
                () -> system.allocateBed("P001", 5));
        assertFalse(system.getWard().isOccupied(5));
    }

    @Test
    @DisplayName("Outpatients and emergency patients cannot be given a bed")
    void nonInpatientCannotBeAllocatedBed() throws Exception {
        system.registerPatient(newOutpatient("P002", "Riaan", "Botha"));

        assertThrows(BedUnavailableException.class,
                () -> system.allocateBed("P002", 1));
    }

    @Test
    @DisplayName("Allocation is refused when every bed is occupied")
    void fullWardRefusesAllocation() throws Exception {
        for (int i = 1; i <= Ward.TOTAL_BEDS; i++) {
            String id = String.format("P%03d", i);
            system.registerPatient(newInpatient(id, "First" + i, "Last" + i));
            system.allocateFirstAvailableBed(id);
        }
        system.registerPatient(newInpatient("P021", "One", "TooMany"));

        assertTrue(system.getWard().isFull());
        assertThrows(WardFullException.class,
                () -> system.allocateFirstAvailableBed("P021"));
        assertEquals(100.0, system.getWard().getOccupancyPercentage(), 0.001);
    }

    @Test
    @DisplayName("Bed numbers outside 1 to 20 are rejected")
    void bedNumberBoundariesAreEnforced() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));

        assertThrows(IllegalArgumentException.class,
                () -> system.allocateBed("P001", 0));
        assertThrows(IllegalArgumentException.class,
                () -> system.allocateBed("P001", 21));
    }

    @Test
    @DisplayName("Beds 1 and 20 are valid boundary allocations")
    void firstAndLastBedsCanBeAllocated() throws Exception {
        system.registerPatient(newInpatient("P001", "Ayanda", "Zulu"));
        system.registerPatient(newInpatient("P002", "Thabo", "Mokoena"));

        system.allocateBed("P001", 1);
        system.allocateBed("P002", Ward.TOTAL_BEDS);

        assertTrue(system.getWard().isOccupied(1));
        assertTrue(system.getWard().isOccupied(Ward.TOTAL_BEDS));
    }

    @Test
    @DisplayName("Invalid patient details are rejected by the setters")
    void invalidPatientDetailsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Inpatient("", "Ayanda", "Zulu", 30, "Female", "Flu", 1));
        assertThrows(IllegalArgumentException.class,
                () -> new Inpatient("P001", "Ayanda", "Zulu", -1, "Female", "Flu", 1));
        assertThrows(IllegalArgumentException.class,
                () -> new Inpatient("P001", "Ayanda", "Zulu", 200, "Female", "Flu", 1));
        assertThrows(IllegalArgumentException.class,
                () -> new Patient("P001", "  ", "Zulu", 30, "Female", "Flu",
                        PatientCategory.EMERGENCY));
    }

    @Test
    @DisplayName("Age boundaries of 0 and 130 are accepted")
    void ageBoundariesAreAccepted() {
        assertDoesNotThrow(() ->
                new Patient("P001", "Baby", "Nkosi", Patient.MIN_AGE, "Female",
                        "Observation", PatientCategory.EMERGENCY));
        assertDoesNotThrow(() ->
                new Patient("P002", "Elder", "Naidoo", Patient.MAX_AGE, "Male",
                        "Observation", PatientCategory.OUTPATIENT));
    }

    // ================= SORTING TESTS =====================================

    @Test
    @DisplayName("Patients are sorted alphabetically by surname")
    void patientsSortBySurname() throws Exception {
        system.registerPatient(newOutpatient("P003", "Sipho", "Zulu"));
        system.registerPatient(newOutpatient("P001", "Riaan", "Botha"));
        system.registerPatient(newOutpatient("P002", "Thabo", "Mokoena"));

        Patient[] sorted = system.getPatientsSortedBySurname();

        assertEquals("Botha", sorted[0].getLastName());
        assertEquals("Mokoena", sorted[1].getLastName());
        assertEquals("Zulu", sorted[2].getLastName());
    }

    @Test
    @DisplayName("Patients are sorted by Patient ID")
    void patientsSortById() throws Exception {
        system.registerPatient(newOutpatient("P003", "Sipho", "Zulu"));
        system.registerPatient(newOutpatient("P001", "Riaan", "Botha"));
        system.registerPatient(newOutpatient("P002", "Thabo", "Mokoena"));

        Patient[] sorted = system.getPatientsSortedById();

        assertEquals("P001", sorted[0].getPatientId());
        assertEquals("P002", sorted[1].getPatientId());
        assertEquals("P003", sorted[2].getPatientId());
    }

    @Test
    @DisplayName("Sorting an empty system returns an empty array")
    void sortingEmptySystemReturnsEmptyArray() {
        assertEquals(0, system.getPatientsSortedBySurname().length);
    }

    // ================= INHERITANCE AND ENUM TESTS ========================

    @Test
    @DisplayName("An Inpatient is a Patient and is always the INPATIENT category")
    void inpatientInheritsFromPatient() {
        Inpatient inpatient = newInpatient("P001", "Ayanda", "Zulu");

        assertTrue(inpatient instanceof Patient);
        assertEquals(PatientCategory.INPATIENT, inpatient.getCategory());
        assertEquals(1, inpatient.getWardNumber());
        assertFalse(inpatient.hasBed());
    }

    @Test
    @DisplayName("Only the inpatient category requires a bed")
    void onlyInpatientsRequireBeds() {
        assertTrue(PatientCategory.INPATIENT.requiresBed());
        assertFalse(PatientCategory.OUTPATIENT.requiresBed());
        assertFalse(PatientCategory.EMERGENCY.requiresBed());
    }

    @Test
    @DisplayName("Category text and menu choices convert correctly")
    void categoryConversionWorks() {
        assertEquals(PatientCategory.EMERGENCY, PatientCategory.fromText("emergency"));
        assertEquals(PatientCategory.OUTPATIENT, PatientCategory.fromText("Outpatient"));
        assertEquals(PatientCategory.INPATIENT, PatientCategory.fromChoice(1));
        assertThrows(IllegalArgumentException.class,
                () -> PatientCategory.fromChoice(9));
        assertThrows(IllegalArgumentException.class,
                () -> PatientCategory.fromText("Visitor"));
    }

    @Test
    @DisplayName("The bed label is formatted as B01 to B20")
    void bedLabelIsFormatted() {
        assertEquals("B01", Ward.bedLabel(1));
        assertEquals("B07", Ward.bedLabel(7));
        assertEquals("B20", Ward.bedLabel(20));
    }
}