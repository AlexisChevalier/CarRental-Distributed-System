/**
 * CarRental
 *
 * This file provides an initialization class, giving access to every data access object (DAO) from the other layers
 */

package com.vehiclerental.dataLayer;

import com.vehiclerental.dataLayer.interfaces.*;
import com.vehiclerental.dataLayer.sqliteImplementation.*;
import com.vehiclerental.exceptions.DatabaseAccessFailedException;

public class DaoFactory {
    private static OrmLiteHelper ormLiteHelper = new OrmLiteHelper();

    /**
     * Returns a Database initialization class
     * @return an instantiated database helper
     * @throws DatabaseAccessFailedException if the database is not usable
     */
    public static DatabaseHelper getDatabaseHelper() throws DatabaseAccessFailedException {
        return new OrmLiteHelper();
    }

    /**
     * Returns an instantiated booking data access object
     *
     * @throws DatabaseAccessFailedException if the database is not usable
     */
    public static BookingDao getBookingDao() throws DatabaseAccessFailedException {
        return new BookingDaoSqliteImpl(ormLiteHelper.getOrmLiteBookingDao());
    }

    /**
     * Returns an instantiated user data access object
     *
     * @throws DatabaseAccessFailedException if the database is not usable
     */
    public static UserDao getUserDao() throws DatabaseAccessFailedException {
        return new UserDaoSqliteImpl(ormLiteHelper.getOrmLiteUserDao());
    }

    /**
     * Returns an instantiated branch data access object
     *
     * @throws DatabaseAccessFailedException if the database is not usable
     */
    public static BranchDao getBranchDao() throws DatabaseAccessFailedException {
        return new BranchDaoSqliteImpl(ormLiteHelper.getOrmLiteBranchDao());
    }

    /**
     * Returns an instantiated vehicle data access object
     *
     * @throws DatabaseAccessFailedException if the database is not usable
     */
    public static VehicleDao getVehicleDao() throws DatabaseAccessFailedException {
        return new VehicleDaoSqliteImpl(ormLiteHelper.getOrmLiteVehicleDao());
    }

    /**
     * Returns an instantiated vehicle move data access object
     *
     * @throws DatabaseAccessFailedException if the database is not usable
     */
    public static VehicleMoveDao getVehicleMoveDao() throws DatabaseAccessFailedException {
        return new VehicleMoveDaoSqliteImpl(ormLiteHelper.getOrmLiteVehicleMoveDao());
    }
}
