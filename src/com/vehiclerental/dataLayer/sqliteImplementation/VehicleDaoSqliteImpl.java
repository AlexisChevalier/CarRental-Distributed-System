/**
 * CarRental
 *
 * This file provides an SQLite implementation of the vehicle data access operations
 */

package com.vehiclerental.dataLayer.sqliteImplementation;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.vehiclerental.dataLayer.entities.Booking;
import com.vehiclerental.dataLayer.entities.VehicleMove;
import com.vehiclerental.dataLayer.interfaces.VehicleDao;
import com.vehiclerental.dataLayer.entities.Branch;
import com.vehiclerental.dataLayer.entities.Vehicle;
import com.vehiclerental.utils.DateUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

//Extends the BaseDaoSqliteImpl in order to inherit the generic methods, and implements the VehicleDao to only implement the specific methods
public class VehicleDaoSqliteImpl extends BaseDaoSqliteImpl<Vehicle> implements VehicleDao {
    //SQL request templates are generated a single time at the first query and are then kept in memory to improve performance
    private static String searchAllAvailableVehiclesSqlQueryTemplate = null;
    private static String searchSpecificAvailableVehicleSqlQueryTemplate = null;

    /**
     * Instantiate the object using a common DAO object of the OrmLite SQLite implementation
     *
     * @param daoImpl the orm-specific data access object
     */
    public VehicleDaoSqliteImpl(Dao<Vehicle, Integer> daoImpl) {
        super(daoImpl);
    }

