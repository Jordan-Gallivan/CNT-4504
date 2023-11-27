package org.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

/** User facing software to receive port for communication with client and process all client requests. */
public class Server
{
    public static void main(String[] args)
    {
        // take input from user to define a port
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the port: ");
        int port = scanner.nextInt();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            
            // keep server running and check for new connections
            loop: while (true) {

                System.out.println("SERVER STARTED");
                System.out.println("WAITING ON THE CLIENT TO CONNECT");

                // accept next request
                Socket socket = serverSocket.accept();
                System.out.println("CLIENT ACCEPTED");

                //creates new thread
                new ServerThread(socket).start();

            }

        } catch (IOException e) {
            // program is halted for any IO exception
            throw new RuntimeException(e);
        }

    }
}




