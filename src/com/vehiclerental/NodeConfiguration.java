/**
 * CarRental
 *
 * This file provides a simple node configuration object, statically accessible from everywhere in the running node
 * It contains all the important part of the node
 *
 * A static class is not a good idea, because it causes a lot of problems for unit testing purposes, I decided to use a single static field instead, which is easily modifiable
 */

package com.vehiclerental;

import com.vehiclerental.clusterCommunicator.ClusterCommunicator;
import com.vehiclerental.dataLayer.entities.Branch;
import com.vehiclerental.presentationLayer.branch.BranchOfficeManager;
import com.vehiclerental.presentationLayer.headOffice.HeadOfficeManager;

import java.util.HashMap;
import java.util.Map;

public class NodeConfiguration {
    //Current node configuration
    public static NodeConfiguration Current = new NodeConfiguration();

    //Current node manager
    public MpiNodeManager mpiNodeManager;
    //Current cluster communicator
    public ClusterCommunicator clusterCommunicator;

    //In memory branch list
    public Map<Integer, Branch> branches = new HashMap<Integer, Branch>();

    //Current branch office manager
    public BranchOfficeManager branchOfficeManager;
    //Current head office manager
    public HeadOfficeManager headOfficeManager;

    //Current node branch
    public Branch nodeBranch;
    //Current port
    public int port;
    //true if the system is available, false if the system is in soft close
    public boolean systemAvailable;
}
