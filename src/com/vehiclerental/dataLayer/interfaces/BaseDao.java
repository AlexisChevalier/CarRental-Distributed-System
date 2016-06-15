/**
 * CarRental
 *
 * This file provides a common interface for the basic data access methods
 */

package com.vehiclerental.dataLayer.interfaces;

import com.vehiclerental.exceptions.DatabaseDeleteFailedException;
import com.vehiclerental.exceptions.DatabaseUpdateFailedException;

import java.util.List;

public interface BaseDao<T> {
    /**
     * Returns all the corresponding elements in the database for a given type
     *
     * @return a list of the specified type
     */
    List<T> getAll();

    /**
     * Return the element matching the provided id in the database for a given type
     *
     * @param id unique id of the element
     * @return the element, null if not found
     */
    T getById(int id);

    /**
     * Inserts the given element into the database of the given type
     *
     * @param entity the given element
     * @return the given element, updated with a generated id, null if it didn't work
     */
    T create(T entity);

    /**
     * Updates a given entity in the database of the given type
     *
     * @param entity the element to update
     * @throws DatabaseUpdateFailedException if the update was not successful
     */
    void update(T entity) throws DatabaseUpdateFailedException;

    /**
     * Removes a given entity from the database of the given type
     *
     * @param entity the element to remove
     * @throws DatabaseDeleteFailedException if the deletion was not successful
     */
    void delete(T entity) throws DatabaseDeleteFailedException;
}
