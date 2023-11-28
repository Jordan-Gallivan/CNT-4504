package org.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/** User facing software to receive port for communication with client and process all client requests. */
public class Server {   // TODO curly brace on same line as control statement
    public static void main(String[] args) {
        // take input from user to define a port
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the port: ");
        int port = scanner.nextInt();

        // listen on user defined port
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // keep server running and check for new connections
            while (true) {
                System.out.println("SERVER STARTED");
                System.out.println("WAITING ON THE CLIENT TO CONNECT");

                // accept next request
                Socket client = serverSocket.accept();
                System.out.println("CLIENT ACCEPTED");

                // Execute client command in new thread
                ServerThread clientHandler = new ServerThread(client);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            // program is halted for any IO exception
            throw new RuntimeException(e);
        }
    }
}
