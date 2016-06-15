/**
 * CarRental
 *
 * This file stores a single SQLite connection instance through for the node
 */

package com.vehiclerental.dataLayer.sqliteImplementation;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class OrmLiteConnectionSingleton {
    private static ConnectionSource dbConnectionSourceInstance;

    private static final String DATABASE_URL = "jdbc:sqlite:car_rental.db";

    /**
     * Returns the existing connection handler for the program
     * Also generates the connection handler if not existing
     *
     * @return ORMLite SQLite connection handler
     * @throws SQLException
     */
    public static ConnectionSource getInstance() throws SQLException {
        if (dbConnectionSourceInstance == null) {
            dbConnectionSourceInstance = new JdbcConnectionSource(DATABASE_URL);
        }
        return dbConnectionSourceInstance;
    }

    /**
     * Closes the current connection handler
     */
    public static void close() {
        dbConnectionSourceInstance.closeQuietly();
        dbConnectionSourceInstance = null;
    }
}
