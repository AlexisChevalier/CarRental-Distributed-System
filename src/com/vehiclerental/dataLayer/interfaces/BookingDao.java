/**
 * CarRental
 *
 * This file provides a common interface for the booking-specific data access methods
 * It implements a specialized version of the BaseDao, providing the basic data access methods for a Booking entity
 */

package com.vehiclerental.dataLayer.interfaces;

import com.vehiclerental.dataLayer.entities.Booking;
import com.vehiclerental.dataLayer.entities.Branch;
import com.vehiclerental.dataLayer.entities.User;

import java.util.List;

public interface BookingDao extends BaseDao<Booking> {
    /**
     * Returns all the bookings for a given branch and a given user
     *
     * @param branch the specified branch
     * @param user the specified user
     * @return a list of the matching bookings
     */
    List<Booking> getBranchBookingsForUser(Branch branch, User user);

    /**
     * Returns all the bookings for a given branch
     *
     * @param currentBranch the specified branch
     * @return a list of the matching bookings
     */
    List<Booking> getBranchBookings(Branch currentBranch);

    /**
     * Returns all the bookings requiring a move (outgoing OR incoming) for a given branch
     *
     * @param nodeBranch the specified branch
     * @param outgoing if true, only the outgoing moves will be returned, if false, only the incoming ones
     * @return a list of the matching bookings
     */
    List<Booking> getBookingsRequiringMovesForBranch(Branch nodeBranch, boolean outgoing);
}
