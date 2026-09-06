package com.mycompany.prog61121b.assignment1;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 * Represents the three categories of patient treated by MediCare Hospital.
 *
 * The enum carries behaviour as well as constants: each category knows its
 * display label and whether a patient of that category requires a hospital bed.
 */
public enum PatientCategory {

    INPATIENT("Inpatient", true),
    OUTPATIENT("Outpatient", false),
    EMERGENCY("Emergency", false);

    private final String label;
    private final boolean requiresBed;

    PatientCategory(String label, boolean requiresBed) {
        this.label = label;
        this.requiresBed = requiresBed;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Only inpatients may be allocated a hospital bed.
     */
    public boolean requiresBed() {
        return requiresBed;
    }

    /**
     * Converts free text typed by the user into a category.
     * Accepts either the enum name ("INPATIENT") or the label ("Inpatient").
     *
     * @throws IllegalArgumentException if the text does not match a category
     */
    public static PatientCategory fromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient category cannot be empty.");
        }
        String cleaned = text.trim();
        for (PatientCategory category : PatientCategory.values()) {
            if (category.name().equalsIgnoreCase(cleaned)
                    || category.label.equalsIgnoreCase(cleaned)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid patient category: " + text);
    }

    /**
     * Converts a menu choice (1, 2 or 3) into a category.
     *
     * @throws IllegalArgumentException if the choice is out of range
     */
    public static PatientCategory fromChoice(int choice) {
        PatientCategory[] categories = PatientCategory.values();
        if (choice < 1 || choice > categories.length) {
            throw new IllegalArgumentException(
                    "Category choice must be between 1 and " + categories.length + ".");
        }
        return categories[choice - 1];
    }

    @Override
    public String toString() {
        return label;
    }
}
