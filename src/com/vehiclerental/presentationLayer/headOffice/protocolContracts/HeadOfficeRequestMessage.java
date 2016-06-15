/**
 * CarRental
 *
 * This file provides the simple communication request object used between the android application and the head office socket server
 * It contains an operation code, a basic auth string (base64(email:password)), a current branch ID and an optional serialized object
 */

package com.vehiclerental.presentationLayer.headOffice.protocolContracts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

public class HeadOfficeRequestMessage {
    @SerializedName("op_code")
    public int OperationCode;
    @SerializedName("auth")
    public String BasicAuth;
    @SerializedName("branch")
    public int BranchId;
    @SerializedName("serialized_object")
    @Expose(serialize = false)
    public String SerializedObject;

    /* Those two variables holds the extracted basic auth values */
    private String authEmail;
    private String authPassword;

    /**
     * Returns the authentication password
     * Decodes the values if it has not been done yet
     *
     * @return the authentication password
     */
    public String getAuthPassword() {
        if (authPassword == null) {
            decodeSimpleAuth();
        }
        return authPassword;
    }

    /**
     * Returns the authentication email
     * Decodes the values if it has not been done yet
     *
     * @return the authentication email
     */
    public String getAuthEmail() {
        if (authEmail == null) {
            decodeSimpleAuth();
        }
        return authEmail;
    }

    /**
     * Decodes the simple auth string and assigns the correct values to the password and email variables
     */
    private void decodeSimpleAuth() {
        try {
            String decoded = new String(DatatypeConverter.parseBase64Binary(BasicAuth), StandardCharsets.UTF_8);
            String[] values = decoded.split(":");
            authEmail = values[0];
            authPassword = values[1];
        } catch (Exception e) {
            System.out.println("Invalid authentication parameters");
        }
    }
}
