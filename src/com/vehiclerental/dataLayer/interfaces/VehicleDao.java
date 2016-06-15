/**
 * CarRental
 *
 * This file provides a common interface for the vehicle-specific data access methods
 * It implements a specialized version of the BaseDao, providing the basic data access methods for a Vehicle entity
 */

package com.vehiclerental.dataLayer.interfaces;

import com.vehiclerental.dataLayer.entities.Branch;
import com.vehiclerental.dataLayer.entities.Vehicle;

import java.util.Calendar;
import java.util.List;

public interface VehicleDao extends BaseDao<Vehicle> {

    /**
     * Return the specified vehicle if it is available for the given criteria
     *
     * @param branch the vehicle branch
     * @param vehicleId the vehicle ID
     * @param startDate the start date of the booking
     * @param endDate the end date of the booking
     * @return the vehicle, if available, null if the vehicle is not found or unavailable
     */
    Vehicle getVehicleIfAvailable(Branch branch, int vehicleId, Calendar startDate, Calendar endDate);

    /**
     * Search for all the vehicle available for the given criteria
     *
     * @param branch the vehicles branch
     * @param type the vehicles type
     * @param pickupDate the start date of the booking
     * @param returnDate the end date of the booking
     * @return a list of the matched vehicles
     */
    List<Vehicle> getAvailableVehicles(Branch branch, Integer type, Calendar pickupDate, Calendar returnDate);

    /**
     * Returns all the vehicles matching a type in the specified branch
     *
     * @param vehicleTypeId the type ID
     * @param nodeBranch the branch
     * @return a list of the matched vehicles
     */
    List<Vehicle> searchBranchVehiclesByType(int vehicleTypeId, Branch nodeBranch);

    /**
     * Search for a vehicle with a specific registration number in a given branch
     * @param registrationNumber the registration number
     * @param nodeBranch the given branch
     * @return the vehicle, if existing, null if not
     */
    Vehicle getVehicleByRegistrationNumberAndBranch(String registrationNumber, Branch nodeBranch);
}

