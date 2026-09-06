/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.prog61121b.assignment1;

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
