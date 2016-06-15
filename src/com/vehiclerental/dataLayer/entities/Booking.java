/**
 * CarRental
 *
 * This file provides the server entity for a Booking, it contains all the database-specific details used by OrmLite
 * (External library used to manage SQLite) in order to manage the database.
 *
 * Note: There is many implementation-specific code in order to define the behavior of the user in the database, it could be
 * a design error, and could have been avoided using inheritance
 */


package com.vehiclerental.dataLayer.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.vehiclerental.dataLayer.sqliteImplementation.OrmLiteHelper;
import com.vehiclerental.utils.CryptoUtils;

@DatabaseTable(tableName = OrmLiteHelper.BOOKING_TABLE_NAME)
public class Booking {

    // Database field names
    public static final String ID_FIELD_NAME = "id";
    public static final String BRANCH_FIELD_NAME = "branch_id";
    public static final String USER_FIELD_NAME = "user_id";
    public static final String VEHICLE_FIELD_NAME = "vehicle_id";
    public static final String START_DAY_FIELD_NAME = "start_day_timestamp";
    public static final String END_DAY_FIELD_NAME = "end_day_timestamp";
    public static final String DAY_COUNT_FIELD_NAME = "days_count";
    public static final String PRICE_FIELD_NAME = "price";
    public static final String CREDIT_CARD_NUMBER_FIELD_NAME = "credit_card_number";
    public static final String CREDIT_CARD_EXPIRATION_MONTH_FIELD_NAME = "credit_card_expiration_month";
    public static final String CREDIT_CARD_EXPIRATION_YEAR_FIELD_NAME = "credit_card_expiration_year";
    public static final String CREDIT_CARD_CVC_CODE_FIELD_NAME = "credit_card_cvc_code";
    public static final String BOOKING_VALIDATED_FIELD_NAME = "booking_validated";
    public static final String VEHICLE_MOVE_FIELD_NAME = "vehicle_move_id";

    //Properties, with ORMlite annotations for the database
    @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
    private int id;
    @DatabaseField(foreign = true, columnName = BRANCH_FIELD_NAME, foreignAutoRefresh = true)
    private Branch branch;
    @DatabaseField(foreign = true, columnName = USER_FIELD_NAME, foreignAutoRefresh = true)
    private User user;
    @DatabaseField(foreign = true, columnName = VEHICLE_FIELD_NAME, foreignAutoRefresh = true)
    private Vehicle vehicle;
    @DatabaseField(foreign = true, columnName = VEHICLE_MOVE_FIELD_NAME, foreignAutoRefresh = true)
    private VehicleMove vehicleMove;
    @DatabaseField(columnName = START_DAY_FIELD_NAME, index = true)
    private long pickUpDate;
    @DatabaseField(columnName = END_DAY_FIELD_NAME, index = true)
    private long returnDate;
    @DatabaseField(columnName = DAY_COUNT_FIELD_NAME)
    private long daysCount;
    @DatabaseField(columnName = PRICE_FIELD_NAME)
    private double price;
    @DatabaseField(columnName = CREDIT_CARD_NUMBER_FIELD_NAME)
    private String creditCardNumberEncrypted;
    @DatabaseField(columnName = CREDIT_CARD_EXPIRATION_MONTH_FIELD_NAME)
    private String creditCardExpirationMonthEncrypted;
    @DatabaseField(columnName = CREDIT_CARD_EXPIRATION_YEAR_FIELD_NAME)
    private String creditCardExpirationYearEncrypted;
    @DatabaseField(columnName = CREDIT_CARD_CVC_CODE_FIELD_NAME)
    private String creditCardCvcCodeEncrypted;
    @DatabaseField(columnName = BOOKING_VALIDATED_FIELD_NAME, index = true)
    private boolean bookingValidated;


    //Helper methods
    /**
     * Encrypts some properties of the entity
     */
    public void encrypt() {
        try {
            this.creditCardNumberEncrypted = CryptoUtils.encrypt(creditCardNumberEncrypted);
            this.creditCardExpirationMonthEncrypted = CryptoUtils.encrypt(creditCardExpirationMonthEncrypted);
            this.creditCardExpirationYearEncrypted = CryptoUtils.encrypt(creditCardExpirationYearEncrypted);
            this.creditCardCvcCodeEncrypted = CryptoUtils.encrypt(creditCardCvcCodeEncrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Decrypts the previously encrypted properties of the entity
     */
    public void decrypt() {
        try {
            this.creditCardNumberEncrypted = CryptoUtils.decrypt(creditCardNumberEncrypted);
            this.creditCardExpirationMonthEncrypted = CryptoUtils.decrypt(creditCardExpirationMonthEncrypted);
            this.creditCardExpirationYearEncrypted = CryptoUtils.decrypt(creditCardExpirationYearEncrypted);
            this.creditCardCvcCodeEncrypted = CryptoUtils.decrypt(creditCardCvcCodeEncrypted);
        } catch (Exception e) {
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

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public long getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(long pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public long getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(long returnDate) {
        this.returnDate = returnDate;
    }

    public long getDaysCount() {
        return daysCount;
    }

    public void setDaysCount(long daysCount) {
        this.daysCount = daysCount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCreditCardNumberEncrypted() {
        return creditCardNumberEncrypted;
    }

    public void setCreditCardNumberEncrypted(String creditCardNumberEncrypted) {
        this.creditCardNumberEncrypted = creditCardNumberEncrypted;
    }

    public String getCreditCardExpirationMonthEncrypted() {
        return creditCardExpirationMonthEncrypted;
    }

    public void setCreditCardExpirationMonthEncrypted(String creditCardExpirationMonthEncrypted) {
        this.creditCardExpirationMonthEncrypted = creditCardExpirationMonthEncrypted;
    }

    public String getCreditCardExpirationYearEncrypted() {
        return creditCardExpirationYearEncrypted;
    }

    public void setCreditCardExpirationYearEncrypted(String creditCardExpirationYearEncrypted) {
        this.creditCardExpirationYearEncrypted = creditCardExpirationYearEncrypted;
    }

    public String getCreditCardCvcCodeEncrypted() {
        return creditCardCvcCodeEncrypted;
    }

    public void setCreditCardCvcCodeEncrypted(String creditCardCvcCodeEncrypted) {
        this.creditCardCvcCodeEncrypted = creditCardCvcCodeEncrypted;
    }

    public boolean getBookingValidated() {
        return bookingValidated;
    }

    public void setBookingValidated(boolean bookingValidated) {
        this.bookingValidated = bookingValidated;
    }

    public VehicleMove getVehicleMove() {
        return vehicleMove;
    }

    public void setVehicleMove(VehicleMove vehicleMove) {
        this.vehicleMove = vehicleMove;
    }
}
