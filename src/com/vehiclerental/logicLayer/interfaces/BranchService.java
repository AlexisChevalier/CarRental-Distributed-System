/**
 * CarRental
 *
 * This file provides a common interface for the branch business methods
 */

package com.vehiclerental.logicLayer.interfaces;

import com.vehiclerental.dataLayer.entities.Branch;

import java.util.List;

public interface BranchService {

    /**
     * Returns all the branches in the database
     *
     * @return a list of all the branches
     */
    List<Branch> getAllBranches();
}
