/**
 * CarRental
 *
 * This file provides an SQLite implementation of the booking data access operations
 */

package com.vehiclerental.dataLayer.sqliteImplementation;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.vehiclerental.dataLayer.entities.*;
import com.vehiclerental.dataLayer.interfaces.BookingDao;
import com.vehiclerental.exceptions.DatabaseAccessFailedException;
import com.vehiclerental.utils.DateUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

//Extends the BaseDaoSqliteImpl in order to inherit the generic methods, and implements the BookingDao to only implement the specific methods
public class BookingDaoSqliteImpl  extends BaseDaoSqliteImpl<Booking> implements BookingDao {
    private static String incomingVehicleSqlQueryTemplate = null;
    private static String outgoingVehicleSqlQueryTemplate = null;

    /**
     * Instantiate the object using a common DAO object of the OrmLite SQLite implementation
     *
     * @param daoImpl the orm-specific data access object
     */
    public BookingDaoSqliteImpl(Dao<Booking, Integer> daoImpl) {
        super(daoImpl);
    }

    /**
     * Returns all the bookings for a given branch and a given user in the SQLite database
     *
     * @param branch the specified branch
     * @param user the specified user
     * @return a list of the matching bookings
     */
    @Override
    public List<Booking> getBranchBookingsForUser(Branch branch, User user) {
        try {
            QueryBuilder<Booking, Integer> statementBuilder = dao.queryBuilder();
            Where whereClause = statementBuilder.where();

            whereClause
                    .eq(Booking.BRANCH_FIELD_NAME, branch)
                    .and()
                    .eq(Booking.USER_FIELD_NAME, user);

            statementBuilder.orderBy(Booking.START_DAY_FIELD_NAME, true);

            return dao.query(statementBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns all the bookings for a given branch in the SQLite database
     *
     * @param currentBranch the specified branch
     * @return a list of the matching bookings
     */
    @Override
    public List<Booking> getBranchBookings(Branch currentBranch) {
        try {
            QueryBuilder<Booking, Integer> statementBuilder = dao.queryBuilder();
            Where whereClause = statementBuilder.where();

            whereClause.eq(Booking.BRANCH_FIELD_NAME, currentBranch);

            statementBuilder.orderBy(Booking.START_DAY_FIELD_NAME, true);

            return dao.query(statementBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns all the bookings requiring a move (outgoing OR incoming) for a given branch
     *
     * @param nodeBranch the specified branch
     * @param outgoing if true, only the outgoing moves will be returned, if false, only the incoming ones
     * @return a list of the matching bookings
     */
    @Override
    public List<Booking> getBookingsRequiringMovesForBranch(Branch nodeBranch, boolean outgoing) {

        try {
            long todayTimestamp = DateUtils.getTodayCalendar().getTimeInMillis();

            String rawQuery = String.format(getVehicleMoveQueryTemplate(outgoing), nodeBranch.getId(), todayTimestamp);

            GenericRawResults<Booking> rawResults = dao.queryRaw(rawQuery, dao.getRawRowMapper());

            List<Booking> bookingList = rawResults.getResults();

            rawResults.close();

            return bookingList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This methods returns the main SQL query used to get the future (today included) vehicle moves to OR from a branch, depending on the outgoing parameter
     * The template is built using the String.format system of Java, which is why I use integer placements (%x$d). They will be replaced later
     * during the population of the query with the given numbers
     * I am using a raw SQL query here because the ORM doesn't support all the used SQL features for this case
     *
     * There is two queries here but they are quite similar, only a few conditions are changed
     *
     * @param outgoing - true if it concerns the vehicles moving from this branch to another one
     * @return The SQL query template
     */
    private String getVehicleMoveQueryTemplate(boolean outgoing) {
        if (outgoing) {
            if (outgoingVehicleSqlQueryTemplate == null) {
                outgoingVehicleSqlQueryTemplate =
                    " SELECT `bo`.* FROM `" + OrmLiteHelper.BOOKING_TABLE_NAME + "` bo " +
                    " LEFT JOIN `" + OrmLiteHelper.VEHICLE_MOVE_TABLE_NAME + "` vm " +
                        " ON `vm`.`" + VehicleMove.ID_FIELD_NAME + "` = `bo`.`" + Booking.VEHICLE_MOVE_FIELD_NAME + "` " +
                    " JOIN `" + OrmLiteHelper.VEHICLE_TABLE_NAME + "` ve " +
                       " ON `ve`.`" + Vehicle.ID_FIELD_NAME + "` = `bo`.`" + Booking.VEHICLE_FIELD_NAME + "` " +
                    " WHERE " +
                        " `vm`.`" + VehicleMove.ID_FIELD_NAME + "` IS NOT NULL " + //The booking must have a move
                        " AND " +
                        " `bo`.`" + Booking.BOOKING_VALIDATED_FIELD_NAME + "` = 1 " + //We also want the booking to be validated
                        " AND " +
                        " ( " +
                            " ( " + //In that case, we are looking for the return from the booking branch to the vehicle branch
                                " `bo`.`" + Booking.BRANCH_FIELD_NAME + "` = %1$d " + // The branch of the booking must be the one provided
                                " AND " +
                                " `vm`.`" + VehicleMove.RETURN_DATE_FIELD_NAME + "` >= %2$d " + //Return date must be after or equal to today
                            " ) " +
                            " OR " +
                            " ( " + //Or in this case, we are looking for the first move, from the vehicle branch to the booking branch
                                " `ve`.`" + Vehicle.BRANCH_FIELD_NAME + "` = %1$d " + // The branch of the vehicle must be the one provided
                                " AND " +
                                " `vm`.`" + VehicleMove.MOVE_DATE_FIELD_NAME + "` >= %2$d " + //Move date must be after or equal to today
                            " ) " +
                        " ) ";
            }

            return outgoingVehicleSqlQueryTemplate;
        } else {
            if (incomingVehicleSqlQueryTemplate == null) {
                incomingVehicleSqlQueryTemplate =
                    " SELECT `bo`.* FROM `" + OrmLiteHelper.BOOKING_TABLE_NAME + "` bo " +
                    " LEFT JOIN `" + OrmLiteHelper.VEHICLE_MOVE_TABLE_NAME + "` vm " +
                        " ON `vm`.`" + VehicleMove.ID_FIELD_NAME + "` = `bo`.`" + Booking.VEHICLE_MOVE_FIELD_NAME + "` " +
                    " JOIN `" + OrmLiteHelper.VEHICLE_TABLE_NAME + "` ve " +
                        " ON `ve`.`" + Vehicle.ID_FIELD_NAME + "` = `bo`.`" + Booking.VEHICLE_FIELD_NAME + "` " +
                    " WHERE " +
                        " `vm`.`" + VehicleMove.ID_FIELD_NAME + "` IS NOT NULL " + //The booking must have a move
                        " AND " +
                        " `bo`.`" + Booking.BOOKING_VALIDATED_FIELD_NAME + "` = 1 " + //We also want the booking to be validated
                        " AND " +
                        " ( " +
                            " ( " + //In that case, we are looking for the departure from the vehicle branch to the booking branch
                                " `bo`.`" + Booking.BRANCH_FIELD_NAME + "` = %1$d " + // The branch of the booking must be the one provided
                                " AND " +
                                " `vm`.`" + VehicleMove.MOVE_DATE_FIELD_NAME + "` >= %2$d " + //Move date must be after or equal to today
                            " ) " +
                            " OR " +
                            " ( " + //Or in that case, we are looking for the return from the booking branch to the vehicle branch
                                " `ve`.`" + Vehicle.BRANCH_FIELD_NAME + "` = %1$d " + // The branch of the vehicle must be the one provided
                                " AND " +
                                " `vm`.`" + VehicleMove.RETURN_DATE_FIELD_NAME + "` >= %2$d " + //Return date must be after or equal to today
                            " ) " +
                        " ) ";
            }

            return incomingVehicleSqlQueryTemplate;
        }
    }
}
