/**
 * CarRental
 *
 * This file provides a specialized exception
 * This exceptions is thrown when the database update operation fails
 */


package com.vehiclerental.exceptions;

public class DatabaseUpdateFailedException extends Exception {

    /**
     * Constructor
     *
     * @param message failure details
     */
    public DatabaseUpdateFailedException(String message) {
        super(message);
    }
}
