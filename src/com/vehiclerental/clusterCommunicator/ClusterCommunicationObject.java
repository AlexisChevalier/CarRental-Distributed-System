/**
 * CarRental
 *
 * This file is a simple communication wrapper for cluster-based communication, it includes a generic object, a source
 * cluster node ID and a tag ID
 */

package com.vehiclerental.clusterCommunicator;

public class ClusterCommunicationObject<T> {
    public T object;
    public int source;
    public int tag;
}
