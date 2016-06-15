/**
 * CarRental
 *
 * This file provides a common interface for the vehicle business methods
 */

package com.vehiclerental.logicLayer.interfaces;

import com.vehiclerental.contracts.*;
import com.vehiclerental.dataLayer.entities.Branch;
import com.vehiclerental.exceptions.ClusterCommunicatorException;
import com.vehiclerental.exceptions.InvalidDateException;
import com.vehiclerental.exceptions.InvalidPropertyException;
import com.vehiclerental.exceptions.RegistrationNumberAlreadyInUseException;

import java.util.List;

public interface VehicleService {
    /**
     * Returns all the available vehicles in a specific branch for the given criteria
     * Takes in account if the branch is the booking branch or not and if it should apply an additional move time or not
     *
     * @param searchCriteriaContract the search criteria details
     * @param currentBranch the current branch
     * @param requiresMove true if it requires a vehicle move for this branch
     * @return the list of the available vehicles
     * @throws InvalidDateException if one of the dates is invalid
     */
    List<BookingSearchResultContract> searchAvailableVehicles(SearchAvailableVehiclesRequestContract searchCriteriaContract, Branch currentBranch, boolean requiresMove) throws InvalidDateException;

    /**
     * Broadcasts the available vehicle search to all the other branch nodes and returns the combined results
     *
     * @param searchCriteriaContract the search criteria details
     * @param currentUserId the ID of the requesting user
     * @return the list of the combined available vehicles
     * @throws ClusterCommunicatorException if one of the dates is invalid
     */
    List<BookingSearchResultContract> broadcastSearchAvailableVehicles(SearchAvailableVehiclesRequestContract searchCriteriaContract, int currentUserId) throws ClusterCommunicatorException;

    /**
     * Returns all the vehicles matching a search criteria in the given branch
     * This is not an available search, only a general vehicle search
     *
     * @param searchCriteria the search criteria
     * @param nodeBranch the given branch
     * @return the list of vehicles matching the criteria in the branch
     * @throws InvalidPropertyException if one of the search criteria properties is invalid
     */
    List<VehicleContract> searchVehicles(SearchVehicleContract searchCriteria, Branch nodeBranch) throws InvalidPropertyException;

    /**
     * Creates or Update a vehicle in the given branch corresponding to the given properties
     *
     * @param createUpdateVehicleContract the given properties
     * @param nodeBranch the given branch
     * @return The created vehicle
     * @throws InvalidPropertyException if one of the parameters is invalid
     * @throws RegistrationNumberAlreadyInUseException if the registration number provied to create a vehicle is already in use
     */
    VehicleContract createOrUpdateVehicle(CreateUpdateVehicleContract createUpdateVehicleContract, Branch nodeBranch) throws InvalidPropertyException, RegistrationNumberAlreadyInUseException;
}
