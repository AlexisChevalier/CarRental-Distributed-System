/**
 * CarRental
 *
 * This file provides the simple communication request object used between the cluster branches
 * It contains an operation code, a request user ID and an optional serialized object
 */

package com.vehiclerental.presentationLayer.branch.protocolContracts;

import com.google.gson.annotations.SerializedName;

public class BranchRequestMessage<T> {
    @SerializedName("op_code")
    public int operationCode;
    @SerializedName("user_id")
    public int userId;
    @SerializedName("serialized_object")
    public T object;
}
