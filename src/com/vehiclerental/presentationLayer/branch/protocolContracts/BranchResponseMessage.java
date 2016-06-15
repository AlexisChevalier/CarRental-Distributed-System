/**
 * CarRental
 *
 * This file provides the simple communication response object used between the cluster branches
 * It contains an operation code, a response status code, an optional error message and an optional serialized object
 */

package com.vehiclerental.presentationLayer.branch.protocolContracts;

import com.google.gson.annotations.SerializedName;

public class BranchResponseMessage<T> {
    @SerializedName("op_code")
    public int OperationCode;
    @SerializedName("status")
    public int Status;
    @SerializedName("error")
    public String Error;
    @SerializedName("serialized_object")
    public T Object;

    /**
     * Generates a default invalid request error response
     *
     * @return generated error response
     */
    public static BranchResponseMessage GetInvalidRequestResponse() {
        BranchResponseMessage invalidRequest = new BranchResponseMessage();
        invalidRequest.Status = 404;
        invalidRequest.Error = "Invalid request code";

        return invalidRequest;
    }

    /**
     * Generates a default server error response
     *
     * @return generated error response
     */
    public static BranchResponseMessage GetServerErrorResponse() {
        BranchResponseMessage invalidRequest = new BranchResponseMessage();
        invalidRequest.Status = 500;
        invalidRequest.Error = "Branch error";

        return invalidRequest;
    }
}
