/**
 * CarRental
 *
 * This file provides the default implementation for the branch business methods
 */

package com.vehiclerental.logicLayer.implementations;

import com.vehiclerental.dataLayer.DaoFactory;
import com.vehiclerental.dataLayer.entities.Branch;
import com.vehiclerental.dataLayer.interfaces.BranchDao;
import com.vehiclerental.exceptions.DatabaseAccessFailedException;
import com.vehiclerental.logicLayer.interfaces.BranchService;

import java.util.List;

public class BranchServiceImpl implements BranchService {
    private BranchDao branchDao;

    /**
     * Constructor - instantiate a database access object
     *
     * @throws DatabaseAccessFailedException if connection failed
     */
    public BranchServiceImpl() throws DatabaseAccessFailedException {
        branchDao = DaoFactory.getBranchDao();
    }

    /**
     * Returns all the branches in the database
     *
     * @return a list of all the branches
     */
    @Override
    public List<Branch> getAllBranches() {
        return branchDao.getAll();
    }
}
