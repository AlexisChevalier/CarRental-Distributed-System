/**
 * CarRental
 *
 * This file provides an SQLite implementation of the branch data access operations
 */

package com.vehiclerental.dataLayer.sqliteImplementation;

import com.j256.ormlite.dao.Dao;
import com.vehiclerental.dataLayer.interfaces.BranchDao;
import com.vehiclerental.dataLayer.entities.Branch;

//Extends the BaseDaoSqliteImpl in order to inherit the generic methods, and implements the BranchDao to only implement the specific methods
public class BranchDaoSqliteImpl  extends BaseDaoSqliteImpl<Branch> implements BranchDao {
    /**
     * Instantiate the object using a common DAO object of the OrmLite SQLite implementation
     *
     * @param daoImpl the orm-specific data access object
     */
    public BranchDaoSqliteImpl(Dao<Branch, Integer> daoImpl) {
        super(daoImpl);
    }
}
