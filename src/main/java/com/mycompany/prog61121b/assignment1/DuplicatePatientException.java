package com.mycompany.prog61121b.assignment1;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Thrown when an attempt is made to register a patient using a Patient ID
 * that is already in use.
 */
public class DuplicatePatientException extends Exception {

    public DuplicatePatientException(String message) {
        super(message);
    }
}
