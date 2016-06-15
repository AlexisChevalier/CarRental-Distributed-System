/**
 * CarRental
 *
 * This file provides the head office presentation layer methods which require a minimal authorization level of user
 */

package com.vehiclerental.presentationLayer.headOffice;

import com.google.gson.reflect.TypeToken;
import com.vehiclerental.clusterCommunicator.ClusterCommunicationObject;
import com.vehiclerental.contracts.*;
import com.vehiclerental.dataLayer.entities.Branch;
import com.vehiclerental.dataLayer.entities.User;
import com.vehiclerental.exceptions.ClusterCommunicatorException;
import com.vehiclerental.exceptions.NotAuthorizedException;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchResponseMessage;
import com.vehiclerental.presentationLayer.headOffice.protocolContracts.HeadOfficeRequestMessage;
import com.vehiclerental.presentationLayer.headOffice.protocolContracts.HeadOfficeResponseMessage;
import com.vehiclerental.utils.SerializationUtils;

import java.lang.reflect.Type;
import java.util.List;

public class UserHeadOfficeController extends BaseHeadOfficeController {

    /**
     * Get the account details for the authenticated user
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleGetAccountDetails(HeadOfficeRequestMessage request) {
        User user;

        try {
            user = doAuthentication(request.getAuthEmail(), request.getAuthPassword(), false);

            //Form socket response
            return generateSuccessfulResponse(request.OperationCode, new UserContract(user));
        } catch (NotAuthorizedException e) {
            return generateError(request.OperationCode, 401, "Unauthorized");
        }
    }

    /**
     * Get the user bookings for the authenticated user in the specified branch
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleGetUserBookings(HeadOfficeRequestMessage request) {
        Branch destBranch = getRequestBranch(request);

        if (destBranch == null) {
            return generateError(request.OperationCode, 404, "Branch not found");
        }

        try {
            User user = doAuthentication(request.getAuthEmail(), request.getAuthPassword(), false);

            ClusterCommunicationObject<String> rawClusterResponse = ForwardRequestToBranch(destBranch, request.OperationCode, null, null, user);

            //Parse cluster response
            Type jsonType = new TypeToken<BranchResponseMessage<List<BookingContract>>>() {}.getType();
            BranchResponseMessage<List<BookingContract>> clusterResponse = SerializationUtils.deserialize(rawClusterResponse.object, jsonType);

            if (clusterResponse.Status != 200) {
                return generateError(request.OperationCode, clusterResponse.Status, clusterResponse.Error);
            }

            //Form socket response
            return generateSuccessfulResponse(request.OperationCode, clusterResponse.Object);
        } catch (ClusterCommunicatorException e) {
            return generateError(request.OperationCode, 500, "Server error");
        } catch (NotAuthorizedException e) {
            return generateError(request.OperationCode, 401, "Unauthorized");
        }
    }

    /**
     * Book a vehicle for the authenticated user in the given branch with the given properties
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleBookVehicle(HeadOfficeRequestMessage request) {
        Branch destBranch = getRequestBranch(request);

        if (destBranch == null) {
            return generateError(request.OperationCode, 404, "Branch not found");
        }

        try {
            User user;

            CreateBookingContract createBookingContract = SerializationUtils.deserialize(request.SerializedObject, CreateBookingContract.class);

            if (createBookingContract.bookingOwnerUserId != null) {
                //In this case, the booking is made for someone, so we require staff credentials
                user = doAuthentication(request.getAuthEmail(), request.getAuthPassword(), true);
            } else {
                //User credentials are required otherwise
                user = doAuthentication(request.getAuthEmail(), request.getAuthPassword(), false);
            }

            ClusterCommunicationObject<String> rawClusterResponse = ForwardRequestToBranch(destBranch, request.OperationCode, createBookingContract, user);

            //Parse cluster response
            Type jsonType = new TypeToken<BranchResponseMessage<BookingContract>>() {}.getType();
            BranchResponseMessage<BookingContract> clusteResponse = SerializationUtils.deserialize(rawClusterResponse.object, jsonType);

            if (clusteResponse.Status != 200) {
                return generateError(request.OperationCode, clusteResponse.Status, clusteResponse.Error);
            }

            //Form socket response
            return generateSuccessfulResponse(request.OperationCode, clusteResponse.Object);
        } catch (ClusterCommunicatorException e) {
            //e.printStackTrace();
            return generateError(request.OperationCode, 500, "Server error");
        } catch (NotAuthorizedException e) {
            return generateError(request.OperationCode, 401, "Unauthorized");
        }
    }
}
