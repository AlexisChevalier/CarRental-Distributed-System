/**
 * CarRental
 *
 * This file provides an SQLite implementation of the vehicle move data access operations
 */

package com.vehiclerental.dataLayer.sqliteImplementation;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.vehiclerental.dataLayer.entities.User;
import com.vehiclerental.dataLayer.entities.VehicleMove;
import com.vehiclerental.dataLayer.interfaces.UserDao;
import com.vehiclerental.dataLayer.interfaces.VehicleDao;
import com.vehiclerental.dataLayer.interfaces.VehicleMoveDao;

import java.sql.SQLException;
import java.util.List;

//Extends the BaseDaoSqliteImpl in order to inherit the generic methods, and implements the VehicleMoveDao to only implement the specific methods
public class VehicleMoveDaoSqliteImpl extends BaseDaoSqliteImpl<VehicleMove> implements VehicleMoveDao {

    /**
     * Instantiate the object using a common DAO object of the OrmLite SQLite implementation
     *
     * @param daoImpl the orm-specific data access object
     */
    public VehicleMoveDaoSqliteImpl(Dao<VehicleMove, Integer> daoImpl) {
        super(daoImpl);
    }
}
