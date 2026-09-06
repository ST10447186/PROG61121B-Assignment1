package com.mycompany.prog61121b.assignment1;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import com.mycompany.prog61121b.assignment1.DuplicatePatientException;
import com.mycompany.prog61121b.assignment1.BedUnavailableException;
import java.util.ArrayList;
import java.util.List;

/**
 * The core service class of the Hospital Patient Admission System.
 *
 * Holds the patient records in an ArrayList and owns the Ward. All business
 * rules live here: duplicate ID prevention, searching, updating, deletion,
 * sorting and bed allocation.
 *
 * Like Ward, this class does no console input or output. It returns values and
 * throws exceptions so that every rule can be unit tested.
 */
public class HospitalSystem {

    private final ArrayList<Patient> patients;
    private final Ward ward;

    public HospitalSystem(int wardNumber) {
        this.patients = new ArrayList<>();
        this.ward = new Ward(wardNumber);
    }

    public HospitalSystem() {
        this(1);
    }

    public Ward getWard() {
        return ward;
    }

    // ----- Create ---------------------------------------------------------

    /**
     * Registers a new patient.
     *
     * @throws DuplicatePatientException if the Patient ID is already in use
     */
    public void registerPatient(Patient patient) throws DuplicatePatientException {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null.");
        }
        if (patientExists(patient.getPatientId())) {
            throw new DuplicatePatientException("A patient with ID "
                    + patient.getPatientId() + " is already registered.");
        }
        patients.add(patient);
    }

    // ----- Read -----------------------------------------------------------

    public boolean patientExists(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return false;
        }
        String target = patientId.trim().toUpperCase();
        for (Patient patient : patients) {
            if (patient.getPatientId().equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Searches for a patient by Patient ID.
     *
     * @throws PatientNotFoundException if no patient has that ID
     */
    public Patient findPatient(String patientId) throws PatientNotFoundException {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty.");
        }
        String target = patientId.trim().toUpperCase();
        for (Patient patient : patients) {
            if (patient.getPatientId().equals(target)) {
                return patient;
            }
        }
        throw new PatientNotFoundException("No patient found with ID " + target + ".");
    }

    /**
     * @return an unmodifiable-style copy of the patient list
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    public int getTotalPatients() {
        return patients.size();
    }

    public int countByCategory(PatientCategory category) {
        int count = 0;
        for (Patient patient : patients) {
            if (patient.getCategory() == category) {
                count++;
            }
        }
        return count;
    }

    // ----- Update ---------------------------------------------------------

    /**
     * Updates the editable details of an existing patient.
     * The Patient ID and the category are not changed here.
     *
     * @throws PatientNotFoundException if no patient has that ID
     */
    public void updatePatient(String patientId, String firstName, String lastName,
                              int age, String gender, String medicalCondition)
            throws PatientNotFoundException {

        Patient patient = findPatient(patientId);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setMedicalCondition(medicalCondition);
    }

    // ----- Delete ---------------------------------------------------------

    /**
     * Deletes a patient. If the patient is an inpatient occupying a bed, the
     * bed is released first so the ward is never left with a stale record.
     *
     * @throws PatientNotFoundException if no patient has that ID
     */
    public void deletePatient(String patientId)
            throws PatientNotFoundException, BedUnavailableException {

        Patient patient = findPatient(patientId);
        if (patient instanceof Inpatient) {
            Inpatient inpatient = (Inpatient) patient;
            if (inpatient.hasBed()) {
                ward.releaseBed(inpatient);
            }
        }
        patients.remove(patient);
    }

    // ----- Bed management -------------------------------------------------

    /**
     * Allocates a specific bed to a registered inpatient.
     *
     * @throws BedUnavailableException if the patient is not an inpatient, or
     *                                 the bed cannot be allocated
     */
    public void allocateBed(String patientId, int bedNumber)
            throws PatientNotFoundException, BedUnavailableException {

        Inpatient inpatient = requireInpatient(patientId);
        ward.allocateBed(bedNumber, inpatient);
    }

    /**
     * Allocates the first free bed to a registered inpatient.
     *
     * @return the bed number allocated
     */
    public int allocateFirstAvailableBed(String patientId)
            throws PatientNotFoundException, BedUnavailableException {

        Inpatient inpatient = requireInpatient(patientId);
        return ward.allocateFirstAvailableBed(inpatient);
    }

    /**
     * Releases the bed held by an inpatient on discharge.
     *
     * @return the bed number that was freed
     */
    public int releaseBed(String patientId)
            throws PatientNotFoundException, BedUnavailableException {

        Inpatient inpatient = requireInpatient(patientId);
        return ward.releaseBed(inpatient);
    }

    /**
     * Finds a patient and confirms they are an inpatient.
     * Only inpatients may be allocated a hospital bed.
     */
    private Inpatient requireInpatient(String patientId)
            throws PatientNotFoundException, BedUnavailableException {

        Patient patient = findPatient(patientId);
        if (!(patient instanceof Inpatient)) {
            throw new BedUnavailableException("Patient " + patient.getPatientId()
                    + " is an " + patient.getCategory().getLabel()
                    + " and does not require a hospital bed.");
        }
        return (Inpatient) patient;
    }

    // ----- Sorting --------------------------------------------------------

    /**
     * Returns the patients sorted by surname, then first name.
     * Uses a selection sort on a plain array to demonstrate array sorting,
     * passing an array to a method, and use of the length field.
     */
    public Patient[] getPatientsSortedBySurname() {
        Patient[] sorted = patients.toArray(new Patient[0]);
        selectionSortBySurname(sorted);
        return sorted;
    }

    /**
     * Returns the patients sorted by Patient ID.
     */
    public Patient[] getPatientsSortedById() {
        Patient[] sorted = patients.toArray(new Patient[0]);
        selectionSortById(sorted);
        return sorted;
    }

    private void selectionSortBySurname(Patient[] list) {
        for (int i = 0; i < list.length - 1; i++) {
            int smallest = i;
            for (int j = i + 1; j < list.length; j++) {
                if (compareBySurname(list[j], list[smallest]) < 0) {
                    smallest = j;
                }
            }
            swap(list, i, smallest);
        }
    }

    private void selectionSortById(Patient[] list) {
        for (int i = 0; i < list.length - 1; i++) {
            int smallest = i;
            for (int j = i + 1; j < list.length; j++) {
                if (list[j].getPatientId().compareTo(list[smallest].getPatientId()) < 0) {
                    smallest = j;
                }
            }
            swap(list, i, smallest);
        }
    }

    private int compareBySurname(Patient first, Patient second) {
        int result = first.getLastName().compareToIgnoreCase(second.getLastName());
        if (result == 0) {
            result = first.getFirstName().compareToIgnoreCase(second.getFirstName());
        }
        return result;
    }

    private void swap(Patient[] list, int a, int b) {
        if (a != b) {
            Patient temporary = list[a];
            list[a] = list[b];
            list[b] = temporary;
        }
    }

    // ----- Reports --------------------------------------------------------

    /**
     * Builds the full patient report, sorted by surname.
     */
    public String getPatientReport() {
        StringBuilder report = new StringBuilder();
        report.append("REGISTERED PATIENTS (").append(getTotalPatients()).append(")\n");
        report.append("-------------------------------------------------------")
              .append("-------------------------------------\n");

        if (patients.isEmpty()) {
            report.append("There are no registered patients.");
            return report.toString();
        }

        report.append(String.format("%-10s %-25s %-5s %-10s %-15s %-12s %-12s%n",
                "ID", "NAME", "AGE", "GENDER", "CONDITION", "CATEGORY", "BED"));

        Patient[] sorted = getPatientsSortedBySurname();
        for (int i = 0; i < sorted.length; i++) {
            report.append(sorted[i].toSummaryLine()).append("\n");
        }
        return report.toString().trim();
    }

    /**
     * Builds the ward summary, combining patient totals with bed occupancy.
     */
    public String getWardSummary() {
        return String.format(
                "WARD SUMMARY%n"
                + "--------------------------------------------------%n"
                + "Registered patients : %d%n"
                + "  Inpatients        : %d%n"
                + "  Outpatients       : %d%n"
                + "  Emergency         : %d%n"
                + "Occupied beds       : %d of %d%n"
                + "Available beds      : %d%n"
                + "Ward occupancy      : %.1f%%",
                getTotalPatients(),
                countByCategory(PatientCategory.INPATIENT),
                countByCategory(PatientCategory.OUTPATIENT),
                countByCategory(PatientCategory.EMERGENCY),
                ward.getOccupiedBedCount(), Ward.TOTAL_BEDS,
                ward.getAvailableBedCount(),
                ward.getOccupancyPercentage());
    }
}
