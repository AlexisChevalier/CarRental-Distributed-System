/**
 * CarRental
 *
 * This file is the entry point of the distributed system
 * It configures the global settings of the java application and initialize each node individually
 *
 * It contains some MPI-specific code because the bootstrap part is too much mpi-specific to be generalized properly
 */

package com.vehiclerental;

import com.j256.ormlite.logger.LocalLog;
import com.vehiclerental.presentationLayer.branch.BranchOfficeManager;
import com.vehiclerental.presentationLayer.headOffice.HeadOfficeManager;
import mpi.MPI;
import mpi.MPIException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.sql.SQLException;

public class Main {

    /**
     * Main method, initialize the current node and determines if it is a head or a branch node
     * @param args command line arguments
     * @throws Exception if any exception occurs
     */
    public static void main (String args[]) throws Exception {
        //Reduce SQLite ORM verbosity
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");

        //Adding bouncy castle as encryption provider
        Security.addProvider(new BouncyCastleProvider());

        //NodeConfiguration is a global static element holding all the node properties, it can be changed for testing reasons by updating the value of NodeConfiguration.Current
        NodeConfiguration.Current.mpiNodeManager = new MpiNodeManager(args);

        switch (NodeConfiguration.Current.mpiNodeManager.getNodeType()) {
            case HEAD_OFFICE: { //Head office
                System.out.println(">>> Head office started on " + MPI.getProcessorName());
                NodeConfiguration.Current.systemAvailable = true;
                handleSocketServerNode();
                break;
            }
            default: { // Branch office
                NodeConfiguration.Current.systemAvailable = true;
                System.out.println(">>> Branch " + NodeConfiguration.Current.nodeBranch.getName() + " started on " + MPI.getProcessorName());
                handleBranchNode();
            }
        }
    }

    /**
     * Starts the head office with the Socket server node
     *
     * @throws MPIException if the MPI system fails
     * @throws SQLException if the database can't be used correctly
     */
    private static void handleSocketServerNode() throws MPIException, SQLException {
        NodeConfiguration.Current.headOfficeManager = new HeadOfficeManager();
        NodeConfiguration.Current.headOfficeManager.start();

        //This will be called when the server is stopped
        String processorName = MPI.getProcessorName();
        NodeConfiguration.Current.mpiNodeManager.finalizeNode();
        System.out.println(">>> Head office stopped on " + processorName);
    }

    /**
     * Start the branch node with the cluster communication listener
     * @throws Exception
     */
    private static void handleBranchNode() throws Exception {
        NodeConfiguration.Current.branchOfficeManager = new BranchOfficeManager();
        NodeConfiguration.Current.branchOfficeManager.start();

        //This will be called when the server is stopped
        String processorName = MPI.getProcessorName();
        NodeConfiguration.Current.mpiNodeManager.finalizeNode();
        System.out.println(">>> Branch " + NodeConfiguration.Current.nodeBranch.getName() + " stopped on " + processorName);
    }
}
