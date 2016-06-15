/**
 * CarRental
 *
 * This file provides the head office presentation layer methods which require a minimal authorization level of staff
 */

package com.vehiclerental.presentationLayer.headOffice;

import com.google.gson.reflect.TypeToken;
import com.vehiclerental.NodeConfiguration;
import com.vehiclerental.clusterCommunicator.ClusterCommunicationObject;
import com.vehiclerental.contracts.*;
import com.vehiclerental.dataLayer.entities.Branch;
import com.vehiclerental.dataLayer.entities.User;
import com.vehiclerental.exceptions.*;
import com.vehiclerental.logicLayer.ServiceFactory;
import com.vehiclerental.logicLayer.interfaces.UserService;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchResponseMessage;
import com.vehiclerental.presentationLayer.headOffice.protocolContracts.HeadOfficeRequestMessage;
import com.vehiclerental.presentationLayer.headOffice.protocolContracts.HeadOfficeResponseMessage;
import com.vehiclerental.utils.SerializationUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StaffHeadOfficeController extends BaseHeadOfficeController {

    /**
     * Get the bookings for the specified branch
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleGetBranchBookings(HeadOfficeRequestMessage request) {
        Branch destBranch = getRequestBranch(request);

        if (destBranch == null) {
            return generateError(request.OperationCode, 404, "Branch not found");
        }

        try {
            User user = doAuthentication(request.getAuthEmail(), request.getAuthPassword(), true);

            ClusterCommunicationObject<String> rawMpiResponse = ForwardRequestToBranch(destBranch, request.OperationCode, null, null, user);

            //Parse MPI response
            Type jsonType = new TypeToken<BranchResponseMessage<List<BookingContract>>>() {}.getType();
            BranchResponseMessage<List<BookingContract>> mpiResponse = SerializationUtils.deserialize(rawMpiResponse.object, jsonType);

            if (mpiResponse.Status != 200) {
                return generateError(request.OperationCode, mpiResponse.Status, mpiResponse.Error);
            }

            //Form socket response
            return generateSuccessfulResponse(request.OperationCode, mpiResponse.Object);
        } catch (ClusterCommunicatorException e) {
            return generateError(request.OperationCode, 500, "Server error");
        } catch (NotAuthorizedException e) {
            return generateError(request.OperationCode, 401, "Unauthorized");
        }
    }

    /**
     * Broadcasts the shutdown request to all the branches and then shut down the head office server
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleShutdownSystem(HeadOfficeRequestMessage request) {
        try {
            User user = doAuthentication(request.getAuthEmail(), request.getAuthPassword(), true);

            Collection<Branch> branches = NodeConfiguration.Current.branches.values();

            for (Branch branch : branches) {
                //We do not handle errors because the single thread nature of the system makes it very unlikely to fail on shutdown
                ForwardRequestToBranch(branch, request.OperationCode, null, null, user);
            }

            NodeConfiguration.Current.systemAvailable = false;
            //Everything will go down after this request
            /*
                It's actually not possible to finalize only the branches, MPI_Finalize requires all the processes to call
                MPI_Finalize before finalizing the cluster
             */
            NodeConfiguration.Current.headOfficeManager.stopAfterCurrentRequest();

            //Form socket response
            return generateSuccessfulResponse(request.OperationCode, null);
        } catch (ClusterCommunicatorException e) {
            return generateError(request.OperationCode, 500, "Server error");
        } catch (NotAuthorizedException e) {
            return generateError(request.OperationCode, 401, "Unauthorized");
        }
    }

    /**
     * Create an user with the given properties
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleCreateUser(HeadOfficeRequestMessage request) {
        CreateAccountRequestContract createAccountRequestContract = SerializationUtils.deserialize(request.SerializedObject, CreateAccountRequestContract.class);

        if (createAccountRequestContract == null) {
            return generateError(request.OperationCode, 400, "Bad request");
        }

        try {
            doAuthentication(request.getAuthEmail(), request.getAuthPassword(), true);
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
        } catch (NotAuthorizedException e) {
            return generateError(request.OperationCode, 401, "Unauthorized");
        }
    }

    /**
     * Search vehicle for a specific branch with a given criteria
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleSearchVehicles(HeadOfficeRequestMessage request) {
        Branch destBranch = getRequestBranch(request);

        if (destBranch == null) {
            return generateError(request.OperationCode, 404, "Branch not found");
        }

        try {
            User user = doAuthentication(request.getAuthEmail(), request.getAuthPassword(), true);

            ClusterCommunicationObject<String> rawMpiResponse = ForwardRequestToBranch(destBranch, request.OperationCode, request.SerializedObject, SearchVehicleContract.class, user);

            //Parse MPI response
            Type jsonType = new TypeToken<BranchResponseMessage<List<VehicleContract>>>() {}.getType();
            BranchResponseMessage<List<VehicleContract>> mpiResponse = SerializationUtils.deserialize(rawMpiResponse.object, jsonType);

            if (mpiResponse.Status != 200) {
                return generateError(request.OperationCode, mpiResponse.Status, mpiResponse.Error);
            }

            //Form socket response
            return generateSuccessfulResponse(request.OperationCode, mpiResponse.Object);
        } catch (ClusterCommunicatorException e) {
            return generateError(request.OperationCode, 500, "Server error");
        } catch (NotAuthorizedException e) {
            return generateError(request.OperationCode, 401, "Unauthorized");
        }
    }

    /**
     * Create or update a vehicle in a given branch according to the given properties
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleUpdateOrCreateVehicle(HeadOfficeRequestMessage request) {
        Branch destBranch = getRequestBranch(request);

        if (destBranch == null) {
            return generateError(request.OperationCode, 404, "Branch not found");
        }

        try {
            User user = doAuthentication(request.getAuthEmail(), request.getAuthPassword(), true);

            ClusterCommunicationObject<String> rawMpiResponse = ForwardRequestToBranch(destBranch, request.OperationCode, request.SerializedObject, CreateUpdateVehicleContract.class, user);

            //Parse MPI response
            Type jsonType = new TypeToken<BranchResponseMessage<VehicleContract>>() {}.getType();
            BranchResponseMessage<VehicleContract> mpiResponse = SerializationUtils.deserialize(rawMpiResponse.object, jsonType);

            if (mpiResponse.Status != 200) {
                return generateError(request.OperationCode, mpiResponse.Status, mpiResponse.Error);
            }

            //Form socket response
            return generateSuccessfulResponse(request.OperationCode, mpiResponse.Object);
        } catch (ClusterCommunicatorException e) {
            return generateError(request.OperationCode, 500, "Server error");
        } catch (NotAuthorizedException e) {
            return generateError(request.OperationCode, 401, "Unauthorized");
        }
    }

    /**
     * Search user accounts matching the given criteria (the staff accounts won't be returned)
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleSearchUser(HeadOfficeRequestMessage request) {
        SearchUserContract searchUserContract = SerializationUtils.deserialize(request.SerializedObject, SearchUserContract.class);

        if (searchUserContract == null) {
            return generateError(request.OperationCode, 400, "Bad request");
        }

        try {
            doAuthentication(request.getAuthEmail(), request.getAuthPassword(), true);
            /* Service calls */
            UserService userService = ServiceFactory.getUserService();

            List<User> users = userService.searchUser(searchUserContract);

            List<UserContract> userContracts = new ArrayList<UserContract>();

            for (User user : users) {
                userContracts.add(new UserContract(user));
            }

            //Form socket response
            return generateSuccessfulResponse(request.OperationCode, userContracts);
        } catch (InvalidPropertyException e) {
            return generateError(request.OperationCode, 400, e.getMessage());
        } catch (DatabaseAccessFailedException e) {
            return generateError(request.OperationCode, 500, "Server error");
        } catch (NotAuthorizedException e) {
            return generateError(request.OperationCode, 401, "Unauthorized");
        }
    }

    /**
     * Updates a specific booking status
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleChangeBookingStatus(HeadOfficeRequestMessage request) {
        Branch destBranch = getRequestBranch(request);

        if (destBranch == null) {
            return generateError(request.OperationCode, 404, "Branch not found");
        }

        try {
            User user = doAuthentication(request.getAuthEmail(), request.getAuthPassword(), true);

            ClusterCommunicationObject<String> rawMpiResponse = ForwardRequestToBranch(destBranch, request.OperationCode, request.SerializedObject, ChangeBookingStatusContract.class, user);

            //Parse MPI response
            Type jsonType = new TypeToken<BranchResponseMessage<BookingContract>>() {}.getType();
            BranchResponseMessage<BookingContract> mpiResponse = SerializationUtils.deserialize(rawMpiResponse.object, jsonType);

            if (mpiResponse.Status != 200) {
                return generateError(request.OperationCode, mpiResponse.Status, mpiResponse.Error);
            }

            //Form socket response
            return generateSuccessfulResponse(request.OperationCode, mpiResponse.Object);
        } catch (ClusterCommunicatorException e) {
            return generateError(request.OperationCode, 500, "Server error");
        } catch (NotAuthorizedException e) {
            return generateError(request.OperationCode, 401, "Unauthorized");
        }
    }

    /**
     * Returns the vehicle moves for a specific branch according to the given criteria
     *
     * @param request the request parameters
     * @return the result response
     */
    public static HeadOfficeResponseMessage HandleGetVehicleMoves(HeadOfficeRequestMessage request) {
        Branch destBranch = getRequestBranch(request);

        if (destBranch == null) {
            return generateError(request.OperationCode, 404, "Branch not found");
        }

        try {
            User user = doAuthentication(request.getAuthEmail(), request.getAuthPassword(), true);

            ClusterCommunicationObject<String> rawMpiResponse = ForwardRequestToBranch(destBranch, request.OperationCode, request.SerializedObject, GetBranchVehicleMovesContract.class, user);

            //Parse MPI response
            Type jsonType = new TypeToken<BranchResponseMessage<List<BookingContract>>>() {}.getType();
            BranchResponseMessage<List<BookingContract>> mpiResponse = SerializationUtils.deserialize(rawMpiResponse.object, jsonType);

            if (mpiResponse.Status != 200) {
                return generateError(request.OperationCode, mpiResponse.Status, mpiResponse.Error);
            }

            //Form socket response
            return generateSuccessfulResponse(request.OperationCode, mpiResponse.Object);
        } catch (ClusterCommunicatorException e) {
            return generateError(request.OperationCode, 500, "Server error");
        } catch (NotAuthorizedException e) {
            return generateError(request.OperationCode, 401, "Unauthorized");
        }
    }
}
