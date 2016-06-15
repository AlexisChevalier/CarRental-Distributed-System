/**
 * CarRental
 *
 * This file provides the default implementation for the user business methods
 */

package com.vehiclerental.logicLayer.implementations;

import com.vehiclerental.contracts.CreateAccountRequestContract;
import com.vehiclerental.contracts.SearchUserContract;
import com.vehiclerental.dataLayer.DaoFactory;
import com.vehiclerental.dataLayer.entities.User;
import com.vehiclerental.dataLayer.interfaces.UserDao;
import com.vehiclerental.exceptions.DatabaseAccessFailedException;
import com.vehiclerental.exceptions.EmailAlreadyInUseException;
import com.vehiclerental.exceptions.InvalidPropertyException;
import com.vehiclerental.logicLayer.interfaces.UserService;
import com.vehiclerental.utils.CryptoUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UserServiceImpl implements UserService {
    private UserDao userDao;

    /**
     * Constructor - instantiate a database access object
     *
     * @throws DatabaseAccessFailedException if connection failed
     */
    public UserServiceImpl() throws DatabaseAccessFailedException {
        userDao = DaoFactory.getUserDao();
    }

    /**
     * Creates a new user account with the given properties
     *
     * @param contract the given account properties
     * @return the newly created account
     * @throws EmailAlreadyInUseException if the email is already in use
     * @throws InvalidPropertyException if one of the properties is invalid
     */
    @Override
    public User createUser(CreateAccountRequestContract contract) throws EmailAlreadyInUseException, InvalidPropertyException {
        User user = new User();

        if (contract.fullName == null || contract.fullName.isEmpty()) {
            throw new InvalidPropertyException("Full name is invalid");
        }
        user.setFullName(contract.fullName);

        // The colon is not accepted here because of the basic auth system (email_address:password)
        if (contract.emailAddress == null || contract.emailAddress.isEmpty() || contract.emailAddress.contains(":")) {
            throw new InvalidPropertyException("Email address is invalid");
        }

        User userWithSameEmail = getUser(contract.emailAddress);

        if (userWithSameEmail != null) {
            throw new EmailAlreadyInUseException();
        }

        user.setEmailAddress(contract.emailAddress);

        user.setIsStaff(false);

        if (contract.phoneNumber == null || contract.phoneNumber.isEmpty()) {
            throw new InvalidPropertyException("Phone number is invalid");
        }
        user.setPhoneNumber(contract.phoneNumber);

        if (contract.address_street == null || contract.address_street.isEmpty()) {
            throw new InvalidPropertyException("Street is invalid");
        }
        user.setAddress_street(contract.address_street);

        if (contract.address_city == null || contract.address_city.isEmpty()) {
            throw new InvalidPropertyException("City is invalid");
        }
        user.setAddress_city(contract.address_city);

        if (contract.address_postalCode == null || contract.address_postalCode.isEmpty()) {
            throw new InvalidPropertyException("Postal code is invalid");
        }
        user.setAddress_postalCode(contract.address_postalCode);

        if (contract.address_country == null || contract.address_country.isEmpty()) {
            throw new InvalidPropertyException("Country is invalid");
        }
        user.setAddress_country(contract.address_country);

        if (contract.password == null || contract.password.isEmpty()) {
            throw new InvalidPropertyException("Password is invalid");
        }

        try {
            user.setHashedPassword(CryptoUtils.Sha256Hash(contract.password));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        //We need to encrypt the user before adding it into the database
        user.encrypt();

        userDao.create(user);

        //Now we decrypt before returning it
        user.decrypt();

        return user;
    }

    /**
     * Checks the validity of a couple of user credentials
     *
     * @param user The user object
     * @param password The plain text password
     * @return true if valid, false otherwise
     */
    @Override
    public boolean isUserAccountValid(User user, String password) {
        try {
            return CryptoUtils.AreHashAndClearValueEqual(user.getHashedPassword(), password);
        } catch (UnsupportedEncodingException e) {
            return false;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    /**
     * Checks the validity of a couple of user credentials
     *
     * @param email The user email address
     * @param password The plain text password
     * @return true if valid, false otherwise
     */
    @Override
    public boolean isUserAccountValid(String email, String password) {
        User user = getUser(email);
        return isUserAccountValid(user, password);
    }

    /**
     * Finds a user with a specified email
     *
     * @param email the specified email
     * @return the user if existing, null otherwise
     */
    @Override
    public User getUser(String email) {
        User user = userDao.getUserByEmail(email);
        //User are encrypted by default, we need to decrypt
        if (user != null) {
            user.decrypt();
        }
        return user;
    }

    /**
     * Finds a user with a specified id
     *
     * @param id the specified id
     * @return the user if existing, null otherwise
     */
    @Override
    public User getUser(int id) {
        User user = userDao.getById(id);
        //User are encrypted by default, we need to decrypt
        if (user != null) {
            user.decrypt();
        }
        return user;
    }

    /**
     * Search users given a specific criteria
     *
     * @param searchUserContract the search criteria
     * @return a list of user matching the criteria
     * @throws InvalidPropertyException if one of the criteria properties is invalid
     */
    @Override
    public List<User> searchUser(SearchUserContract searchUserContract) throws InvalidPropertyException {
        if (searchUserContract.searchTerm.length() < 2) {
            throw new InvalidPropertyException("Search criteria must be at least two characters");
        }

        List<User> users = userDao.searchUsers(searchUserContract.searchTerm);

        for (User user : users) {
            //User are encrypted by default, we need to decrypt
            user.decrypt();
        }

        return users;
    }
}
