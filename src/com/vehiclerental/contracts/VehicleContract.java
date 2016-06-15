/**
 * CarRental
 *
 * This file provides a light communication object representing the state of a vehicle
 */

package com.vehiclerental.contracts;

import com.vehiclerental.dataLayer.entities.Vehicle;

public class VehicleContract {

    /**
     * Transforms a server vehicle into a contract object
     * @param vehicle the vehicle
     */
    public VehicleContract(Vehicle vehicle) {
        if (vehicle != null) {
            id = vehicle.getId();
            branch = new BranchContract(vehicle.getBranch());
            type = vehicle.getType();
            status = vehicle.getStatus();
            registrationNumber = vehicle.getRegistrationNumber();
            doors = vehicle.getDoors();
            seats = vehicle.getSeats();
            automaticTransmission = vehicle.isAutomaticTransmission();
            poundsPerDay = Math.round(vehicle.getPoundsPerDay() * 100.0) / 100.0;
            name = vehicle.getName();
        }
    }

    public int id;
    public BranchContract branch;
    public int type;
    public int status;
    public String registrationNumber;
    public int doors;
    public int seats;
    public boolean automaticTransmission;
    public double poundsPerDay;
    public String name;
}
