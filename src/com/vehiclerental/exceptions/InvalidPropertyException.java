/**
 * CarRental
 *
 * This file provides a specialized exception
 * This exceptions is thrown when a specific property of a user request is invalid
 */

package com.vehiclerental.exceptions;

public class InvalidPropertyException extends Exception {

    /**
     * Constructor
     *
     * @param message failure details
     */
    public InvalidPropertyException(String message) {
        super(message);
    }
}
