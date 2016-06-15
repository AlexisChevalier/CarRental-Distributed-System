/**
 * CarRental
 *
 * This file provides a common interface for the branch-specific data access methods
 *
 * There is no operations at the moment, but it implements a specialized version of the BaseDao, providing the
 * basic data access methods for a Branch entity
 */

package com.vehiclerental.dataLayer.interfaces;

import com.vehiclerental.dataLayer.entities.Branch;

public interface BranchDao extends BaseDao<Branch> {
}
