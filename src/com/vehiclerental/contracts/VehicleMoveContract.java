/**
 * CarRental
 *
 * This file provides a light communication object representing the state of a vehicle move
 */


package com.vehiclerental.contracts;

import com.vehiclerental.dataLayer.entities.VehicleMove;
import com.vehiclerental.utils.DateUtils;

public class VehicleMoveContract {

    /**
     * Transforms a server vehicle move into a contract object
     * @param move the vehicle move
     */
    public VehicleMoveContract(VehicleMove move) {
        this.id = move.getId();
        this.vehicleMoveDate = DateUtils.getIso8601DateString(move.getVehicleMoveDate());
        this.vehicleReturnDate = DateUtils.getIso8601DateString(move.getVehicleReturnDate());
    }

    public int id;
    public String vehicleMoveDate;
    public String vehicleReturnDate;
}
