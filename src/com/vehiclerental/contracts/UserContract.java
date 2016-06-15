/**
 * CarRental
 *
 * This file provides a light communication object representing the state of an user
 */

package com.vehiclerental.contracts;

import com.vehiclerental.dataLayer.entities.User;

public class UserContract {

    /**
     * Transforms a server user into a contract object
     * @param user the user
     */
    public UserContract(User user) {
        this.id = user.getId();
        this.isStaff = user.getIsStaff();
        this.fullName = user.getFullName();
        this.emailAddress = user.getEmailAddress();
        this.phoneNumber = user.getPhoneNumber();
    }

    public int id;
    public boolean isStaff;
    public String fullName;
    public String emailAddress;
    public String phoneNumber;
}
