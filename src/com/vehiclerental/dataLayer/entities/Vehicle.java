/**
 * CarRental
 *
 * This file provides the server entity for a Vehicle, it contains all the database-specific details used by OrmLite
 * (External library used to manage SQLite) in order to manage the database.
 *
 * Note: There is many implementation-specific code in order to define the behavior of the user in the database, it could be
 * a design error, and could have been avoided using inheritance
 */


package com.vehiclerental.dataLayer.entities;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.vehiclerental.dataLayer.sqliteImplementation.OrmLiteHelper;

@DatabaseTable(tableName = OrmLiteHelper.VEHICLE_TABLE_NAME)
public class Vehicle {

    // Database field names
    public static final String ID_FIELD_NAME = "id";
    public static final String BRANCH_FIELD_NAME = "branch_id";
    public static final String STATUS_FIELD_NAME = "status";
    public static final String TYPE_FIELD_NAME = "type";
    public static final String REGISTRATION_NUMBER_FIELD_NAME = "registration_number";
    public static final String DOORS_FIELD_NAME = "doors";
    public static final String SEATS_FIELD_NAME = "seats";
    public static final String AUTOMATIC_TRANSMISSION_FIELD_NAME = "automatic_transmission";
    public static final String POUNDS_PER_DAY_FIELD_NAME = "pounds_per_day";
    public static final String NAME_FIELD_NAME = "name";

    //Status enum
    public enum Status {
        AVAILABLE(0),
        IN_TRANSIT(1),
        IN_CLIENT_BOOKING(2),
        MAINTENANCE(3);

        private int code;

        Status(int code) {
            this.code = code;
        }

        public int getCode() { return code; }

        public static Status get(int code) {
            for(Status s : values()) {
                if(s.code == code) return s;
            }
            return null;
        }
    }

    //Type enum
    public enum Type {
        SMALL_CAR(0),
        FAMILY_CAR(1),
        SMALL_VAN(2),
        LARGE_VAN(3);

        private int code;

        Type(int code) {
            this.code = code;
        }

        public int getCode() { return code; }

        public static Type get(int code) {
            for(Type s : values()) {
                if(s.code == code) return s;
            }
            return null;
        }
    }

    //Properties, with ORMlite annotations for the database
    @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
    private int id;
    @DatabaseField(foreign = true, columnName = BRANCH_FIELD_NAME, foreignAutoRefresh = true)
    private Branch branch;
    @DatabaseField(columnName = STATUS_FIELD_NAME)
    private int status;
    @DatabaseField(columnName = TYPE_FIELD_NAME, index = true)
    private int type;
    @DatabaseField(unique = true, columnName = REGISTRATION_NUMBER_FIELD_NAME, index = true)
    private String registrationNumber;
    @DatabaseField(columnName = DOORS_FIELD_NAME)
    private int doors;
    @DatabaseField(columnName = SEATS_FIELD_NAME)
    private int seats;
    @DatabaseField(columnName = AUTOMATIC_TRANSMISSION_FIELD_NAME)
    private boolean automaticTransmission;
    @DatabaseField(columnName = POUNDS_PER_DAY_FIELD_NAME)
    private double poundsPerDay;
    @DatabaseField(columnName = NAME_FIELD_NAME)
    private String name;
    @ForeignCollectionField(eager = false)
    private ForeignCollection<Booking> bookings;

    //Constructors
    public Vehicle() {
    }

    /**
     * Shortcut to generate a vehicle
     *
     * @param branch Branch owning the vehicle
     * @param status Status of the vehicle
     * @param type Type of the vehicle
     * @param registrationNumber Unique registration number of the vehicle
     * @param doors Number of doors
     * @param seats Number of seats
     * @param automaticTransmission True if automatic transmission, False if manual
     * @param poundsPerDay Price of the car per day
     * @param name Name of the car
     */
    public Vehicle(Branch branch, int status, int type, String registrationNumber, int doors, int seats, boolean automaticTransmission, double poundsPerDay, String name) {
        this.branch = branch;
        this.status = status;
        this.type = type;
        this.registrationNumber = registrationNumber;
        this.doors = doors;
        this.seats = seats;
        this.automaticTransmission = automaticTransmission;
        this.poundsPerDay = poundsPerDay;
        this.name = name;
    }

    //Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getDoors() {
        return doors;
    }

    public void setDoors(int doors) {
        this.doors = doors;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public boolean isAutomaticTransmission() {
        return automaticTransmission;
    }

    public void setAutomaticTransmission(boolean automaticTransmission) {
        this.automaticTransmission = automaticTransmission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPoundsPerDay() {
        return poundsPerDay;
    }

    public void setPoundsPerDay(double poundsPerDay) {
        this.poundsPerDay = poundsPerDay;
    }

    public ForeignCollection<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(ForeignCollection<Booking> bookings) {
        this.bookings = bookings;
    }
}
