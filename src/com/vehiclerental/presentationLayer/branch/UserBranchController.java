/**
 * CarRental
 *
 * This file provides the branch offices presentation layer methods which require a minimal authorization level of user
 */

package com.vehiclerental.presentationLayer.branch;

import com.vehiclerental.NodeConfiguration;
import com.vehiclerental.contracts.BookingContract;
import com.vehiclerental.contracts.CreateBookingContract;
import com.vehiclerental.dataLayer.entities.User;
import com.vehiclerental.exceptions.*;
import com.vehiclerental.logicLayer.ServiceFactory;
import com.vehiclerental.logicLayer.interfaces.BookingService;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchRequestMessage;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchResponseMessage;

import java.util.List;

public class UserBranchController extends BaseBranchController {

    /**
     * Create a booking in the specified branch with the given criteria
     *
     * @param request the request parameters
     * @return the result response
     */
    public static BranchResponseMessage handleCreateBooking(BranchRequestMessage<CreateBookingContract> request) {
        try {
            /* Service calls */
            BookingService bookingService = ServiceFactory.getBookingService();

            BookingContract bookingContract = bookingService.createBooking(request.userId, NodeConfiguration.Current.nodeBranch, request.object);

            /* Handle response */
            return generateSuccessfulResponse(request.operationCode, bookingContract);
        } catch (InvalidPropertyException e) {
            return generateError(request.operationCode, 400, e.getMessage());
        } catch (NotAuthorizedException e) {
            return generateError(request.operationCode, 401, "Unauthorized");
        } catch (VehicleUnavailableException e) {
            return generateError(request.operationCode, 400, "Vehicle unavailable");
        } catch (InvalidDateException e) {
            return generateError(request.operationCode, 400, "Invalid dates");
        } catch (ClusterCommunicatorException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return generateError(request.operationCode, 500, "Branch error");
        } catch (DatabaseAccessFailedException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return generateError(request.operationCode, 500, "Branch error");
        }
    }

    /**
     * Fetches all the user bookings for a specified branch
     *
     * @param request the request parameters
     * @return the result response
     */
    public static BranchResponseMessage handleGetUserBookings(BranchRequestMessage<Void> request) {
        try {
            BookingService bookingService = ServiceFactory.getBookingService();

            User userWrapper = new User();
            userWrapper.setId(request.userId);

            List<BookingContract> bookingContracts = bookingService.getUserBookingsForBranch(userWrapper, NodeConfiguration.Current.nodeBranch);

            return generateSuccessfulResponse(request.operationCode, bookingContracts);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return generateError(request.operationCode, 500, "Branch error");
        }
    }
}
