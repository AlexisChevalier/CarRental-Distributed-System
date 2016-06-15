/**
 * CarRental
 *
 * This file provides an implementation for MPI of the cluster communication interface
 */

package com.vehiclerental.clusterCommunicator;

import com.vehiclerental.exceptions.ClusterCommunicatorException;
import com.vehiclerental.utils.CryptoUtils;
import com.vehiclerental.utils.SerializationUtils;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

public class MpiClusterCommunicatorImpl implements ClusterCommunicator {

    /**
     * Sends an object to a specific cluster node using MPI
     *
     * @param destinator the cluster ID of the targeted node
     * @param tag the tag ID of the message
     * @param object the object to send
     * @param <T> the generic type of the object
     * @throws ClusterCommunicatorException
     */
    public <T> void sendObject(int destinator, int tag, T object) throws ClusterCommunicatorException {
        try {
            //Serialize item
            String data = SerializationUtils.serialize(object);

            //Encrypt data
            try {
                data = CryptoUtils.encrypt(data);
            } catch (Exception e) {
                //If it fails, continue with plain text data with a notification
                System.out.println("[ERROR] - Inter-branch communication traffic cannot be encrypted");
                e.printStackTrace();
            }

            char[] serializedCharArray = data.toCharArray();
            int size[] = new int[1];
            size[0] = serializedCharArray.length;

            //Send serialized encrypted data length
            MPI.COMM_WORLD.send(size, 1, MPI.INT, destinator, tag);
            //Then send the serialized encrypted data
            MPI.COMM_WORLD.send(serializedCharArray, serializedCharArray.length, MPI.CHAR, destinator, tag);
        } catch (MPIException e) {
            throw new ClusterCommunicatorException(e.getMessage());
        }
    }

    /**
     * MPI implementation of the receiveObject method
     *
     * @param source expected source node cluster ID
     * @param tag expected message tag ID
     * @param type expected message object type
     * @param <T> the generic type of the expected object
     * @return The communication wrapper with the deserialized object
     * @throws ClusterCommunicatorException
     */
    public <T> ClusterCommunicationObject<T> receiveObject(int source, int tag, Class<T> type) throws ClusterCommunicatorException {
        try {
            ClusterCommunicationObject<T> response = new ClusterCommunicationObject<T>();
            char message[];
            int size[] = new int[1];

            //Obtain size of serialized encrypted data
            MPI.COMM_WORLD.recv(size, 1, MPI.INT, source, tag);
            message = new char[size[0]];

            //Obtain serialized encrypted data
            Status mpiStatus = MPI.COMM_WORLD.recv(message, size[0], MPI.CHAR, source, tag);
            response.source = mpiStatus.getSource();
            response.tag = mpiStatus.getTag();

            String data = String.valueOf(message);

            //Decrypt serialzed encrypted data
            try {
                //Now MPI uses encrypted data
                data = CryptoUtils.decrypt(data);
            } catch (Exception e) {
                //If it fails, continue with plain text data
                System.out.println("[ERROR] - Inter-branch communication traffic cannot be decrypted");
                e.printStackTrace();
            }

            //Deserialize serialized data
            response.object = SerializationUtils.deserialize(data, type);
            return response;
        } catch (MPIException e) {
            throw new ClusterCommunicatorException(e.getMessage());
        }
    }
}
