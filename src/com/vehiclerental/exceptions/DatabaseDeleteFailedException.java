/**
 * CarRental
 *
 * This file provides a specialized exception
 * This exceptions is thrown when the database delete operation fails
 */


package com.vehiclerental.exceptions;

public class DatabaseDeleteFailedException extends Exception {

    /**
     * Constructor
     *
     * @param message failure details
     */
    public DatabaseDeleteFailedException(String message) {
        super(message);
    }
}
