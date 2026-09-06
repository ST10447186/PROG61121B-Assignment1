package com.mycompany.prog61121b.assignment1;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Thrown when no patient can be found for a given Patient ID.
 */
public class PatientNotFoundException extends Exception {

    public PatientNotFoundException(String message) {
        super(message);
    }
}
