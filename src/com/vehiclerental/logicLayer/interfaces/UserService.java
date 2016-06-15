/**
 * CarRental
 *
 * This file provides a common interface for the user business methods
 */

package com.vehiclerental.logicLayer.interfaces;

import com.vehiclerental.contracts.CreateAccountRequestContract;
import com.vehiclerental.contracts.SearchUserContract;
import com.vehiclerental.dataLayer.entities.User;
import com.vehiclerental.exceptions.EmailAlreadyInUseException;
import com.vehiclerental.exceptions.InvalidPropertyException;

import java.util.List;

public interface UserService {
    /**
     * Creates a new user account with the given properties
     *
     * @param contract the given account properties
     * @return the newly created account
     * @throws EmailAlreadyInUseException if the email is already in use
     * @throws InvalidPropertyException if one of the properties is invalid
     */
    User createUser(CreateAccountRequestContract contract) throws EmailAlreadyInUseException, InvalidPropertyException;

    /**
     * Checks the validity of a couple of user credentials
     *
     * @param user The user object
     * @param password The plain text password
     * @return true if valid, false otherwise
     */
    boolean isUserAccountValid(User user, String password);

    /**
     * Checks the validity of a couple of user credentials
     *
     * @param email The user email address
     * @param password The plain text password
     * @return true if valid, false otherwise
     */
    boolean isUserAccountValid(String email, String password);

    /**
     * Finds a user with a specified email
     *
     * @param email the specified email
     * @return the user if existing, null otherwise
     */
    User getUser(String email);

    /**
     * Finds a user with a specified id
     *
     * @param id the specified id
     * @return the user if existing, null otherwise
     */
    User getUser(int id);

    /**
     * Search users given a specific criteria
     *
     * @param searchUserContract the search criteria
     * @return a list of user matching the criteria
     * @throws InvalidPropertyException if one of the criteria properties is invalid
     */
    List<User> searchUser(SearchUserContract searchUserContract) throws InvalidPropertyException;
}
