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
 * Oracle, 2024. Creating Exception Classes. The Java Tutorials. [Online]
 *     Available at: https://docs.oracle.com/javase/tutorial/essential/exceptions/creating.html
 *     [Accessed 06 September 2026].
 *
 * The Independent Institute of Education, 2026. PROG61121B Module Manual.
 *     Johannesburg: The Independent Institute of Education.
 */

/**
 * Thrown when a bed cannot be allocated or released.
 *
 * Checked exception, so the calling code (the menu) is forced to handle it.
 */
public class BedUnavailableException extends Exception {

    public BedUnavailableException(String message) {
        super(message);
    }
}
