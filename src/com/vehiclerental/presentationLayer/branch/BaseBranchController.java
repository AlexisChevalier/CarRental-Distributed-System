/**
 * CarRental
 *
 * This file provides a base for the branch office controller
 * It provides basic methods reused in all the different controllers
 */

package com.vehiclerental.presentationLayer.branch;

import com.vehiclerental.presentationLayer.branch.protocolContracts.BranchResponseMessage;

public class BaseBranchController {
    /**
     * Generates an error response with a given operationCode, status code and message
     *
     * @param operationCode given operationCode
     * @param status given status code
     * @param message given message
     * @return the generated error message
     */
    protected static BranchResponseMessage generateError(int operationCode, int status, String message) {
        BranchResponseMessage resp = new BranchResponseMessage();
        resp.OperationCode = operationCode;
        resp.Status = status;
        resp.Error = message;

        return resp;
    }

    /**
     * Generates a success response with a given operation code and object
     *
     * @param operationCode given operation code
     * @param object given object (optional)
     * @return the generated response
     */
    protected static BranchResponseMessage generateSuccessfulResponse(int operationCode, Object object) {
        BranchResponseMessage responseMessage = new BranchResponseMessage();

        responseMessage.OperationCode = operationCode;
        responseMessage.Status = 200;
        responseMessage.Object = object;

        return responseMessage;
    }
}
