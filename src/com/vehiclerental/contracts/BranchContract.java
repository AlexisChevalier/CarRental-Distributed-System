/**
 * CarRental
 *
 * This file provides a light communication object representing a branch
 */

package com.vehiclerental.contracts;

import com.vehiclerental.dataLayer.entities.Branch;

public class BranchContract {

    /**
     * Transforms a server side branch to a commmunication contract
     * @param branch the branch
     */
    public BranchContract(Branch branch) {
        if (branch != null) {
            id = branch.getId();
            name = branch.getName();
            latitude = branch.getLatitude();
            longitude = branch.getLongitude();
        }
    }

    public int id;
    public String name;
    public double latitude;
    public double longitude;
}
