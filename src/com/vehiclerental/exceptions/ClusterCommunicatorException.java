/**
 * CarRental
 *
 * This file provides a specialized exception
 * This exceptions is thrown when the cluster communicator fails
 */

package com.vehiclerental.exceptions;

public class ClusterCommunicatorException extends Exception {
    /**
     * Constructor
     *
     * @param message failure details
     */
    public ClusterCommunicatorException(String message) {
        super(message);
    }
}
