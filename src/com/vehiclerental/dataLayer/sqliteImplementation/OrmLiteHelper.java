/**
 * CarRental
 *
 * This file provides an SQLite implementation of the database initialization interface, used during the startup of the distributed
 * system. It uses ORMLite (library) to create the database if needed and populate the tables.
 */

package com.vehiclerental.dataLayer.sqliteImplementation;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.vehiclerental.dataLayer.entities.*;
import com.vehiclerental.dataLayer.interfaces.DatabaseHelper;
import com.vehiclerental.dataLayer.interfaces.VehicleDao;
import com.vehiclerental.exceptions.DatabaseAccessFailedException;
import com.vehiclerental.utils.CryptoUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrmLiteHelper implements DatabaseHelper {

    //Database table names
    public static final String VEHICLE_MOVE_TABLE_NAME = "vehicle_moves";
    public static final String BOOKING_TABLE_NAME = "bookings";
    public static final String VEHICLE_TABLE_NAME = "vehicles";
    public static final String USER_TABLE_NAME = "users";
    public static final String BRANCH_TABLE_NAME = "branches";

    /**
     * This methods sets up the database environment with the default values
     * @throws DatabaseAccessFailedException if it is not possible to connect to the database
     */
    public void ensureInitialized() throws DatabaseAccessFailedException {
        try {
            Dao<User, Integer> userDao = getOrmLiteUserDao();
            if (!userDao.isTableExists()) {
                initializeUserTable(userDao);
            }

            Dao<Branch, Integer> branchDao = getOrmLiteBranchDao();
            if (!branchDao.isTableExists()) {
                initializeBranchTable(branchDao);
            }

            Dao<Vehicle, Integer> vehicleDao = getOrmLiteVehicleDao();
            if (!vehicleDao.isTableExists()) {
                initializeVehicleTable(vehicleDao);
            }

            Dao<Booking, Integer> bookingDao = getOrmLiteBookingDao();
            if (!bookingDao.isTableExists()) {
                initializeBookingTable(bookingDao);
            }

            Dao<VehicleMove, Integer> vehicleMoveDao = getOrmLiteVehicleMoveDao();
            if (!vehicleMoveDao.isTableExists()) {
                initializeVehicleMoveTable(vehicleMoveDao);
            }
        } catch (SQLException e) {
            throw new DatabaseAccessFailedException(e.getMessage());
        }
    }

    /**
     * Generates an ORMLite data access object for the User table
     *
     * @return ORMlite User dao object
     * @throws DatabaseAccessFailedException if connection failed
     */
    public Dao<User, Integer> getOrmLiteUserDao() throws DatabaseAccessFailedException {
        try {
            return DaoManager.createDao(OrmLiteConnectionSingleton.getInstance(), User.class);
        } catch (SQLException e) {
            throw new DatabaseAccessFailedException(e.getMessage());
        }
    }

    /**
     * Generates an ORMLite data access object for the Vehicle table
     *
     * @return ORMlite Vehicle dao object
     * @throws DatabaseAccessFailedException if connection failed
     */
    public Dao<Vehicle, Integer> getOrmLiteVehicleDao() throws DatabaseAccessFailedException {
        try {
            return DaoManager.createDao(OrmLiteConnectionSingleton.getInstance(), Vehicle.class);
        } catch (SQLException e) {
            throw new DatabaseAccessFailedException(e.getMessage());
        }
    }

    /**
     * Generates an ORMLite data access object for the Booking table
     *
     * @return ORMlite Booking dao object
     * @throws DatabaseAccessFailedException if connection failed
     */
    public Dao<Booking, Integer> getOrmLiteBookingDao() throws DatabaseAccessFailedException {
        try {
            return DaoManager.createDao(OrmLiteConnectionSingleton.getInstance(), Booking.class);
        } catch (SQLException e) {
            throw new DatabaseAccessFailedException(e.getMessage());
        }
    }

    /**
     * Generates an ORMLite data access object for the Branch table
     *
     * @return ORMlite Branch dao object
     * @throws DatabaseAccessFailedException if connection failed
     */
    public Dao<Branch, Integer> getOrmLiteBranchDao() throws DatabaseAccessFailedException {
        try {
            return DaoManager.createDao(OrmLiteConnectionSingleton.getInstance(), Branch.class);
        } catch (SQLException e) {
            throw new DatabaseAccessFailedException(e.getMessage());
        }
    }

    /**
     * Generates an ORMLite data access object for the VehicleMove table
     *
     * @return ORMlite VehicleMove dao object
     * @throws DatabaseAccessFailedException if connection failed
     */
    public Dao<VehicleMove, Integer> getOrmLiteVehicleMoveDao() throws DatabaseAccessFailedException {
        try {
            return DaoManager.createDao(OrmLiteConnectionSingleton.getInstance(), VehicleMove.class);
        } catch (SQLException e) {
            throw new DatabaseAccessFailedException(e.getMessage());
        }
    }

    /**
     * Creates the Branch table and generates default values
     *
     * @param dao ORMLite branch data access object
     */
    private void initializeBranchTable(Dao<Branch, Integer> dao) {
        try {
            TableUtils.createTable(OrmLiteConnectionSingleton.getInstance(), Branch.class);
            dao.create(new Branch(1, "London", 51.507351, -0.127758));
            dao.create(new Branch(2, "Paris", 48.856614, 2.352222));
            dao.create(new Branch(3, "Madrid", 40.416775, -3.703790));
            dao.create(new Branch(4, "Berlin", 52.520007, 13.404954));
        } catch (SQLException e) {
            System.out.println("Error initializing branch table");
            e.printStackTrace();
        }
    }

    /**
     * Creates the User table and generates default values
     *
     * @param dao ORMLite user data access object
     */
    private void initializeUserTable(Dao<User, Integer> dao) {
        try {
            TableUtils.createTable(OrmLiteConnectionSingleton.getInstance(), User.class);
            User user = new User(true, "Administrator", "admin@car-rental.co.uk", "0745983387", CryptoUtils.Sha256Hash("password"), "8 high street", "Oxford", "0X4 8HJ", "United Kingdom");
            user.encrypt();
            dao.create(user);
            user = new User(false, "Alexis Chevalier", "alexis.chevalier1@gmail.com", "0745983387", CryptoUtils.Sha256Hash("password"), "8 high street", "Oxford", "0X4 8HJ", "United Kingdom");
            user.encrypt();
            dao.create(user);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error initializing user table");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error initializing user table");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error initializing user table");
            e.printStackTrace();
        }
    }

    /**
     * Creates the Vehicle table and generates default values
     *
     * @param dao ORMLite vehicle data access object
     */
    private void initializeVehicleTable(Dao<Vehicle, Integer> dao) {
        try {
            TableUtils.createTable(OrmLiteConnectionSingleton.getInstance(), Vehicle.class);

            Dao<Branch, Integer> branchDao = getOrmLiteBranchDao();
            List<Branch> branches = branchDao.queryForAll();

            List<String> cars = new ArrayList<>();
            cars.add("Acura Integra");
            cars.add("Acura SLX");
            cars.add("Alfa Romeo GTV-6");
            cars.add("Alfa Romeo Milano");
            cars.add("Peugeot 206");
            cars.add("Renault Clio");
            cars.add("Chrysler Aspen");
            cars.add("Chrysler Voyager");
            cars.add("Dodge Viper");
            cars.add("Dodge Lancer");
            cars.add("Ford Fiesta");
            cars.add("Ford Mustang");
            cars.add("Suzuki Forenza");
            cars.add("Volvo S60");

            Random random = new Random();

            int i = 0;
            int randomNumber;
            for (Branch branch : branches) {
                randomNumber = random.nextInt(cars.size());
                i++;
                dao.create(new Vehicle(branch, Vehicle.Status.AVAILABLE.getCode(), Vehicle.Type.SMALL_CAR.getCode(), branch.getName().toUpperCase() + "-" + Integer.toString(i), 2, 4, false, (5 + Math.floor((random.nextDouble() * 10) * 100) / 100), cars.get(randomNumber)));
                randomNumber = random.nextInt(cars.size());
                i++;
                dao.create(new Vehicle(branch, Vehicle.Status.AVAILABLE.getCode(), Vehicle.Type.FAMILY_CAR.getCode(), branch.getName().toUpperCase() + "-" + Integer.toString(i), 2, 4, false, (5 + Math.floor((random.nextDouble() * 10) * 100) / 100), cars.get(randomNumber)));
                randomNumber = random.nextInt(cars.size());
                i++;
                dao.create(new Vehicle(branch, Vehicle.Status.AVAILABLE.getCode(), Vehicle.Type.SMALL_VAN.getCode(), branch.getName().toUpperCase() + "-" + Integer.toString(i), 2, 4, false, (5 + Math.floor((random.nextDouble() * 10) * 100) / 100), cars.get(randomNumber)));
                randomNumber = random.nextInt(cars.size());
                i++;
                dao.create(new Vehicle(branch, Vehicle.Status.AVAILABLE.getCode(), Vehicle.Type.LARGE_VAN.getCode(), branch.getName().toUpperCase() + "-" + Integer.toString(i), 2, 4, false, (5 + Math.floor((random.nextDouble() * 10) * 100) / 100), cars.get(randomNumber)));
            }
        } catch (SQLException e) {
            System.out.println("Error initializing vehicle table");
            e.printStackTrace();
        } catch (DatabaseAccessFailedException e) {
            System.out.println("Error initializing vehicle table");
            e.printStackTrace();
        }
    }

    /**
     * Creates the VehicleMove table and generates default values
     *
     * @param dao ORMLite vehicleMove data access object
     */
    private void initializeVehicleMoveTable(Dao<VehicleMove, Integer> dao) {
        try {
            TableUtils.createTable(OrmLiteConnectionSingleton.getInstance(), VehicleMove.class);
        } catch (SQLException e) {
            System.out.println("Error initializing vehicle_move table");
            e.printStackTrace();
        }
    }

    /**
     * Creates the Booking table and generates default values
     *
     * @param dao ORMLite booking data access object
     */
    private void initializeBookingTable(Dao<Booking, Integer> dao) {
        try {
            TableUtils.createTable(OrmLiteConnectionSingleton.getInstance(), Booking.class);
        } catch (SQLException e) {
            System.out.println("Error initializing user table");
            e.printStackTrace();
        }
    }
}
