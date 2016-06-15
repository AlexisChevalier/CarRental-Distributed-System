/**
 * CarRental
 *
 * This file provides a specialized exception
 * This exceptions is thrown when the database access fails
 */

package com.vehiclerental.exceptions;

public class DatabaseAccessFailedException extends Exception {
    /**
     * Constructor
     */
    public DatabaseAccessFailedException() {
    }

    /**
     * Constructor
     *
     * @param message failure details
     */
    public DatabaseAccessFailedException(String message) {
        super(message);
    }
}
