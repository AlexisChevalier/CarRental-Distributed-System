/**
 * CarRental
 *
 * This file provides the branch offices presentation layer methods which require a minimal authorization level of guest
 */

package com.vehiclerental.presentationLayer.branch;

import com.vehiclerental.NodeConfiguration;
import com.vehiclerental.contracts.BookingSearchResultContract;
import com.vehiclerental.contracts.SearchAvailableVehiclesRequestContract;
import com.vehiclerental.exceptions.ClusterCommunicatorException;
import com.vehiclerental.exceptions.DatabaseAccessFailedException;
import com.vehiclerental.exceptions.InvalidDateException;
import com.vehiclerental.logicLayer.ServiceFactory;
import com.vehiclerental.logicLayer.interfaces.VehicleService;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchRequestMessage;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchResponseMessage;

import java.util.List;

public class GuestBranchController extends BaseBranchController {

    /**
     * Search for the available vehicles in the current branch and optionally across the system and returns the results
     *
     * @param request request criteria
     * @param requireBroadcast true if should search in other branches
     * @return the result response
     */
    public static BranchResponseMessage handleSearchAvailableVehicles(BranchRequestMessage<SearchAvailableVehiclesRequestContract> request, boolean requireBroadcast) {
        try {
            /* Service calls */
            VehicleService vehicleService = ServiceFactory.getVehicleService();
            List<BookingSearchResultContract> vehicleContracts;

            //Search database for current branch (the vehicle found in the first branch (not broacasted) will not require a move)
            vehicleContracts = vehicleService.searchAvailableVehicles(request.object, NodeConfiguration.Current.nodeBranch, !requireBroadcast);

            if (requireBroadcast) {
                //Search other branches
                vehicleContracts.addAll(vehicleService.broadcastSearchAvailableVehicles(request.object, request.userId));
            }

            /* Handle response */
            return generateSuccessfulResponse(request.operationCode, vehicleContracts);
        } catch (InvalidDateException e) {
            return generateError(request.operationCode, 400, e.getMessage());
        } catch (DatabaseAccessFailedException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return generateError(request.operationCode, 500, "Branch error");
        } catch (ClusterCommunicatorException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return generateError(request.operationCode, 500, "Branch error");
        }
    }
}
