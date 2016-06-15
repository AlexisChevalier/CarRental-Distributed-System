/**
 * CarRental
 *
 * This file provides the default implementation for the vehicle business methods
 */

package com.vehiclerental.logicLayer.implementations;

import com.google.gson.reflect.TypeToken;
import com.vehiclerental.NodeConfiguration;
import com.vehiclerental.OperationCodes;
import com.vehiclerental.clusterCommunicator.ClusterCommunicationObject;
import com.vehiclerental.contracts.*;
import com.vehiclerental.dataLayer.DaoFactory;
import com.vehiclerental.dataLayer.entities.Branch;
import com.vehiclerental.dataLayer.entities.Vehicle;
import com.vehiclerental.dataLayer.interfaces.VehicleDao;
import com.vehiclerental.exceptions.*;
import com.vehiclerental.logicLayer.interfaces.VehicleService;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchRequestMessage;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchResponseMessage;
import com.vehiclerental.utils.DateUtils;
import com.vehiclerental.utils.SerializationUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VehicleServiceImpl implements VehicleService {
    private VehicleDao vehicleDao;

    /**
     * Constructor - instantiate a database access object
     *
     * @throws DatabaseAccessFailedException if connection failed
     */
    public VehicleServiceImpl() throws DatabaseAccessFailedException {
        vehicleDao = DaoFactory.getVehicleDao();
    }

    /**
     * Returns all the available vehicles in a specific branch for the given criteria
     * Takes in account if the branch is the booking branch or not and if it should apply an additional move time or not
     *
     * @param searchCriteriaContract the search criteria details
     * @param currentBranch the current branch
     * @param requiresMove true if it requires a vehicle move for this branch
     * @return the list of the available vehicles
     * @throws InvalidDateException if one of the dates is invalid
     */
    @Override
    public List<BookingSearchResultContract> searchAvailableVehicles(SearchAvailableVehiclesRequestContract searchCriteriaContract, Branch currentBranch, boolean requiresMove) throws InvalidDateException {
        Calendar startDate = DateUtils.getCalendarFromIso8601String(searchCriteriaContract.pickupDate);
        Calendar endDate = DateUtils.getCalendarFromIso8601String(searchCriteriaContract.returnDate);

        Calendar todayCalendar = DateUtils.getTodayCalendar();

        if(DateUtils.getBookingDays(startDate, endDate) > 7) {
            throw new InvalidDateException("Bookings are only available for a maximum of 7 days");
        }

        if (requiresMove) {
            //Include moving time in the search
            startDate.add(Calendar.DAY_OF_YEAR, -1);
            endDate.add(Calendar.DAY_OF_YEAR, 1);
        }

        //Today must be strictly before the start date
        if (DateUtils.compareCalendar(todayCalendar, startDate) != DateUtils.DATE1_BEFORE_DATE2) {
            if (requiresMove) {
                throw new InvalidDateException("Impossible to book a vehicle on a distant branch for tomorrow or before");
            } else {
                throw new InvalidDateException("Impossible to book a vehicle for the same day or before");
            }
        }

        //Start date must be before end date
        if (DateUtils.compareCalendar(startDate, endDate) != DateUtils.DATE1_BEFORE_DATE2) {
            throw new InvalidDateException("The return date must be after the pick up date");
        }

        List<Vehicle> vehicles = vehicleDao.getAvailableVehicles(
                currentBranch,
                searchCriteriaContract.vehicleType,
                startDate,
                endDate);

        List<BookingSearchResultContract> responseContracts = new ArrayList<BookingSearchResultContract>();

        Calendar pickupDate = DateUtils.getCalendarFromIso8601String(searchCriteriaContract.pickupDate);
        Calendar returnDate = DateUtils.getCalendarFromIso8601String(searchCriteriaContract.returnDate);

        for (Vehicle vehicle : vehicles) {
            BookingSearchResultContract result = new BookingSearchResultContract();

            result.vehicle = new VehicleContract(vehicle);
            result.daysCount = DateUtils.getBookingDays(pickupDate, returnDate);
            result.requireVehicleMove = requiresMove;
            result.pickupDate = DateUtils.getIso8601DateString(pickupDate);
            result.returnDate = DateUtils.getIso8601DateString(returnDate);
            result.price = Math.round(vehicle.getPoundsPerDay() * result.daysCount * 100.0) / 100.0;

            responseContracts.add(result);
        }

        return responseContracts;
    }

    /**
     * Broadcasts the available vehicle search to all the other branch nodes and returns the combined results
     *
     * @param searchCriteriaContract the search criteria details
     * @param currentUserId the ID of the requesting user
     * @return the list of the combined available vehicles
     * @throws ClusterCommunicatorException if one of the dates is invalid
     */
    @Override
    public List<BookingSearchResultContract> broadcastSearchAvailableVehicles(SearchAvailableVehiclesRequestContract searchCriteriaContract, int currentUserId) throws ClusterCommunicatorException {
        List<BookingSearchResultContract> responseContracts = new ArrayList<BookingSearchResultContract>();
        BranchRequestMessage<SearchAvailableVehiclesRequestContract> branchRequestMessage = new BranchRequestMessage<SearchAvailableVehiclesRequestContract>();
        branchRequestMessage.object = searchCriteriaContract;
        branchRequestMessage.operationCode = OperationCodes.CLUSTER_SEARCH_AVAIL_VEHICLES_BROADCAST;
        branchRequestMessage.userId = currentUserId;

        for (Branch branch:NodeConfiguration.Current.branches.values()) {
            //Contact every other branch one by one
            if (branch.getClusterId() != NodeConfiguration.Current.nodeBranch.getClusterId()) {

                NodeConfiguration.Current.clusterCommunicator.sendObject(
                        branch.getClusterId(),
                        OperationCodes.CLUSTER_SEARCH_AVAIL_VEHICLES_BROADCAST,
                        SerializationUtils.serialize(branchRequestMessage));
                ClusterCommunicationObject<String> rawMpiResponse = NodeConfiguration.Current.clusterCommunicator.receiveObject(
                        branch.getClusterId(),
                        OperationCodes.CLUSTER_SEARCH_AVAIL_VEHICLES_BROADCAST,
                        String.class);

                //Parse cluster response
                Type jsonType = new TypeToken<BranchResponseMessage<List<BookingSearchResultContract>>>() {}.getType();
                BranchResponseMessage<List<BookingSearchResultContract>> clusterVehiclesResponse = SerializationUtils.deserialize(rawMpiResponse.object, jsonType);
                if (clusterVehiclesResponse.Status == 200) {
                    responseContracts.addAll(clusterVehiclesResponse.Object);
                }
            }
        }

        return responseContracts;
    }

    /**
     * Returns all the vehicles matching a search criteria in the given branch
     * This is not an available search, only a general vehicle search
     *
     * @param searchCriteria the search criteria
     * @param nodeBranch the given branch
     * @return the list of vehicles matching the criteria in the branch
     * @throws InvalidPropertyException if one of the search criteria properties is invalid
     */
    @Override
    public List<VehicleContract> searchVehicles(SearchVehicleContract searchCriteria, Branch nodeBranch) throws InvalidPropertyException {
        List<Vehicle> vehicles;
         if (searchCriteria == null) {
            throw new InvalidPropertyException("Invalid criteria");
        }

        if (!searchCriteria.registrationNumber.isEmpty()) {
            //Search using registration number
            vehicles = new ArrayList<>();
            Vehicle vehicle = vehicleDao.getVehicleByRegistrationNumberAndBranch(searchCriteria.registrationNumber, nodeBranch);
            if (vehicle != null) {
                vehicles.add(vehicle);
            }
        } else {
            //Search using type
            vehicles = vehicleDao.searchBranchVehiclesByType(searchCriteria.vehicleTypeId, nodeBranch);
        }

        if (vehicles == null) {
            return null;
        }

        List<VehicleContract> contracts = new ArrayList<VehicleContract>();
        for (Vehicle vehicle : vehicles) {
            contracts.add(new VehicleContract(vehicle));
        }

        return contracts;
    }

    /**
     * Creates or Update a vehicle in the given branch corresponding to the given properties
     *
     * @param createUpdateVehicleContract the given properties
     * @param nodeBranch the given branch
     * @return The created vehicle
     * @throws InvalidPropertyException if one of the parameters is invalid
     * @throws RegistrationNumberAlreadyInUseException if the registration number provied to create a vehicle is already in use
     */
    @Override
    public VehicleContract createOrUpdateVehicle(CreateUpdateVehicleContract createUpdateVehicleContract, Branch nodeBranch) throws InvalidPropertyException, RegistrationNumberAlreadyInUseException {
        Vehicle vehicle;

        if (!createUpdateVehicleContract.isUpdateOperation) {
            //Create operation
            vehicle = new Vehicle();

            vehicle.setBranch(nodeBranch);
            vehicle.setAutomaticTransmission(createUpdateVehicleContract.automaticTransmission);

            if (createUpdateVehicleContract.doors <= 0) {
                throw new InvalidPropertyException("Invalid doors count");
            }
            vehicle.setDoors(createUpdateVehicleContract.doors);

            if (createUpdateVehicleContract.seats <= 0) {
                throw new InvalidPropertyException("Invalid seats count");
            }
            vehicle.setSeats(createUpdateVehicleContract.seats);

            if (createUpdateVehicleContract.name == null || createUpdateVehicleContract.name.isEmpty()) {
                throw new InvalidPropertyException("Invalid name");
            }
            vehicle.setName(createUpdateVehicleContract.name);

            if (createUpdateVehicleContract.registrationNumber == null || createUpdateVehicleContract.registrationNumber.isEmpty()) {
                throw new InvalidPropertyException("Invalid registration number");
            }
            Vehicle duplicate = vehicleDao.getVehicleByRegistrationNumberAndBranch(createUpdateVehicleContract.registrationNumber, nodeBranch);
            if (duplicate != null) {
                throw new RegistrationNumberAlreadyInUseException();
            }
            vehicle.setRegistrationNumber(createUpdateVehicleContract.registrationNumber);

            //Vehicles are available by default
            vehicle.setStatus(Vehicle.Status.AVAILABLE.getCode());

            if (Vehicle.Type.get(createUpdateVehicleContract.type) == null) {
                throw new InvalidPropertyException("Invalid vehicle type");
            }
            vehicle.setType(createUpdateVehicleContract.type);

            if (createUpdateVehicleContract.poundsPerDay <= 0) {
                throw new InvalidPropertyException("Invalid price per day");
            }
            vehicle.setPoundsPerDay(Math.round(createUpdateVehicleContract.poundsPerDay * 100.0) / 100.0);

            return new VehicleContract(vehicleDao.create(vehicle));
        } else {
            //Update operation
            vehicle = vehicleDao.getById(createUpdateVehicleContract.id);

            if (vehicle == null) {
                throw new InvalidPropertyException("Invalid vehicle");
            }

            //Feature removed due to a side effect on the vehicle moves, I explained by email that it took too much time to fix before the deadline
            /*Branch newBranch = NodeConfiguration.Current.branches.get(createUpdateVehicleContract.newBranchId);

            if (newBranch == null) {
                throw new InvalidPropertyException("Invalid new branch");
            }
            vehicle.setBranch(newBranch);*/

            if (Vehicle.Status.get(createUpdateVehicleContract.newStatusId) == null) {
                throw new InvalidPropertyException("Invalid status");
            }
            vehicle.setStatus(createUpdateVehicleContract.newStatusId);

            try {
                vehicleDao.update(vehicle);
                return new VehicleContract(vehicle);
            } catch (DatabaseUpdateFailedException e) {
                throw new InvalidPropertyException("Invalid update parameters");
            }
        }
    }
}
