/**
 * CarRental
 *
 * This file provides the main handler for the head office socket communication listener
 * It also includes a request router dispatching the received request to the correct method in a controller
 */

package com.vehiclerental.presentationLayer.headOffice;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.vehiclerental.NodeConfiguration;
import com.vehiclerental.OperationCodes;
import com.vehiclerental.presentationLayer.headOffice.protocolContracts.HeadOfficeRequestMessage;
import com.vehiclerental.presentationLayer.headOffice.protocolContracts.HeadOfficeResponseMessage;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;

public class HeadOfficeManager {
    private SSLContext sslContext = null;
    private SSLServerSocket serverSocket = null;
    private boolean isStopped = false;

    /**
     * Start the socket server
     */
    public void start() {
        openServerSocket();

        //Main listening loop
        while(!isStopped()) {
            Socket clientSocket;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println(">>> SOCKET SERVER server stopped.") ;
                    break;
                }
                throw new RuntimeException(">>> SOCKET SERVER error accepting client connection", e);
            }

            //Request parsing, dispatching and response
            try {
                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                JsonWriter writer = new JsonWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));

                HeadOfficeRequestMessage requestMessage = gson.fromJson(reader, HeadOfficeRequestMessage.class);

                HeadOfficeResponseMessage responseMessage = handleRequest(requestMessage);

                gson.toJson(responseMessage, HeadOfficeResponseMessage.class, writer);

                writer.flush();

                writer.close();

                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
                if (!clientSocket.isClosed()) {
                    try {
                        clientSocket.close();
                    } catch (IOException inner) {
                        inner.printStackTrace();
                    }
                }
            }
        }

        //End of the loop, closing the server
        try {
            this.serverSocket.close();
            System.out.println(">>> SOCKET SERVER stopped.") ;
        } catch (IOException e) {
            throw new RuntimeException(">>> SOCKET SERVER error closing server", e);
        }
    }

    /**
     * Returns the state of the socket server
     *
     * @return true if stopped, false otherwise
     */
    private boolean isStopped() {
        return this.isStopped;
    }

    /**
     * Sets the server to stop after the current request
     */
    public void stopAfterCurrentRequest(){
        this.isStopped = true;
    }

    /**
     * Initiates the secure server socket
     */
    private void openServerSocket() {
        try {
            setupSslTrustStore();
            this.serverSocket = (SSLServerSocket) sslContext.getServerSocketFactory().createServerSocket(NodeConfiguration.Current.port);

            System.out.println(">>> SOCKET SERVER started on port " + Integer.toString(NodeConfiguration.Current.port) + ".") ;
        } catch (IOException e) {
            throw new RuntimeException(">>> SOCKET SERVER cannot open port" + Integer.toString(NodeConfiguration.Current.port), e);
        }
    }

    /**
     * Handle and dispatch the received request
     *
     * @param request received and parsed request
     * @return response object
     * @throws Exception if any error
     */
    private HeadOfficeResponseMessage handleRequest(HeadOfficeRequestMessage request) throws Exception {
        HeadOfficeResponseMessage response;

        //Not implemented yet, but the system can be switched to a fake shutdown mode which could allow soft close/start if needed
        if (!NodeConfiguration.Current.systemAvailable) {
            //System not running
            return generateSystemUnavailableError();
        }

        if (request == null) {
            return HeadOfficeResponseMessage.GetInvalidRequestResponse();
        }

        //Main dispatch
        switch (request.OperationCode) {
            //Guest methods
            case OperationCodes.GET_BRANCHES: {
                response = GuestHeadOfficeController.HandleGetBranches(request);
                break;
            }
            case OperationCodes.SEARCH_AVAIL_VEHICLES: {
                response = GuestHeadOfficeController.HandleSearchAvailableVehicles(request);
                break;
            }
            case OperationCodes.CREATE_ACCOUNT: {
                response = GuestHeadOfficeController.HandleCreateAccount(request);
                break;
            }

            //User methods
            case OperationCodes.GET_ACCOUNT_DETAILS: {
                response = UserHeadOfficeController.HandleGetAccountDetails(request);
                break;
            }
            case OperationCodes.BOOK_VEHICLE: {
                //Also handles create a booking for someone else
                response = UserHeadOfficeController.HandleBookVehicle(request);
                break;
            }
            case OperationCodes.GET_USER_BOOKINGS: {
                response = UserHeadOfficeController.HandleGetUserBookings(request);
                break;
            }

            //Staff methods
            case OperationCodes.SHUTDOWN_SYSTEM: {
                response = StaffHeadOfficeController.HandleShutdownSystem(request);
                break;
            }
            case OperationCodes.GET_BRANCH_BOOKINGS: {
                response = StaffHeadOfficeController.HandleGetBranchBookings(request);
                break;
            }
            case OperationCodes.CREATE_USER: {
                response = StaffHeadOfficeController.HandleCreateUser(request);
                break;
            }
            case OperationCodes.SEARCH_USER: {
                response = StaffHeadOfficeController.HandleSearchUser(request);
                break;
            }
            case OperationCodes.UPDATE_OR_CREATE_VEHICLE: {
                response = StaffHeadOfficeController.HandleUpdateOrCreateVehicle(request);
                break;
            }
            case OperationCodes.SEARCH_ALL_VEHICLES: {
                response = StaffHeadOfficeController.HandleSearchVehicles(request);
                break;
            }
            case OperationCodes.CHANGE_BOOKING_STATUS: {
                response = StaffHeadOfficeController.HandleChangeBookingStatus(request);
                break;
            }
            case OperationCodes.GET_VEHICLE_MOVES: {
                response = StaffHeadOfficeController.HandleGetVehicleMoves(request);
                break;
            }
            default: {
                response = HeadOfficeResponseMessage.GetInvalidRequestResponse();
                break;
            }
        }

        return response;
    }

    /**
     * Generates a default service unavailable response message
     *
     * @return the generated message
     */
    private HeadOfficeResponseMessage generateSystemUnavailableError() {
        HeadOfficeResponseMessage resp = new HeadOfficeResponseMessage();
        resp.Status = 503;
        resp.Error = "Service unavailable";

        return resp;
    }

    /**
     * Sets up the SSL trust store, containing the encryption keys for encrypted socket communication
     *
     * The keystore was generated with this tool: http://www.keystore-explorer.org/downloads.php
     */
    private void setupSslTrustStore() {
        try {
            char[] password = "carrental".toCharArray();

            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(new FileInputStream(new File("carrental.keystore").getAbsolutePath()), password);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password);

            // Create a SSLContext with the certificate
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyStoreException | IOException | KeyManagementException | CertificateException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
    }
}
