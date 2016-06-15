/**
 * CarRental
 *
 * This file provides the main handler for the branch cluster communication listener
 * It also includes a request router dispatching the received request to the correct method in a controller
 */

package com.vehiclerental.presentationLayer.branch;

import com.google.gson.reflect.TypeToken;
import com.vehiclerental.NodeConfiguration;
import com.vehiclerental.OperationCodes;
import com.vehiclerental.clusterCommunicator.ClusterCommunicationObject;
import com.vehiclerental.contracts.*;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchRequestMessage;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchResponseMessage;
import com.vehiclerental.utils.SerializationUtils;
import mpi.MPI;

import java.lang.reflect.Type;

public class BranchOfficeManager {
    private boolean stopped;

    /**
     * Sets the server to stop after the current request
     */
    public void stopAfterCurrentRequest() {
        this.stopped = true;
    }

    /**
     * Start the cluster listener loop
     */
    public void start() throws Exception {
        ClusterCommunicationObject<String> clusterCommunicationObject;
        while (!stopped) {
            //Receive
            clusterCommunicationObject = NodeConfiguration.Current.clusterCommunicator.receiveObject(
                    MPI.ANY_SOURCE,
                    MPI.ANY_TAG,
                    String.class);

            //Handle
            String response = SerializationUtils.serialize(handleRequest(clusterCommunicationObject));

            //Respond
            NodeConfiguration.Current.clusterCommunicator.sendObject(
                    clusterCommunicationObject.source,
                    clusterCommunicationObject.tag,
                    response);
        }
    }

    /**
     * Handle and dispatch the received request
     *
     * @param communicationObject received and parsed
     * @return response object
     */
    public BranchResponseMessage handleRequest(ClusterCommunicationObject<String> communicationObject){
        BranchResponseMessage response;
        try {
            switch (communicationObject.tag) {
                //Guest methods
                case OperationCodes.SEARCH_AVAIL_VEHICLES: {
                    Type jsonType = new TypeToken<BranchRequestMessage<SearchAvailableVehiclesRequestContract>>() {}.getType();
                    BranchRequestMessage<SearchAvailableVehiclesRequestContract> request = SerializationUtils.deserialize(communicationObject.object, jsonType);
                    response = GuestBranchController.handleSearchAvailableVehicles(request, true);
                    break;
                }
                case OperationCodes.CLUSTER_SEARCH_AVAIL_VEHICLES_BROADCAST: {
                    Type jsonType = new TypeToken<BranchRequestMessage<SearchAvailableVehiclesRequestContract>>() {}.getType();
                    BranchRequestMessage<SearchAvailableVehiclesRequestContract> request = SerializationUtils.deserialize(communicationObject.object, jsonType);
                    response = GuestBranchController.handleSearchAvailableVehicles(request, false);
                    break;
                }

                //User methods
                case OperationCodes.BOOK_VEHICLE: {
                    Type jsonType = new TypeToken<BranchRequestMessage<CreateBookingContract>>() {}.getType();
                    BranchRequestMessage<CreateBookingContract> request = SerializationUtils.deserialize(communicationObject.object, jsonType);
                    response = UserBranchController.handleCreateBooking(request);
                    break;
                }
                case OperationCodes.GET_USER_BOOKINGS: {
                    Type jsonType = new TypeToken<BranchRequestMessage<Void>>() {}.getType();
                    BranchRequestMessage<Void> request = SerializationUtils.deserialize(communicationObject.object, jsonType);
                    response = UserBranchController.handleGetUserBookings(request);
                    break;
                }
                //Staff methods
                case OperationCodes.SHUTDOWN_SYSTEM: {
                    Type jsonType = new TypeToken<BranchRequestMessage<Void>>() {}.getType();
                    BranchRequestMessage<Void> request = SerializationUtils.deserialize(communicationObject.object, jsonType);
                    response = StaffBranchController.handleBranchShutdown(request);
                    break;
                }
                case OperationCodes.GET_BRANCH_BOOKINGS: {
                    Type jsonType = new TypeToken<BranchRequestMessage<Void>>() {}.getType();
                    BranchRequestMessage<Void> request = SerializationUtils.deserialize(communicationObject.object, jsonType);

                    response = StaffBranchController.handleGetBranchBookings(request);
                    break;
                }
                case OperationCodes.UPDATE_OR_CREATE_VEHICLE: {
                    Type jsonType = new TypeToken<BranchRequestMessage<CreateUpdateVehicleContract>>() {}.getType();
                    BranchRequestMessage<CreateUpdateVehicleContract> request = SerializationUtils.deserialize(communicationObject.object, jsonType);

                    response = StaffBranchController.handleUpdateOrCreateVehicle(request);
                    break;
                }
                case OperationCodes.SEARCH_ALL_VEHICLES: {
                    Type jsonType = new TypeToken<BranchRequestMessage<SearchVehicleContract>>() {}.getType();
                    BranchRequestMessage<SearchVehicleContract> request = SerializationUtils.deserialize(communicationObject.object, jsonType);

                    response = StaffBranchController.handleSearchVehicles(request);
                    break;
                }
                case OperationCodes.CHANGE_BOOKING_STATUS: {
                    Type jsonType = new TypeToken<BranchRequestMessage<ChangeBookingStatusContract>>() {}.getType();
                    BranchRequestMessage<ChangeBookingStatusContract> request = SerializationUtils.deserialize(communicationObject.object, jsonType);

                    response = StaffBranchController.handleChangeBookingStatus(request);
                    break;
                }
                case OperationCodes.GET_VEHICLE_MOVES: {
                    Type jsonType = new TypeToken<BranchRequestMessage<GetBranchVehicleMovesContract>>() {}.getType();
                    BranchRequestMessage<GetBranchVehicleMovesContract> request = SerializationUtils.deserialize(communicationObject.object, jsonType);

                    response = StaffBranchController.handleGetVehicleMoves(request);
                    break;
                }
                default: {
                    response = BranchResponseMessage.GetInvalidRequestResponse();
                    break;
                }
            }

            return response;
        } catch (Exception e) {
            return BranchResponseMessage.GetServerErrorResponse();
        }
    }
}
