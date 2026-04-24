package com.csa.coursework.exceptions;

/**
 * Custom exception to represent a 409 Conflict when a room cannot be deleted.
 */
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}