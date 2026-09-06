package com.mycompany.prog61121b.assignment1;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
