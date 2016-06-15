/**
 * CarRental
 *
 * This file provides a base for the head office controller
 * It provides basic methods reused in all the different controllers
 */

package com.vehiclerental.presentationLayer.headOffice;

import com.vehiclerental.NodeConfiguration;
import com.vehiclerental.clusterCommunicator.ClusterCommunicationObject;
import com.vehiclerental.dataLayer.entities.Branch;
import com.vehiclerental.dataLayer.entities.User;
import com.vehiclerental.exceptions.ClusterCommunicatorException;
import com.vehiclerental.exceptions.DatabaseAccessFailedException;
import com.vehiclerental.exceptions.NotAuthorizedException;
import com.vehiclerental.logicLayer.ServiceFactory;
import com.vehiclerental.logicLayer.interfaces.UserService;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchRequestMessage;
import com.vehiclerental.presentationLayer.headOffice.protocolContracts.HeadOfficeRequestMessage;
import com.vehiclerental.presentationLayer.headOffice.protocolContracts.HeadOfficeResponseMessage;
import com.vehiclerental.utils.SerializationUtils;

public class BaseHeadOfficeController {

    /**
     * Generate an error response message with a given operation code, status code and message
     *
     * @param operationCode given operation code
     * @param status given status code
     * @param message given message
     * @return the generated error message
     */
    protected static HeadOfficeResponseMessage generateError(int operationCode, int status, String message) {
        HeadOfficeResponseMessage resp = new HeadOfficeResponseMessage();
        resp.OperationCode = operationCode;
        resp.Status = status;
        resp.Error = message;

        return resp;
    }

    /**
     * Return the branch specified by the request
     *
     * @param requestMessage the request object
     * @return the branch object
     */
    protected static Branch getRequestBranch(HeadOfficeRequestMessage requestMessage) {
        return NodeConfiguration.Current.branches.get(requestMessage.BranchId);
    }

    /**
     * Generate a success response message with a given operation code, and object
     *
     * @param operationCode given operation code
     * @param entity given response object
     * @return the generated response message
     */
    protected static <T> HeadOfficeResponseMessage generateSuccessfulResponse(int operationCode, T entity) {
        HeadOfficeResponseMessage headOfficeResponseMessage = new HeadOfficeResponseMessage();
        headOfficeResponseMessage.OperationCode = operationCode;
        headOfficeResponseMessage.Status = 200;
        headOfficeResponseMessage.SerializedObject = SerializationUtils.serialize(entity);

        return headOfficeResponseMessage;
    }

    /**
     * Apply authentication process to given email and password
     * If required, the user will also be required to be a staff user
     *
     * @param email the user email
     * @param password the user password
     * @param requireStaffAccount true if requires a staff account
     * @return the identified user
     * @throws NotAuthorizedException if not authorized or authenticated
     */
    protected static User doAuthentication(String email, String password, boolean requireStaffAccount) throws NotAuthorizedException {
        UserService userService;
        try {
            userService = ServiceFactory.getUserService();
            User user = userService.getUser(email);

            if (user == null) {
                throw new NotAuthorizedException();
            }

            if (!userService.isUserAccountValid(user, password)) {
                throw new NotAuthorizedException();
            }

            if (requireStaffAccount) {
                if (!user.getIsStaff()) {
                    throw new NotAuthorizedException();
                }
            }

            return user;
        } catch (DatabaseAccessFailedException e) {
            e.printStackTrace();
            throw new NotAuthorizedException();
        }
    }

    /**
     * Forward specific request to a cluster branch with a serialized object
     *
     * @param destination branch ID
     * @param operationCode request operation code
     * @param serializedObject request serialized object
     * @param serializedObjectClass request serialized object expected class
     * @param user request user
     * @param <T> serialized object generic type
     * @return the response object
     * @throws ClusterCommunicatorException
     */
    protected static <T> ClusterCommunicationObject<String> ForwardRequestToBranch(Branch destination, int operationCode, String serializedObject, Class<T> serializedObjectClass, User user) throws ClusterCommunicatorException {
        if (serializedObject != null) {
            //Deserialize the object before sending in order to serialize the whole communication object
            T object = SerializationUtils.deserialize(serializedObject, serializedObjectClass);
            return ForwardRequestToBranch(destination, operationCode, object, user);
        } else {
            return ForwardRequestToBranch(destination, operationCode, null, user);
        }
    }

    /**
     * Forward specific request to a cluster branch with a deserialized object
     *
     * @param destination branch ID
     * @param operationCode request operation code
     * @param object deserialized object
     * @param user request user
     * @param <T> object generic type
     * @return the response object
     * @throws ClusterCommunicatorException
     */
    protected static <T> ClusterCommunicationObject<String> ForwardRequestToBranch(Branch destination, int operationCode, T object, User user) throws ClusterCommunicatorException {
        BranchRequestMessage<T> branchRequestMessage = new BranchRequestMessage<T>();

        if (user != null) {
            branchRequestMessage.userId = user.getId();
        }

        branchRequestMessage.operationCode = operationCode;
        branchRequestMessage.object = object;

        NodeConfiguration.Current.clusterCommunicator.sendObject(destination.getClusterId(), operationCode, SerializationUtils.serialize(branchRequestMessage));

        return NodeConfiguration.Current.clusterCommunicator.receiveObject(destination.getClusterId(), operationCode, String.class);
    }
}
