/**
 * CarRental
 *
 * This file provides a common interface to communicate between the cluster nodes
 */

package com.vehiclerental.clusterCommunicator;

import com.vehiclerental.exceptions.ClusterCommunicatorException;

public interface ClusterCommunicator {
    /**
     * Sends an object to a specific cluster node
     *
     * @param destinator the cluster ID of the targeted node
     * @param tag the tag ID of the message
     * @param object the object to send
     * @param <T> the type of the object (Generic)
     * @throws ClusterCommunicatorException - In case of a communication error
     */
    <T> void sendObject(int destinator, int tag, T object) throws ClusterCommunicatorException;

    /**
     * Receives and deserialize an object from a specific cluster node
     *
     * @param source expected source node cluster ID
     * @param tag expected message tag ID
     * @param type expected message object type
     * @param <T> expected message object type (Generic)
     * @return The communication wrapper with the deserialized object
     * @throws ClusterCommunicatorException - In case of a communication error
     */
    <T> ClusterCommunicationObject<T> receiveObject(int source, int tag, Class<T> type) throws ClusterCommunicatorException;
}
