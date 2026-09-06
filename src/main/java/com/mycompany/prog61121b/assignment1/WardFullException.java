/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prog61121b.assignment1;

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
 * Thrown when a bed is requested but every bed in the ward is occupied.
 *
 * Extends BedUnavailableException so a caller may catch either the specific
 * "ward full" case or the general "bed unavailable" case.
 */
public class WardFullException extends BedUnavailableException {

    public WardFullException(String message) {
        super(message);
    }
}
