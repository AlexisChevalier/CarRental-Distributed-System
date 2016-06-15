/**
 * CarRental
 *
 * This file provides a common interface for database initialization class, used during the startup of the distributed
 * system
 */

package com.vehiclerental.dataLayer.interfaces;

import com.vehiclerental.exceptions.DatabaseAccessFailedException;

public interface DatabaseHelper {
    /**
     * This methods sets up the database environment with the default values
     * @throws DatabaseAccessFailedException if it is not possible to connect to the database
     */
    void ensureInitialized() throws DatabaseAccessFailedException;
}
