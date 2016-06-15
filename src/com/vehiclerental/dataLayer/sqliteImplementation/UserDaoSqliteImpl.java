/**
 * CarRental
 *
 * This file provides an SQLite implementation of the user data access operations
 */

package com.vehiclerental.dataLayer.sqliteImplementation;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.vehiclerental.dataLayer.interfaces.UserDao;
import com.vehiclerental.dataLayer.entities.User;

import java.sql.SQLException;
import java.util.List;

//Extends the BaseDaoSqliteImpl in order to inherit the generic methods, and implements the UserDao to only implement the specific methods
public class UserDaoSqliteImpl extends BaseDaoSqliteImpl<User> implements UserDao {

    /**
     * Instantiate the object using a common DAO object of the OrmLite SQLite implementation
     *
     * @param daoImpl the orm-specific data access object
     */
    public UserDaoSqliteImpl(Dao<User, Integer> daoImpl) {
        super(daoImpl);
    }

    /**
     * Search for an user matching the given email in the SQLite database
     *
     * @param email the given email
     * @return the found user, or null if not found
     */
    @Override
    public User getUserByEmail(String email) {
        try {
            QueryBuilder<User, Integer> statementBuilder = dao.queryBuilder();

            Where whereClause = statementBuilder.where();

            whereClause.eq(User.EMAIL_ADDRESS_FIELD_NAME, email);

            //Search for user WHERE the email address matches the provided one
            List<User> matches = dao.query(statementBuilder.prepare());

            if (matches.size() == 0) {
                return null;
            } else {
                return matches.get(0);
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Search for an user whose a part of email or full name match a the search term in the SQLite database
     *
     * @param searchTerm a string of minimum 3 characters
     * @return A list of matching users
     */
    @Override
    public List<User> searchUsers(String searchTerm) {
        try {
            QueryBuilder<User, Integer> statementBuilder = dao.queryBuilder();

            Where whereClause = statementBuilder.where();

            //Search for user WHERE the email address or the full name field looks like the provided term
            whereClause
                    .like(User.EMAIL_ADDRESS_FIELD_NAME, "%" + searchTerm + "%")
                    .like(User.FULL_NAME_FIELD_NAME, "%" + searchTerm + "%")
                .or(2)
                //The users must not be staff members
                .eq(User.IS_STAFF_FIELD_NAME, false)
            .and(2);

            return dao.query(statementBuilder.prepare());
        } catch (SQLException e) {
            return null;
        }
    }
}
