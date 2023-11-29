//package org.client;

import java.util.Scanner;

/** User facing Client software to process requests with, and return results of server. */
public class Client {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // pull hostname and port from user.
        System.out.println("Welcome to the CNT 4504 Project 2 Client.  Please enter the Hostname and Port.");
        System.out.println("Hostname: ");
        String hostname = scanner.nextLine();
        System.out.println("Port: ");
        int port = scanner.nextInt();
        System.out.println("Connecting to the server...");

        int serverRequest;
        int numRequests = 0;
        ClientHelper client = new ClientHelper(hostname, port);

        // pull request from user and process.  Continue until user exits with command 99
        while (true) {

            // pull operation
            System.out.println("Select an operation you would like to request for the server:");
            client.displayOperations();
            System.out.println("Operation: ");
            serverRequest = scanner.nextInt();

            // exit loop condition
            if (serverRequest == 99) {
                System.out.println("Exiting...");
                client.sendExitMessage();
                break;
            }

            // validate operation
            if (!client.validOperation(serverRequest)) {
                System.out.println(serverRequest + " is not a valid operation.  Please try again");
                continue;
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
        scanner.close();
    }
}
