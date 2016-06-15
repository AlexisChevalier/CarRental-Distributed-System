/**
 * CarRental
 *
 * This file provides the server entity for a Vehicle Move, it contains all the database-specific details used by OrmLite
 * (External library used to manage SQLite) in order to manage the database.
 *
 * Note: There is many implementation-specific code in order to define the behavior of the user in the database, it could be
 * a design error, and could have been avoided using inheritance
 */

package com.vehiclerental.dataLayer.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.vehiclerental.dataLayer.sqliteImplementation.OrmLiteHelper;

@DatabaseTable(tableName = OrmLiteHelper.VEHICLE_MOVE_TABLE_NAME)
public class VehicleMove {

    // Database field names
    public static final String ID_FIELD_NAME = "id";
    public static final String MOVE_BOOKING_FIELD_NAME = "vehicle_move_booking";
    public static final String MOVE_DATE_FIELD_NAME = "vehicle_move_date";
    public static final String RETURN_DATE_FIELD_NAME = "vehicle_return_date";

    //Properties, with ORMlite annotations for the database
    @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
    private int id;
    @DatabaseField(foreign = true, columnName = MOVE_BOOKING_FIELD_NAME, index = true)
    private Booking booking;
    @DatabaseField(columnName = MOVE_DATE_FIELD_NAME, index = true)
    private long vehicleMoveDate;
    @DatabaseField(columnName = RETURN_DATE_FIELD_NAME, index = true)
    private long vehicleReturnDate;

    //Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public long getVehicleMoveDate() {
        return vehicleMoveDate;
    }

    public void setVehicleMoveDate(long vehicleMoveDate) {
        this.vehicleMoveDate = vehicleMoveDate;
    }

    public long getVehicleReturnDate() {
        return vehicleReturnDate;
    }

    public void setVehicleReturnDate(long vehicleReturnDate) {
        this.vehicleReturnDate = vehicleReturnDate;
    }
}
