/**
 * CarRental
 *
 * This file provides an SQLite implementation of the booking data access operations
 */

package com.vehiclerental.dataLayer.sqliteImplementation;

import com.j256.ormlite.dao.Dao;
import com.vehiclerental.dataLayer.interfaces.BaseDao;
import com.vehiclerental.exceptions.DatabaseDeleteFailedException;
import com.vehiclerental.exceptions.DatabaseUpdateFailedException;

import java.sql.SQLException;
import java.util.List;

public class BaseDaoSqliteImpl<T> implements BaseDao<T> {
    protected Dao<T, Integer> dao;

    /**
     * Instantiate the object using a common DAO object of the OrmLite SQLite implementation
     *
     * @param daoImpl the orm-specific data access object
     */
    public BaseDaoSqliteImpl(Dao<T, Integer> daoImpl) {
        dao = daoImpl;
    }

    /**
     * Returns all the corresponding elements in the SQLite database for a given type
     *
     * @return a list of the specified type
     */
    @Override
    public List<T> getAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Return the element matching the provided id in the SQLite database for a given type
     *
     * @param id unique id of the element
     * @return the element, null if not found
     */
    @Override
    public T getById(int id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Inserts the given element into the SQLite database of the given type
     *
     * @param entity the given element
     * @return the given element, updated with a generated id, null if it didn't work
     */
    @Override
    public T create(T entity) {
        try {
            return dao.createIfNotExists(entity);
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Updates a given entity in the SQLite database of the given type
     *
     * @param entity the element to update
     * @throws DatabaseUpdateFailedException if the update was not successfull
     */
    @Override
    public void update(T entity) throws DatabaseUpdateFailedException {
        try {
            dao.update(entity);
        } catch (SQLException e) {
            throw new DatabaseUpdateFailedException(e.getMessage());
        }
    }

    /**
     * Removes a given entity from the SQLite database of the given type
     *
     * @param entity the element to remove
     * @throws DatabaseDeleteFailedException if the deletion was not successful
     */
    @Override
    public void delete(T entity) throws DatabaseDeleteFailedException {
        try {
            dao.delete(entity);
        } catch (SQLException e) {
            throw new DatabaseDeleteFailedException(e.getMessage());
        }
    }
}
