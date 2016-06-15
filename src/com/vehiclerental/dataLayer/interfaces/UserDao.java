/**
 * CarRental
 *
 * This file provides a common interface for the user-specific data access methods
 * It implements a specialized version of the BaseDao, providing the basic data access methods for an User entity
 */

package com.vehiclerental.dataLayer.interfaces;

import com.vehiclerental.dataLayer.entities.User;

import java.util.List;

public interface UserDao extends BaseDao<User> {
    /**
     * Search for an user matching the given email in the database
     *
     * @param email the given email
     * @return the found user, or null if not found
     */
    User getUserByEmail(String email);

    /**
     * Search for an user whose a part of email or full name match a the search term
     *
     * @param searchTerm a string of minimum 3 characters
     * @return A list of matching users
     */
    List<User> searchUsers(String searchTerm);
}
