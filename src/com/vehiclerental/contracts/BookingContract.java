/**
 * CarRental
 *
 * This file provides a light communication object representing the state of a booking
 */

package com.vehiclerental.contracts;

import com.vehiclerental.dataLayer.entities.Booking;
import com.vehiclerental.utils.DateUtils;

public class BookingContract {
    public int id;
    public String branch;
    public VehicleContract vehicle;
    public VehicleMoveContract vehicleMove;
    public String pickupDate;
    public String returnDate;
    public long daysCount;
    public double price;
    public boolean bookingValidated;
    public boolean requireVehicleMove;

    /**
     * Transforms a server-side booking to a communication contract
     *
     * @param booking the complete booking
     */
    public BookingContract(Booking booking) {
        this.id = booking.getId();
        this.branch = booking.getBranch().getName();
        if (booking.getVehicle() != null) {
            this.vehicle = new VehicleContract(booking.getVehicle());
        }
        if (booking.getVehicleMove() != null) {
            this.vehicleMove = new VehicleMoveContract(booking.getVehicleMove());
        }
        this.pickupDate = DateUtils.getIso8601DateString(booking.getPickUpDate());
        this.returnDate = DateUtils.getIso8601DateString(booking.getReturnDate());
        this.daysCount = booking.getDaysCount();
        this.price = Math.round(booking.getPrice() * 100.0) / 100.0;
        this.bookingValidated = booking.getBookingValidated();
        this.requireVehicleMove = booking.getVehicleMove() != null;
    }
}
