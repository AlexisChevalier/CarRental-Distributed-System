/**
 * CarRental
 *
 * This file provides a specialized exception
 * This exceptions is thrown when a provided date is invalid or not correctly formatted
 */


package com.vehiclerental.exceptions;

public class InvalidDateException extends Exception {

    /**
     * Constructor
     *
     * @param message failure details
     */
    public InvalidDateException(String message) {
        super(message);
    }
}
