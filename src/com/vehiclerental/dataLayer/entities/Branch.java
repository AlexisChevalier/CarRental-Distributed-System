/**
 * CarRental
 *
 * This file provides the server entity for a Branch, it contains all the database-specific details used by OrmLite
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

@DatabaseTable(tableName = OrmLiteHelper.BRANCH_TABLE_NAME)
public class Branch {

    // Database field names
    public static final String CLUSTER_ID_FIELD_NAME = "cluster_id";
    public static final String NAME_FIELD_NAME = "name";
    public static final String LATITUDE_FIELD_NAME = "latitude";
    public static final String LONGITUDE_DAY_FIELD_NAME = "longitude";

    //Properties, with ORMlite annotations for the database
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(unique = true, columnName = CLUSTER_ID_FIELD_NAME)
    private int clusterId;
    @DatabaseField(columnName = NAME_FIELD_NAME)
    private String name;
    @DatabaseField(columnName = LATITUDE_FIELD_NAME)
    private double latitude;
    @DatabaseField(columnName = LONGITUDE_DAY_FIELD_NAME)
    private double longitude;
    @ForeignCollectionField(eager = false)
    private ForeignCollection<Vehicle> vehicles;
    @ForeignCollectionField(eager = false)
    private ForeignCollection<Booking> bookings;

    //Constructors
    public Branch() {
    }

    /**
     * Shortcut to generate a branch
     *
     * @param clusterId - ID of the cluster node
     * @param name - Name of the branch office
     * @param latitude - Geographical latitude of the branch
     * @param longitude - Geographical longitude of the branch
     */
    public Branch(int clusterId, String name, double latitude, double longitude) {
        this.clusterId = clusterId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ForeignCollection<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(ForeignCollection<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public ForeignCollection<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(ForeignCollection<Booking> bookings) {
        this.bookings = bookings;
    }
}
