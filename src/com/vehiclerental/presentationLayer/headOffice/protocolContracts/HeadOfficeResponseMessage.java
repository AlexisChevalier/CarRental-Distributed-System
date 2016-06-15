/**
 * CarRental
 *
 * This file provides the simple communication response object used between the android application and the head office socket server
 * It contains an operation code, a status code, an optional error message and an optional serialized object
 */

package com.vehiclerental.presentationLayer.headOffice.protocolContracts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HeadOfficeResponseMessage {
    @SerializedName("op_code")
    public int OperationCode;
    @SerializedName("status")
    public int Status;
    @SerializedName("error")
    public String Error;
    @SerializedName("serialized_object")
    @Expose(serialize = false)
    public String SerializedObject;

    /**
     * Generates a default invalid request error response
     *
     * @return the generated error response
     */
    public static HeadOfficeResponseMessage GetInvalidRequestResponse() {
        HeadOfficeResponseMessage invalidRequest = new HeadOfficeResponseMessage();
        invalidRequest.Status = 404;
        invalidRequest.Error = "Invalid request code";

        return invalidRequest;
    }
}
