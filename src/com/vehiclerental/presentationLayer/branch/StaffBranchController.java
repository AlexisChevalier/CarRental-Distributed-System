/**
 * CarRental
 *
 * This file provides the branch offices presentation layer methods which require a minimal authorization level of staff
 */

package com.vehiclerental.presentationLayer.branch;

import com.vehiclerental.NodeConfiguration;
import com.vehiclerental.contracts.*;
import com.vehiclerental.dataLayer.entities.Booking;
import com.vehiclerental.exceptions.InvalidPropertyException;
import com.vehiclerental.exceptions.RegistrationNumberAlreadyInUseException;
import com.vehiclerental.logicLayer.ServiceFactory;
import com.vehiclerental.logicLayer.interfaces.BookingService;
import com.vehiclerental.logicLayer.interfaces.VehicleService;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchRequestMessage;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchResponseMessage;
import com.vehiclerental.utils.SerializationUtils;

import java.util.List;

public class StaffBranchController extends BaseBranchController {

    /**
     * Shutdowns the specified branch
     *
     * @param request the request parameters
     * @return the result response
     */
    public static BranchResponseMessage handleBranchShutdown(BranchRequestMessage<Void> request) {

        NodeConfiguration.Current.systemAvailable = false;
        NodeConfiguration.Current.branchOfficeManager.stopAfterCurrentRequest();

        return generateSuccessfulResponse(request.operationCode, null);
    }

    /**
     * Fetches all the bookings for a specified branch
     *
     * @param request the request parameters
     * @return the result response
     */
    protected static BranchResponseMessage handleGetBranchBookings(BranchRequestMessage<Void> request) {
        try {
            BookingService bookingService = ServiceFactory.getBookingService();

            List<BookingContract> bookingContracts = bookingService.getBookingsForBranch(NodeConfiguration.Current.nodeBranch);

            return generateSuccessfulResponse(request.operationCode, bookingContracts);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return generateError(request.operationCode, 500, "Branch error");
        }
    }

    /**
     * Creates or update a vehicle based on the given properties
     *
     * @param request the request parameters
     * @return the result response
     */
    public static BranchResponseMessage handleUpdateOrCreateVehicle(BranchRequestMessage<CreateUpdateVehicleContract> request) {
        try {
            VehicleService vehicleService = ServiceFactory.getVehicleService();

            VehicleContract vehicleContract = vehicleService.createOrUpdateVehicle(request.object, NodeConfiguration.Current.nodeBranch);

            return generateSuccessfulResponse(request.operationCode, vehicleContract);
        } catch (InvalidPropertyException e) {
            return generateError(request.operationCode, 400, e.getMessage());
        } catch (RegistrationNumberAlreadyInUseException e) {
            return generateError(request.operationCode, 400, "Registration number already in use");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return generateError(request.operationCode, 500, "Branch error");
        }
    }

    /**
     * Search for vehicles in the given branch according to the given parameters
     *
     * @param request the request parameters
     * @return the result response
     */
    public static BranchResponseMessage handleSearchVehicles(BranchRequestMessage<SearchVehicleContract> request) {
        try {
            VehicleService vehicleService = ServiceFactory.getVehicleService();

            List<VehicleContract> vehicleContracts = vehicleService.searchVehicles(request.object, NodeConfiguration.Current.nodeBranch);

            return generateSuccessfulResponse(request.operationCode, vehicleContracts);
        } catch (InvalidPropertyException e) {
            return generateError(request.operationCode, 400, e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return generateError(request.operationCode, 500, "Branch error");
        }
    }

    /**
     * Fetches all the future vehicle moves for the specified branch and parameters
     *
     * @param request the request parameters
     * @return the result response
     */
    public static BranchResponseMessage handleGetVehicleMoves(BranchRequestMessage<GetBranchVehicleMovesContract> request) {
        try {
            BookingService bookingService = ServiceFactory.getBookingService();

            List<BookingContract> moves = bookingService.getVehicleMoves(request.object, NodeConfiguration.Current.nodeBranch);

            return generateSuccessfulResponse(request.operationCode, moves);
        } catch (InvalidPropertyException e) {
            return generateError(request.operationCode, 400, e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return generateError(request.operationCode, 500, "Branch error");
        }
    }

    /**
     * Updates the booking status for a specific booking with the given properties
     *
     * @param request the request parameters
     * @return the result response
     */
    public static BranchResponseMessage handleChangeBookingStatus(BranchRequestMessage<ChangeBookingStatusContract> request) {
        try {
            BookingService bookingService = ServiceFactory.getBookingService();

            BookingContract bookingContract = bookingService.changeBookingStatus(request.object, NodeConfiguration.Current.nodeBranch);

            return generateSuccessfulResponse(request.operationCode, bookingContract);
        } catch (InvalidPropertyException e) {
            return generateError(request.operationCode, 400, e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return generateError(request.operationCode, 500, "Branch error");
        }
    }
}
