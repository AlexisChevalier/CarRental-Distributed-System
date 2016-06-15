/**
 * CarRental
 *
 * This file provides the head office presentation layer methods which require a minimal authorization level of guest
 */

package com.vehiclerental.presentationLayer.headOffice;

import com.google.gson.reflect.TypeToken;
import com.vehiclerental.NodeConfiguration;
import com.vehiclerental.clusterCommunicator.ClusterCommunicationObject;
import com.vehiclerental.contracts.*;
import com.vehiclerental.dataLayer.entities.Branch;
import com.vehiclerental.dataLayer.entities.User;
import com.vehiclerental.exceptions.ClusterCommunicatorException;
import com.vehiclerental.exceptions.DatabaseAccessFailedException;
import com.vehiclerental.exceptions.EmailAlreadyInUseException;
import com.vehiclerental.exceptions.InvalidPropertyException;
import com.vehiclerental.logicLayer.ServiceFactory;
import com.vehiclerental.logicLayer.interfaces.UserService;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchResponseMessage;
import com.vehiclerental.presentationLayer.headOffice.protocolContracts.HeadOfficeRequestMessage;
import com.vehiclerental.presentationLayer.headOffice.protocolContracts.HeadOfficeResponseMessage;
import com.vehiclerental.utils.SerializationUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GuestHeadOfficeController extends BaseHeadOfficeController {
    /**
     * Search available vehicle in the specified branch with the specified parameters (The search will be extended to all the branches in the system)
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleSearchAvailableVehicles(HeadOfficeRequestMessage request) throws Exception {
        Branch destBranch = NodeConfiguration.Current.branches.get(request.BranchId);

        if (destBranch == null) {
            return generateError(request.OperationCode, 404, "Branch not found");
        }

        try {
            ClusterCommunicationObject<String> rawClusterResponse = ForwardRequestToBranch(destBranch, request.OperationCode, request.SerializedObject, SearchAvailableVehiclesRequestContract.class, null);

            //Parse MPI response
            Type jsonType = new TypeToken<BranchResponseMessage<List<BookingSearchResultContract>>>() {}.getType();
            BranchResponseMessage<List<BookingSearchResultContract>> clusterResponse = SerializationUtils.deserialize(rawClusterResponse.object, jsonType);

            if (clusterResponse.Status != 200) {
                return generateError(request.OperationCode, clusterResponse.Status, clusterResponse.Error);
            }

            //Form socket response
            return generateSuccessfulResponse(request.OperationCode, clusterResponse.Object);
        } catch (ClusterCommunicatorException e) {
            //e.printStackTrace();
            return generateError(request.OperationCode, 500, "Server error");
        }
    }

    /**
     * Get the list of available branches
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleGetBranches(HeadOfficeRequestMessage request) {
        List<BranchContract> branchContracts = new ArrayList<BranchContract>();

        /*
            Branches are always in memory, no need to proceed to an SQL query
            Even if a branch have been added by an external user during the runtime of the system,
            The branch wouldn't be able to run on a node without a complete restart of the system.
        */
        for (Branch branch : NodeConfiguration.Current.branches.values()) {
            branchContracts.add(new BranchContract(branch));
        }

        //Form socket response
        return generateSuccessfulResponse(request.OperationCode, branchContracts);
    }

    /**
     * Create an user account with the given parameters
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleCreateAccount(HeadOfficeRequestMessage request) {

        CreateAccountRequestContract createAccountRequestContract = SerializationUtils.deserialize(request.SerializedObject, CreateAccountRequestContract.class);

        if (createAccountRequestContract == null) {
            return generateError(request.OperationCode, 400, "Bad request");
        }

        try {
            /* Service calls */
            UserService userService = ServiceFactory.getUserService();

            User user = userService.createUser(createAccountRequestContract);

            //Form socket response
            return generateSuccessfulResponse(request.OperationCode, new UserContract(user));
        } catch (EmailAlreadyInUseException e) {
            return generateError(request.OperationCode, 400, "Email already in use");
        } catch (InvalidPropertyException e) {
            return generateError(request.OperationCode, 400, e.getMessage());
        } catch (DatabaseAccessFailedException e) {
            return generateError(request.OperationCode, 500, "Server error");
        }
    }
}
