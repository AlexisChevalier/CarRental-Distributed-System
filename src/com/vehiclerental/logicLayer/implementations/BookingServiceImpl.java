/**
 * CarRental
 *
 * This file provides the default implementation for the booking business methods
 */

package com.vehiclerental.logicLayer.implementations;

import com.google.gson.reflect.TypeToken;
import com.vehiclerental.NodeConfiguration;
import com.vehiclerental.OperationCodes;
import com.vehiclerental.clusterCommunicator.ClusterCommunicationObject;
import com.vehiclerental.contracts.*;
import com.vehiclerental.dataLayer.DaoFactory;
import com.vehiclerental.dataLayer.entities.*;
import com.vehiclerental.dataLayer.interfaces.BookingDao;
import com.vehiclerental.dataLayer.interfaces.VehicleDao;
import com.vehiclerental.dataLayer.interfaces.VehicleMoveDao;
import com.vehiclerental.exceptions.*;
import com.vehiclerental.logicLayer.ServiceFactory;
import com.vehiclerental.logicLayer.interfaces.BookingService;
import com.vehiclerental.logicLayer.interfaces.UserService;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchRequestMessage;
import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchResponseMessage;
import com.vehiclerental.utils.DateUtils;
import com.vehiclerental.utils.SerializationUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingServiceImpl implements BookingService {
    private VehicleMoveDao vehicleMoveDao;
    private BookingDao bookingDao;
    private VehicleDao vehicleDao;
    private UserService userService;

    /**
     * Constructor - instantiate a database access object
     *
     * @throws DatabaseAccessFailedException if connection failed
     */
    public BookingServiceImpl() throws DatabaseAccessFailedException {
        bookingDao = DaoFactory.getBookingDao();
        vehicleDao = DaoFactory.getVehicleDao();
        userService = ServiceFactory.getUserService();
        vehicleMoveDao = DaoFactory.getVehicleMoveDao();
    }

    /**
     * Returns all the bookings for a specific user and for a specific branch
     *
     * @param user specified user
     * @param currentBranch specified branch
     * @return the list of bookings matching the criteria
     */
    @Override
    public List<BookingContract> getUserBookingsForBranch(User user, Branch currentBranch) {
        List<BookingContract> results = new ArrayList<BookingContract>();

        List<Booking> bookings = bookingDao.getBranchBookingsForUser(currentBranch, user);

        for (Booking booking : bookings) {
            results.add(new BookingContract(booking));
        }

        return results;
    }

    /**
     * Returns all the bookings for a specific branch
     *
     * @param currentBranch specified branch
     * @return the list of bookings matching the criteria
     */
    @Override
    public List<BookingContract> getBookingsForBranch(Branch currentBranch) {
        List<BookingContract> results = new ArrayList<BookingContract>();

        List<Booking> bookings = bookingDao.getBranchBookings(currentBranch);

        for (Booking booking : bookings) {
            results.add(new BookingContract(booking));
        }

        return results;
    }

    /**
     * Creates a booking according to the business rules
     * This method may involve cluster communication
     *
     * Important : The booking is ALWAYS created by the branch owning the vehicle booking in order to avoid latency between the availability check and the booking creation
     *
     * @param userId the user creating the booking
     * @param currentBranch the current branch
     * @param contract the booking parameters
     * @return The created booking, as a contract
     * @throws InvalidPropertyException if a property is invalid
     * @throws NotAuthorizedException if the user is not authorized for this operation (booking creation for himself OR for someone else)
     * @throws VehicleUnavailableException if the desired vehicle is unavailable
     * @throws ClusterCommunicatorException if the cluster communication fails
     * @throws InvalidDateException if the booking dates are invalid
     */
    @Override
    public BookingContract createBooking(int userId, Branch currentBranch, CreateBookingContract contract) throws InvalidPropertyException, NotAuthorizedException, VehicleUnavailableException, ClusterCommunicatorException, InvalidDateException {
        if (currentBranch == null) {
            throw new InvalidPropertyException("Invalid branch");
        }

        // If the booking concerns a vehicle on another branch, we need to forward the request to this branch
        if (contract.vehicleBranchId != currentBranch.getId()) {
            Branch vehicleBranch = NodeConfiguration.Current.branches.get(contract.vehicleBranchId);

            if (vehicleBranch == null) {
                throw new InvalidPropertyException("Invalid branch");
            }

            BranchRequestMessage<CreateBookingContract> forwardedMpiRequest = new BranchRequestMessage<CreateBookingContract>();
            forwardedMpiRequest.userId = userId;
            forwardedMpiRequest.object = contract;
            forwardedMpiRequest.operationCode = OperationCodes.BOOK_VEHICLE;

            NodeConfiguration.Current.clusterCommunicator.sendObject(
                    vehicleBranch.getClusterId(),
                    OperationCodes.BOOK_VEHICLE,
                    SerializationUtils.serialize(forwardedMpiRequest));
            ClusterCommunicationObject<String> rawMpiResponse = NodeConfiguration.Current.clusterCommunicator.receiveObject(
                    vehicleBranch.getClusterId(),
                    OperationCodes.BOOK_VEHICLE,
                    String.class);

            //Parse MPI response
            Type jsonType = new TypeToken<BranchResponseMessage<BookingContract>>() {}.getType();
            BranchResponseMessage<BookingContract> clusterBookingResponse = SerializationUtils.deserialize(rawMpiResponse.object, jsonType);

            return clusterBookingResponse.Object;

        } else {
            //We are now on the vehicle branch, we can create the booking

            //Contains the real start/end values for the database search
            Calendar pickupDate = DateUtils.getCalendarFromIso8601String(contract.pickupDate);
            Calendar returnDate = DateUtils.getCalendarFromIso8601String(contract.returnDate);

            if(DateUtils.getBookingDays(pickupDate, returnDate) > 7) {
                throw new InvalidDateException("Bookings are only available for a maximum of 7 days");
            }

            Booking booking = new Booking();
            Branch bookingBranch = NodeConfiguration.Current.branches.get(contract.bookingBranchId);

            booking.setBranch(bookingBranch);

            booking.setPickUpDate(pickupDate.getTimeInMillis());

            booking.setReturnDate(returnDate.getTimeInMillis());

            Calendar realStart = pickupDate;
            Calendar realEnd = returnDate;

            //Do we need a move ?
            VehicleMove move = null;
            if (bookingBranch.getId() != currentBranch.getId()) {
                move = new VehicleMove();

                Calendar moveStartDate = DateUtils.getCalendarFromIso8601String(contract.pickupDate);
                moveStartDate.add(Calendar.DAY_OF_YEAR, -1);
                Calendar moveReturnDate = DateUtils.getCalendarFromIso8601String(contract.returnDate);
                moveReturnDate.add(Calendar.DAY_OF_YEAR, 1);

                //The move start is one day before the user pickup
                move.setVehicleMoveDate(moveStartDate.getTimeInMillis());
                realStart = moveStartDate;
                //The move return is one day after the user return
                move.setVehicleReturnDate(moveReturnDate.getTimeInMillis());
                realEnd = moveReturnDate;

                move.setBooking(booking);
            }

            Calendar todayCalendar = DateUtils.getTodayCalendar();

            if (DateUtils.compareCalendar(todayCalendar, realStart) != DateUtils.DATE1_BEFORE_DATE2) {
                if (move == null) {
                    throw new InvalidDateException("Impossible to book a vehicle for the same day or before");
                } else {
                    throw new InvalidDateException("Impossible to book a vehicle on a distant branch for tomorrow or before");
                }
            }

            if (DateUtils.compareCalendar(realStart, realEnd) != DateUtils.DATE1_BEFORE_DATE2) {
                throw new InvalidDateException("The return date must be after the pick up date");
            }

            //If the booking concerns this branch, we create it
            Vehicle vehicle = vehicleDao.getVehicleIfAvailable(currentBranch, contract.vehicleId, realStart, realEnd);

            if (vehicle == null) {
                throw new VehicleUnavailableException();
            }
            booking.setCreditCardCvcCodeEncrypted(contract.creditCardCvcCode);
            booking.setCreditCardExpirationMonthEncrypted(contract.creditCardExpirationMonth);
            booking.setCreditCardExpirationYearEncrypted(contract.creditCardExpirationYear);
            booking.setCreditCardNumberEncrypted(contract.creditCardNumber);
            try {
                booking.encrypt();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR: Encryption of payment informations failed");
            }

            User user = new User();

            if (contract.bookingOwnerUserId != null) {
                //Making a booking for someone else
                user = userService.getUser(contract.bookingOwnerUserId);

                if (user == null) {
                    throw new InvalidPropertyException("Invalid user");
                }

            } else {
                user.setId(userId);
            }

            booking.setUser(user);
            booking.setBookingValidated(true);
            booking.setVehicle(vehicle);
            booking.setDaysCount(DateUtils.getBookingDays(pickupDate, returnDate));
            booking.setPrice(Math.round(vehicle.getPoundsPerDay() * booking.getDaysCount() * 100.0) / 100.0);

            if (move != null) {
                booking.setVehicleMove(move);
                vehicleMoveDao.create(move);
            }

            booking = bookingDao.create(booking);

            if (move != null) {
                try {
                    //Update booking reference in vehiclemoves
                    move.setBooking(booking);
                    vehicleMoveDao.update(move);
                } catch (DatabaseUpdateFailedException e) {
                    e.printStackTrace();
                }
            }

            return new BookingContract(booking);
        }
    }


    /**
     * Returns the expected moves for the specified branch following the given criteria
     *
     * @param criteria search criteria
     * @param nodeBranch specified branch
     * @return a list of booking involving matches
     * @throws InvalidPropertyException if one of the criteria properties is invalid
     */
    @Override
    public List<BookingContract> getVehicleMoves(GetBranchVehicleMovesContract criteria, Branch nodeBranch) throws InvalidPropertyException {

        if (nodeBranch == null) {
            throw new InvalidPropertyException("Invalid branch");
        }

        List<Booking> bookingsRequiringMoves = bookingDao.getBookingsRequiringMovesForBranch(nodeBranch, criteria.outgoing);

        List<BookingContract> bookingContractsWithMoves = new ArrayList<>();

        for (Booking booking : bookingsRequiringMoves) {
            BookingContract contract = new BookingContract(booking);

            bookingContractsWithMoves.add(contract);
        }

        return bookingContractsWithMoves;
    }

    /**
     * Update the status of a given booking
     *
     * @param updateProperties booking update details
     * @param nodeBranch branch of the booking
     * @return the updated booking, as a contract
     * @throws InvalidPropertyException if a property is invalid
     * @throws VehicleUnavailableException if the booking was invalidated and the vehicle is now unavailable
     * @throws DatabaseUpdateFailedException if the database access failed
     * @throws InvalidDateException if the booking dates are invalid
     */
    @Override
    public BookingContract changeBookingStatus(ChangeBookingStatusContract updateProperties, Branch nodeBranch) throws InvalidPropertyException, VehicleUnavailableException, DatabaseUpdateFailedException, InvalidDateException {

        if (nodeBranch == null) {
            throw new InvalidPropertyException("Invalid branch");
        }

        Booking booking = bookingDao.getById(updateProperties.bookingId);

        if (booking == null) {
            throw new InvalidPropertyException("Invalid booking");
        }

        Calendar realStart = DateUtils.getCalendarFromTimestamp(booking.getPickUpDate());
        Calendar realEnd = DateUtils.getCalendarFromTimestamp(booking.getReturnDate());

        if (booking.getVehicleMove() != null) {
            realStart = DateUtils.getCalendarFromTimestamp(booking.getVehicleMove().getVehicleMoveDate());
            realEnd = DateUtils.getCalendarFromTimestamp(booking.getVehicleMove().getVehicleReturnDate());
        }

        if (updateProperties.bookingValidated) {
            Vehicle vehicleStillAvailable = vehicleDao.getVehicleIfAvailable(nodeBranch, booking.getVehicle().getId(), realStart, realEnd);
            if (vehicleStillAvailable == null) {
                throw new VehicleUnavailableException();
            }
        }

        booking.setBookingValidated(updateProperties.bookingValidated);

        bookingDao.update(booking);

        return new BookingContract(booking);
    }
}
