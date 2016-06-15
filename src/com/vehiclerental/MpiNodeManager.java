/**
 * CarRental
 *
 * This file provides management methods for the MPI configuration of the nodes
 * It detects the nodes type initialize them individually
 * Each node wait for the database to be ready before listening to the network
 */

package com.vehiclerental;

import com.vehiclerental.clusterCommunicator.MpiClusterCommunicatorImpl;
import com.vehiclerental.dataLayer.DaoFactory;
import com.vehiclerental.dataLayer.interfaces.DatabaseHelper;
import com.vehiclerental.dataLayer.entities.Branch;
import com.vehiclerental.dataLayer.sqliteImplementation.OrmLiteConnectionSingleton;
import com.vehiclerental.exceptions.DatabaseAccessFailedException;
import com.vehiclerental.logicLayer.ServiceFactory;
import com.vehiclerental.logicLayer.interfaces.BranchService;
import mpi.MPI;
import mpi.MPIException;

public class MpiNodeManager {
    //Node type enum
    public enum NodeType {
        HEAD_OFFICE,
        BRANCH_OFFICE
    }

    //Node type for the current node
    private NodeType nodeType;

    /**
     * Main method of the node manager, it initialize MPI and detects the node type
     * It also parses the command line arguments for each node type
     *
     * @param args given command line arguments
     * @throws MPIException if the MPI system fails
     * @throws DatabaseAccessFailedException if the database can't be used
     */
    public MpiNodeManager(String[] args) throws MPIException, DatabaseAccessFailedException {
        MPI.Init(args);

        NodeConfiguration.Current.clusterCommunicator = new MpiClusterCommunicatorImpl();

        int rank = MPI.COMM_WORLD.getRank();

        if (rank == 0) {
            NodeConfiguration.Current.port = Integer.parseInt(args[0]);
            nodeType = NodeType.HEAD_OFFICE;

            /* DB Initialisation */
            DatabaseHelper dbHelper = DaoFactory.getDatabaseHelper();
            dbHelper.ensureInitialized();

            // DB ready, system can continue
            MPI.COMM_WORLD.barrier();

            loadBranchesFromDatabase(rank);

        } else {
            nodeType = NodeType.BRANCH_OFFICE;

            // Wait for head office(DB init)
            MPI.COMM_WORLD.barrier();

            loadBranchesFromDatabase(rank);

            if (NodeConfiguration.Current.nodeBranch == null) {
                System.out.println("Node with rank " + rank + " has no branch to host. Exiting.");
                finalizeNode();
                System.exit(0);
            }
        }
    }

    /**
     * Load available branches from the database and define the current branch based on the current node cluster ID
     * @param rank current node cluster ID
     * @throws DatabaseAccessFailedException if the database can't be used
     */
    private void loadBranchesFromDatabase(int rank) throws DatabaseAccessFailedException {
        BranchService branchService = ServiceFactory.getBranchService();
        for (Branch branch: branchService.getAllBranches()) {
            NodeConfiguration.Current.branches.put(branch.getId(), branch);
            if (rank == branch.getClusterId()) {
                NodeConfiguration.Current.nodeBranch = branch;
            }
        }
    }

    /**
     * Terminates a node by closing its database access and finalizing its MPI communication
     *
     * It will wait for all the other nodes to call MPI.Finalize before exiting
     *
     * @throws MPIException if the MPI system fails
     */
    public void finalizeNode() throws MPIException {
        try {
            OrmLiteConnectionSingleton.close();
        } catch (Exception e) {
            System.out.println("[error] - failed to close database on node " + MPI.COMM_WORLD.getRank());
            e.printStackTrace();
        }
        MPI.Finalize();
    }

    /**
     * Return the current node type
     *
     * @return the node type
     */
    public NodeType getNodeType() {
        return nodeType;
    }
}