    /**
     * Return the specified vehicle if it is available for the given criteria in the SQLite database
     *
     * @param branch the vehicle branch
     * @param vehicleId the vehicle ID
     * @param startDate the start date of the booking
     * @param endDate the end date of the booking
     * @return the vehicle, if available, null if the vehicle is not found or unavailable
     */
    @Override
    public Vehicle getVehicleIfAvailable(Branch branch, int vehicleId, Calendar startDate, Calendar endDate) {
        try {
            long startTimestamp = startDate.getTimeInMillis();
            long endTimestamp = endDate.getTimeInMillis();

            String rawQuery = populateSearchSpecificAvailableVehicleSqlQuery(branch, startTimestamp, endTimestamp, vehicleId);

            GenericRawResults<Vehicle> rawResults = dao.queryRaw(rawQuery, dao.getRawRowMapper());

            List<Vehicle> vehicleList = rawResults.getResults();

            rawResults.close();

            if (vehicleList.size() == 0) {
                return null;
            } else {
                return vehicleList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Search for all the vehicle available for the given criteria in the SQLite database
     *
     * @param branch the vehicles branch
     * @param type the vehicles type
     * @param startDate the start date of the booking
     * @param endDate the end date of the booking
     * @return a list of the matched vehicles
     */
    @Override
    public List<Vehicle> getAvailableVehicles(Branch branch, Integer type, Calendar startDate, Calendar endDate) {
        try {

            long startTimestamp = startDate.getTimeInMillis();
            long endTimestamp = endDate.getTimeInMillis();

            String rawQuery = populateSearchAllAvailableVehiclesSqlQuery(branch, startTimestamp, endTimestamp, type);

            GenericRawResults<Vehicle> rawResults = dao.queryRaw(rawQuery, dao.getRawRowMapper());

            List<Vehicle> vehicleList = rawResults.getResults();

            rawResults.close();

            return vehicleList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns all the vehicles matching a type in the specified branch in the SQLite database
     *
     * @param vehicleTypeId the type ID
     * @param nodeBranch the branch
     * @return a list of the matched vehicles
     */
    @Override
    public List<Vehicle> searchBranchVehiclesByType(int vehicleTypeId, Branch nodeBranch) {
        try {
            QueryBuilder<Vehicle, Integer> statementBuilder = dao.queryBuilder();

            Where whereClause = statementBuilder.where();

            whereClause
                    .eq(Vehicle.TYPE_FIELD_NAME, vehicleTypeId)
                    .and()
                    .eq(Vehicle.BRANCH_FIELD_NAME, nodeBranch);

            return dao.query(statementBuilder.prepare());
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Search for a vehicle with a specific registration number in a given branch in the SQLite database
     * @param registrationNumber the registration number
     * @param nodeBranch the given branch
     * @return the vehicle, if existing, null if not
     */
    @Override
    public Vehicle getVehicleByRegistrationNumberAndBranch(String registrationNumber, Branch nodeBranch) {
        try {
            QueryBuilder<Vehicle, Integer> statementBuilder = dao.queryBuilder();

            Where whereClause = statementBuilder.where();

            whereClause
                    .eq(Vehicle.REGISTRATION_NUMBER_FIELD_NAME, registrationNumber)
                    .and()
                    .eq(Vehicle.BRANCH_FIELD_NAME, nodeBranch);

            List<Vehicle> vehicles = dao.query(statementBuilder.prepare());

            if (vehicles.size() == 0) {
                return null;
            } else {
                return vehicles.get(0);
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Populates the SQL query template with the correct values for the search available vehicles
     *
     * @param branch the given branch
     * @param bookingBeginDate the given booking begin date, as a timestamp with milliseconds truncated to the day
     * @param bookingEndDate the given booking end date, as a timestamp with milliseconds truncated to the day
     * @param vehicleType the given vehicle type
     * @return the built SQL query
     */
    private String populateSearchAllAvailableVehiclesSqlQuery(Branch branch, long bookingBeginDate, long bookingEndDate, int vehicleType) {
        /* The sql query is build without escaping because we only specify numbers, and they are inserted using String.format so an exception will be thrown if an illegal character is used */
        return String.format(getSearchAvailableVehicleSqlQueryTemplate(false), branch.getId(), bookingBeginDate, bookingEndDate, vehicleType);
    }

    /**
     * Populates the SQL query template with the correct values for the search specific available vehicle
     *
     * @param branch the given branch
     * @param bookingBeginDate the given booking begin date, as a timestamp with milliseconds truncated to the day
     * @param bookingEndDate the given booking end date, as a timestamp with milliseconds truncated to the day
     * @param vehicleId the given vehicle id
     * @return the built SQL query
     */
    private String populateSearchSpecificAvailableVehicleSqlQuery(Branch branch, long bookingBeginDate, long bookingEndDate, int vehicleId) {
        /* The sql query is build without escaping because we only specify numbers, and they are inserted using String.format so an exception will be thrown if an illegal character is used */
        return String.format(getSearchAvailableVehicleSqlQueryTemplate(true), branch.getId(), bookingBeginDate, bookingEndDate, vehicleId);
    }

    /**
     * Returns the SQL query template for the search available vehicles, it will contain the generated table/column names and the placeholders for the values
     * The template is built and cached because there is a high amount of string concatenations, that is why I do it only one time
     *
     * @param isVehicleSpecificSearch - true if it concerns the search of a specific vehicle
     * @return The SQL query template
     */
    private String getSearchAvailableVehicleSqlQueryTemplate(boolean isVehicleSpecificSearch) {

        if (isVehicleSpecificSearch) {
            if (searchSpecificAvailableVehicleSqlQueryTemplate == null) {
                searchSpecificAvailableVehicleSqlQueryTemplate = buildQueryTemplate(true);
            }
            return searchSpecificAvailableVehicleSqlQueryTemplate;
        } else {
            if (searchAllAvailableVehiclesSqlQueryTemplate == null) {
                searchAllAvailableVehiclesSqlQueryTemplate = buildQueryTemplate(false);
            }
            return searchAllAvailableVehiclesSqlQueryTemplate;
        }
    }

    /**
     * This methods returns the main SQL query used to search available vehicles or detect if a specific vehicle is available for given dates
     * The template is built using the String.format system of Java, which is why I use integer placements (%x$d). They will be replaced later
     * during the population of the query with the given numbers
     * I am using a raw SQL query here because the ORM doesn't support all the used SQL features for this case
     *
     * This query involves a custom part depending of we are looking for a specific vehicle or all the vehicles available
     *
     * @param isVehicleSpecificSearch - true if it concerns the search of a specific vehicle
     * @return The SQL query template
     */
    private String buildQueryTemplate(boolean isVehicleSpecificSearch) {
        String customPart;
        String queryTemplate;

        if (isVehicleSpecificSearch) {
            //If we want to search a single vehicle, then the request will target a vehicle ID
            customPart = "`ve`.`" + Vehicle.ID_FIELD_NAME + "` = %4$d ";
        } else {
            //If not, we will target a vehicle type ID
            customPart = "`ve`.`" + Vehicle.TYPE_FIELD_NAME + "` = %4$d ";
        }

        queryTemplate =
            "SELECT * FROM `" + OrmLiteHelper.VEHICLE_TABLE_NAME + "` ve WHERE " + // Search vehicles
            "( " +
                "`ve`.`" + Vehicle.BRANCH_FIELD_NAME + "` = %1$d " + // Search in a specific branch
                "AND " +
                "`ve`.`" + Vehicle.STATUS_FIELD_NAME + "` != " + Vehicle.Status.MAINTENANCE.getCode() + " " + //The vehicle should not be available if under maintenance
                "AND " +
                customPart + //Insert custom part defined at the beginning of the method
                "AND " +
                "( " +
                    "NOT EXISTS " + // We want to find every vehicle that doesn't have any booking matching the next rules
                    "( " +
                        "SELECT * FROM `" + OrmLiteHelper.BOOKING_TABLE_NAME + "` bo " + //Select all from bookings
                            "LEFT JOIN `" + OrmLiteHelper.VEHICLE_MOVE_TABLE_NAME + "` vm " + //With vehicles moves (nullified if no move required)
                            "ON `vm`.`" + VehicleMove.ID_FIELD_NAME + "` = `bo`.`" + Booking.VEHICLE_MOVE_FIELD_NAME + "` " + // Simple foreign key binding
                        "WHERE " +
                        "( " +
                            "`bo`.`" + Booking.VEHICLE_FIELD_NAME + "` = `ve`.`" + Vehicle.ID_FIELD_NAME+ "` " + //This is a sub-query, and we want to get all the bookings corresponding to the ID of the parent request for the vehicle
                            "AND " +
                            "`bo`.`" + Booking.BOOKING_VALIDATED_FIELD_NAME + "` = 1 " + //We also want the booking to be validated
                            "AND " +
                            "( " +
                                //Is there any booking starting before and ending after the specified dates (Booking would be in the middle of the existing booking)
                                "(" + getIfNullStart() + " < %2$d AND " + getIfNullEnd() + " > %3$d) " +
                                "OR " +
                                //Is there any booking starting before the beginning and ending before the ending specified dates (with limits to stay inside the period) (Booking would be too early for the previous booking to end)
                                "(" + getIfNullStart() + " < %2$d AND " + getIfNullEnd() + " <= %3$d AND " + getIfNullEnd() + " >= %2$d) " +
                                "OR " +
                                //Is there any booking starting after the beginning and ending after the ending specified dates (with limits to stay inside the period)
                                "(" + getIfNullStart() + " >= %2$d AND " + getIfNullEnd() + " > %3$d AND " + getIfNullStart() + " <= %3$d) " +
                                "OR " +
                                //Is there any booking starting after the beginning and ending before the ending specified dates
                                "(" + getIfNullStart() + " >= %2$d AND " + getIfNullEnd() + " <= %3$d) " +
                            ") " +
                        ") " +
                    ") " +
                ") " +
            ") ";

        return queryTemplate;
    }

    /**
     * This method returns a specific SQL statement to use either the vehicle move start date if available or the vehicle pickup date
     *
     * IFNULL is an SQL statement, in IFNULL(val1, val2), SQLite will return val1 if it is not null, and it will return val2 otherwise
     *
     * @return the generated statement
     */
    private static String getIfNullStart() {
        return "IFNULL(`vm`.`" + VehicleMove.MOVE_DATE_FIELD_NAME + "`, `bo`.`" + Booking.START_DAY_FIELD_NAME + "`)";
    }
    /**
     * This method returns a specific SQL statement to use either the vehicle move return date if available or the vehicle return date
     *
     * IFNULL is an SQL statement, in IFNULL(val1, val2), SQLite will return val1 if it is not null, and it will return val2 otherwise
     *
     * @return the generated statement
     */
    private static String getIfNullEnd() {
        return "IFNULL(`vm`.`" + VehicleMove.RETURN_DATE_FIELD_NAME + "`, `bo`.`" + Booking.END_DAY_FIELD_NAME + "`)";
    }
}
