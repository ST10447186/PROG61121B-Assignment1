package com.mycompany.prog61121b.assignment1;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 * Base class representing a patient registered at MediCare Hospital.
 *
 * All attributes are private (information hiding). Validation lives inside the
 * setters, and the constructor calls those setters, so an invalid Patient
 * object can never be created.
 */
public class Patient {

    public static final int MIN_AGE = 0;
    public static final int MAX_AGE = 130;

    private String patientId;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String medicalCondition;
    private PatientCategory category;

    public Patient(String patientId, String firstName, String lastName, int age,
                   String gender, String medicalCondition, PatientCategory category) {
        setPatientId(patientId);
        setFirstName(firstName);
        setLastName(lastName);
        setAge(age);
        setGender(gender);
        setMedicalCondition(medicalCondition);
        setCategory(category);
    }

    // ----- Getters -------------------------------------------------------

    public String getPatientId() {
        return patientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getMedicalCondition() {
        return medicalCondition;
    }

    public PatientCategory getCategory() {
        return category;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // ----- Setters (with validation) -------------------------------------

    public void setPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty.");
        }
        this.patientId = patientId.trim().toUpperCase();
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }
        this.firstName = firstName.trim();
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty.");
        }
        this.lastName = lastName.trim();
    }

    public void setAge(int age) {
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new IllegalArgumentException(
                    "Age must be between " + MIN_AGE + " and " + MAX_AGE + ".");
        }
        this.age = age;
    }

    public void setGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be empty.");
        }
        this.gender = gender.trim();
    }

    public void setMedicalCondition(String medicalCondition) {
        if (medicalCondition == null || medicalCondition.trim().isEmpty()) {
            throw new IllegalArgumentException("Medical condition cannot be empty.");
        }
        this.medicalCondition = medicalCondition.trim();
    }

    public void setCategory(PatientCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Patient category cannot be null.");
        }
        this.category = category;
    }

    // ----- Behaviour ------------------------------------------------------

    /**
     * Prints the patient's details to the console.
     * Subclasses override this method to add their own information.
     */
    public void displayDetails() {
        System.out.println("--------------------------------------------------");
        System.out.printf("Patient ID       : %s%n", patientId);
        System.out.printf("Name             : %s%n", getFullName());
        System.out.printf("Age              : %d%n", age);
        System.out.printf("Gender           : %s%n", gender);
        System.out.printf("Medical Condition: %s%n", medicalCondition);
        System.out.printf("Category         : %s%n", category.getLabel());
    }

    /**
     * One-line summary used by the patient list report.
     */
    public String toSummaryLine() {
        return String.format("%-10s %-25s %-5d %-10s %-15s %-12s",
                patientId, getFullName(), age, gender, medicalCondition, category.getLabel());
    }

    @Override
    public String toString() {
        return getFullName() + " (" + patientId + ")";
    }
}