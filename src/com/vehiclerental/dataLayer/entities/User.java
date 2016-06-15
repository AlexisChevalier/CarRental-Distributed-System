/**
 * CarRental
 *
 * This file provides the server entity for an User, it contains all the database-specific details used by OrmLite
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
import com.vehiclerental.utils.CryptoUtils;


@DatabaseTable(tableName = OrmLiteHelper.USER_TABLE_NAME)
public class User {

    // Database field names
    public static final String IS_STAFF_FIELD_NAME = "is_staff";
    public static final String FULL_NAME_FIELD_NAME = "full_name";
    public static final String EMAIL_ADDRESS_FIELD_NAME = "email_address";
    public static final String PHONE_NUMBER_FIELD_NAME = "phone_number";
    public static final String HASHED_PASSWORD_FIELD_NAME = "hashed_password";
    public static final String ADDRESS_STREET_FIELD_NAME = "address_street";
    public static final String ADDRESS_CITY_FIELD_NAME = "address_city";
    public static final String ADDRESS_POSTAL_CODE_FIELD_NAME = "address_postal_code";
    public static final String ADDRESS_COUNTRY_FIELD_NAME = "address_country";

    //Properties, with ORMlite annotations for the database
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false, columnName = IS_STAFF_FIELD_NAME)
    private boolean isStaff;
    @DatabaseField(columnName = FULL_NAME_FIELD_NAME)
    private String fullName;
    @DatabaseField(unique = true, columnName = EMAIL_ADDRESS_FIELD_NAME, index = true)
    private String emailAddress;
    @DatabaseField(columnName = PHONE_NUMBER_FIELD_NAME)
    private String phoneNumber;
    @DatabaseField(columnName = HASHED_PASSWORD_FIELD_NAME)
    private String hashedPassword;
    @DatabaseField(columnName = ADDRESS_STREET_FIELD_NAME)
    private String address_street;
    @DatabaseField(columnName = ADDRESS_CITY_FIELD_NAME)
    private String address_city;
    @DatabaseField(columnName = ADDRESS_POSTAL_CODE_FIELD_NAME)
    private String address_postalCode;
    @DatabaseField(columnName = ADDRESS_COUNTRY_FIELD_NAME)
    private String address_country;
    @ForeignCollectionField(eager = false)
    private ForeignCollection<Booking> bookings;

    //Constructors
    public User() {
    }

    /**
     * Shortcut to generate an user
     *
     * @param isStaff True if staff member
     * @param fullName Full name of the user
     * @param emailAddress Unique email address of the user
     * @param phoneNumber Phone number of the user
     * @param hashedPassword SHA256 hashed password of the user
     * @param address_street Street of the user
     * @param address_city City of the user
     * @param address_postalCode Postal code of the user
     * @param address_country Country of the user
     */
    public User(boolean isStaff, String fullName, String emailAddress, String phoneNumber, String hashedPassword, String address_street, String address_city, String address_postalCode, String address_country) {
        this.isStaff = isStaff;
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.hashedPassword = hashedPassword;
        this.address_street = address_street;
        this.address_city = address_city;
        this.address_postalCode = address_postalCode;
        this.address_country = address_country;
    }

    //Helper methods
    /**
     * Encrypts some properties of the entity
     */
    public void encrypt() {
        try {
            this.phoneNumber = CryptoUtils.encrypt(phoneNumber);
            this.hashedPassword = CryptoUtils.encrypt(hashedPassword);
            this.address_street = CryptoUtils.encrypt(address_street);
            this.address_city = CryptoUtils.encrypt(address_city);
            this.address_postalCode = CryptoUtils.encrypt(address_postalCode);
            this.address_country = CryptoUtils.encrypt(address_country);
        } catch (Exception e) {
            //Display error if encryption failed
            e.printStackTrace();
        }
    }

    /**
     * Decrypts the previously encrypted properties of the entity
     */
    public void decrypt() {
        try {
            this.phoneNumber = CryptoUtils.decrypt(phoneNumber);
            this.hashedPassword = CryptoUtils.decrypt(hashedPassword);
            this.address_street = CryptoUtils.decrypt(address_street);
            this.address_city = CryptoUtils.decrypt(address_city);
            this.address_postalCode = CryptoUtils.decrypt(address_postalCode);
            this.address_country = CryptoUtils.decrypt(address_country);
        } catch (Exception e) {
            //Display error if encryption failed
            e.printStackTrace();
        }
    }

    //Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(boolean staff) {
        this.isStaff = staff;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getAddress_street() {
        return address_street;
    }

    public void setAddress_street(String address_street) {
        this.address_street = address_street;
    }

    public String getAddress_city() {
        return address_city;
    }

    public void setAddress_city(String address_city) {
        this.address_city = address_city;
    }

    public String getAddress_postalCode() {
        return address_postalCode;
    }

    public void setAddress_postalCode(String address_postalCode) {
        this.address_postalCode = address_postalCode;
    }

    public String getAddress_country() {
        return address_country;
    }

    public void setAddress_country(String address_country) {
        this.address_country = address_country;
    }

    public ForeignCollection<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(ForeignCollection<Booking> bookings) {
        this.bookings = bookings;
    }
}