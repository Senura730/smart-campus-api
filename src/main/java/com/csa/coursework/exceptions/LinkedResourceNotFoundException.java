package com.csa.coursework.exceptions;

/**
 * The "Alarm": This is the actual error object.
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}