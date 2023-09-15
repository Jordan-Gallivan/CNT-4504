package org.client;

import java.io.Console;
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
 */

public class Client {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the CNT 4504 Project 1 Client.  Please enter the Hostname and Port.");
        System.out.println("Hostname: ");
        String hostname = scanner.nextLine();
        System.out.println("Port: ");
        int port = scanner.nextInt();
        System.out.println("Connecting to the server...");

        // TODO correct error handling
        //  o add to loop?
        Socket socket;
        try {
            socket = new Socket(hostname, port);
            ClientOperations client = new ClientOperations(socket);

            System.out.println("Connection Successful!");

            int userInput;
            int userRequest = 0;

            while (true) {
                System.out.println("Select an operation you would like to request for the server:");
                client.displayOperations();
                System.out.println("Operation: ");
                userInput = scanner.nextInt();

                if (!client.validOperation(userInput)) {
                    System.out.println(userInput + " is not a valid operation.  Please try again");
                    continue;
                }
                if (userInput == 99) {
                    System.out.println("Exiting...");
                    break;
                }

                System.out.println("Select the number of times you would like to make this request:");
                client.displayRequests();
                userRequest = scanner.nextInt();

                if (!client.validRequests(userInput)) {
                    System.out.println(userRequest + " is not a valid request.  Please try again");
                    continue;
                }

                if (!client.sendMessages(userInput, userRequest)) {
                    System.out.println("Error encountered sending request.");
                }

            }

            socket.close();


        } catch (IOException | RuntimeException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            System.out.println("Connection unsuccessful.  Please try again later");
        }
        scanner.close();
    }

}
