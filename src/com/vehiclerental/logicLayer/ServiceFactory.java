/**
 * CarRental
 *
 * This file provides an initialization class, giving access to every service objects from the other layers
 */

package com.vehiclerental.logicLayer;

import com.vehiclerental.exceptions.DatabaseAccessFailedException;
import com.vehiclerental.logicLayer.implementations.BookingServiceImpl;
import com.vehiclerental.logicLayer.implementations.BranchServiceImpl;
import com.vehiclerental.logicLayer.implementations.UserServiceImpl;
import com.vehiclerental.logicLayer.implementations.VehicleServiceImpl;
import com.vehiclerental.logicLayer.interfaces.BookingService;
import com.vehiclerental.logicLayer.interfaces.BranchService;
import com.vehiclerental.logicLayer.interfaces.UserService;
import com.vehiclerental.logicLayer.interfaces.VehicleService;

public class ServiceFactory {

    /**
     * Returns an instantiated Vehicle service
     *
     * @throws DatabaseAccessFailedException if the database is not usable
     */
    public static VehicleService getVehicleService() throws DatabaseAccessFailedException {
        return new VehicleServiceImpl();
    }

    /**
     * Returns an instantiated User service
     *
     * @throws DatabaseAccessFailedException if the database is not usable
     */
    public static UserService getUserService() throws DatabaseAccessFailedException {
        return new UserServiceImpl();
    }

    /**
     * Returns an instantiated Branch service
     *
     * @throws DatabaseAccessFailedException if the database is not usable
     */
    public static BranchService getBranchService() throws DatabaseAccessFailedException {
        return new BranchServiceImpl();
    }

    /**
     * Returns an instantiated Booking service
     *
     * @throws DatabaseAccessFailedException if the database is not usable
     */
    public static BookingService getBookingService() throws DatabaseAccessFailedException {
        return new BookingServiceImpl();
    }
}
