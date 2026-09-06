package com.mycompany.prog61121b.assignment1;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 * An inpatient is a Patient who is admitted to the ward and may occupy a bed.
 *
 * Demonstrates:
 *  - inheritance (extends Patient)
 *  - constructor chaining (super(...) initialises the inherited attributes)
 *  - method overriding (displayDetails() extends the superclass behaviour)
 */
public class Inpatient extends Patient {

    /** Sentinel value meaning "this inpatient has not been given a bed yet". */
    public static final int NO_BED = -1;

    public static final int TOTAL_BEDS = 20;

    private int wardNumber;
    private int bedNumber;

    /**
     * Creates an inpatient who has not yet been allocated a bed.
     * The category is always INPATIENT, so it is not passed in by the caller.
     */
    public Inpatient(String patientId, String firstName, String lastName, int age,
                     String gender, String medicalCondition, int wardNumber) {

        // Initialise the inherited attributes through the superclass constructor.
        super(patientId, firstName, lastName, age, gender, medicalCondition,
                PatientCategory.INPATIENT);

        setWardNumber(wardNumber);
        this.bedNumber = NO_BED;
    }

    // ----- Getters -------------------------------------------------------

    public int getWardNumber() {
        return wardNumber;
    }

    public int getBedNumber() {
        return bedNumber;
    }

    public boolean hasBed() {
        return bedNumber != NO_BED;
    }

    // ----- Setters (with validation) -------------------------------------

    public void setWardNumber(int wardNumber) {
        if (wardNumber < 1) {
            throw new IllegalArgumentException("Ward number must be 1 or greater.");
        }
        this.wardNumber = wardNumber;
    }

    /**
     * Records the bed occupied by this inpatient.
     * Pass NO_BED to clear the bed when the patient is discharged.
     */
    public void setBedNumber(int bedNumber) {
        if (bedNumber != NO_BED && (bedNumber < 1 || bedNumber > TOTAL_BEDS)) {
            throw new IllegalArgumentException(
                    "Bed number must be between 1 and " + TOTAL_BEDS + ".");
        }
        this.bedNumber = bedNumber;
    }

    public void clearBed() {
        this.bedNumber = NO_BED;
    }

    /**
     * Returns the bed in the ward's label format, e.g. B07, or "Not allocated".
     */
    public String getBedLabel() {
        return hasBed() ? String.format("B%02d", bedNumber) : "Not allocated";
    }

    // ----- Overridden behaviour -------------------------------------------

    /**
     * Extends the superclass output with the ward and bed information.
     */
    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.printf("Ward Number      : %d%n", wardNumber);
        System.out.printf("Bed Number       : %s%n", getBedLabel());
    }

    @Override
    public String toSummaryLine() {
        return super.toSummaryLine() + String.format(" %-12s", getBedLabel());
    }
}
