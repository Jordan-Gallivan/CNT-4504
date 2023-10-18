package org.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/*
TODO:
    o determine java runtime environment of server
        o if < 15, remove text block in ClientOperations
    o import dependencies to server
    x parseInt exception handling -> go to scanner?
    Make client circle back to start after server responds
    Throws "99 is not a valid option. Please try again" message
 */

public class Client {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // pull hostname and port from user.
        System.out.println("Welcome to the CNT 4504 Project 1 Client.  Please enter the Hostname and Port.");
        System.out.println("Hostname: ");
        String hostname = scanner.nextLine();
        System.out.println("Port: ");
        int port = scanner.nextInt();
        System.out.println("Connecting to the server...");

        try (scanner; Socket socket = new Socket(hostname, port)) {

            // initialize ClientHelper
            ClientHelper client = new ClientHelper(socket);

            System.out.println("Connection Successful!");

            int serverRequest;
            int numRequests = 0;

            // pull request from user and process.  Continue until user exits with command 99
            while (true) {

                // pull operation
                System.out.println("Select an operation you would like to request for the server:");
                client.displayOperations();
                System.out.println("Operation: ");
                serverRequest = scanner.nextInt();

                // validate operation
                if (!client.validOperation(serverRequest)) {
                    System.out.println(serverRequest + " is not a valid operation.  Please try again");
                    continue;
                }
                // exit loop condition
                if (serverRequest == 99) {
                    System.out.println("Exiting...");
                    break;
                }

                // pull number of requests
                System.out.println("Select the number of times you would like to make this request" +
                        client.displayRequests() + " :");
                numRequests = scanner.nextInt();

                // validate number of requests
                if (!client.validRequests(numRequests)) {
                    System.out.println(numRequests + " is not a valid request.  Please try again");
                    continue;
                }

                // process requests and annotate errors
                if (!client.sendMessages(serverRequest, numRequests)) {
                    System.out.println("Error encountered sending request.");
                }
            }

        } catch (IOException | RuntimeException e) {
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            System.out.println("Connection unsuccessful.  Please try again later");
        }
    }
}
