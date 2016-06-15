/**
 * CarRental
 *
 * This file provides a common interface for the booking business methods
 */

package com.vehiclerental.logicLayer.interfaces;

import com.vehiclerental.contracts.*;
import com.vehiclerental.dataLayer.entities.Branch;
import com.vehiclerental.dataLayer.entities.User;
import com.vehiclerental.exceptions.*;
import mpi.MPIException;

import java.util.List;

public interface BookingService {
    /**
     * Returns all the bookings for a specific user and for a specific branch
     *
     * @param user specified user
     * @param currentBranch specified branch
     * @return the list of bookings matching the criteria
     */
    List<BookingContract> getUserBookingsForBranch(User user, Branch currentBranch);

    /**
     * Returns all the bookings for a specific branch
     *
     * @param currentBranch specified branch
     * @return the list of bookings matching the criteria
     */
    List<BookingContract> getBookingsForBranch(Branch currentBranch);

    /**
     * Creates a booking according to the business rules
     * This method may involve cluster communication
     *
     * Important : The booking is ALWAYS created by the branch owning the vehicle booking in order to avoid latency between the availability check and the booking creation
     *
     * @param user the user creating the booking
     * @param currentBranch the current branch
     * @param contract the booking parameters
     * @return The created booking, as a contract
     * @throws InvalidPropertyException if a property is invalid
     * @throws NotAuthorizedException if the user is not authorized for this operation (booking creation for himself OR for someone else)
     * @throws VehicleUnavailableException if the desired vehicle is unavailable
     * @throws ClusterCommunicatorException if the cluster communication fails
     * @throws InvalidDateException if the booking dates are invalid
     */
    BookingContract createBooking(int user, Branch currentBranch, CreateBookingContract contract) throws InvalidPropertyException, NotAuthorizedException, VehicleUnavailableException, ClusterCommunicatorException, InvalidDateException;

    /**
     * Returns the expected moves for the specified branch following the given criteria
     *
     * @param criteria search criteria
     * @param nodeBranch specified branch
     * @return a list of booking involving matches
     * @throws InvalidPropertyException if one of the criteria properties is invalid
     */
    List<BookingContract> getVehicleMoves(GetBranchVehicleMovesContract criteria, Branch nodeBranch) throws InvalidPropertyException;

    /**
     * Update the status of a given booking
     *
     * @param updateProperties booking update details
     * @param nodeBranch branch of the booking
     * @return the updated booking, as a contract
     * @throws InvalidPropertyException if a property is invalid
     * @throws VehicleUnavailableException if the booking was invalidated and the vehicle is now unavailable
     * @throws DatabaseUpdateFailedException if the database access failed
     * @throws InvalidDateException if the booking dates are invalid
     */
    BookingContract changeBookingStatus(ChangeBookingStatusContract updateProperties, Branch nodeBranch) throws InvalidPropertyException, VehicleUnavailableException, DatabaseUpdateFailedException, InvalidDateException;
}
