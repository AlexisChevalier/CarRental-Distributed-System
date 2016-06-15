/**
 * CarRental
 *
 * This file provides a common interface for the vehicle move-specific data access methods
 *
 * There is no operations at the moment, but it implements a specialized version of the BaseDao, providing the
 * basic data access methods for a VehicleMove entity
 */

package com.vehiclerental.dataLayer.interfaces;

import com.vehiclerental.dataLayer.entities.VehicleMove;

public interface VehicleMoveDao extends BaseDao<VehicleMove> {}
