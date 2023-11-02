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

                // initialize reader/writer.  Passed as parameters to all helper functions
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                // receive request from client
                String line = in.readLine();
                System.out.println("The client entered: " + line);
                String messageToSend;

                // process request.
                // Default is an invalid request, which is logged and returned to client.
                switch (line) {
                    case "date_and_time":
                        Date currentDate = new Date();
                        messageToSend = "THE DATE IS: " + currentDate.toString();
                        break;
                    case "uptime":
                        messageToSend = "THE TOTAL UPTIME OF THE SERVER IS: " +
                                ServerHelper.executeSystemCommand("uptime");
                        break;
                    case "memory_use":
                        long totalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        messageToSend = "The Memory Usage is: " + totalMemory + " bytes";
                        break;
                    case "netstat":
                        messageToSend = ServerHelper.executeSystemCommand("netstat");
                        break;
                    case "current_users":
                        messageToSend = ServerHelper.executeSystemCommand("who");
                        break;
                    case "running_processes":
                        messageToSend = "running process" + ServerHelper.executeSystemCommand("ps");
                        break;
                    case "EXIT":
                        writer.flush();
                        socket.close();
                        break loop;
                    default:
                        messageToSend = "NO MATCHES FOUND FOR CLIENT INPUT";
                        System.out.println("NO MATCHES FOUND FOR CLIENT INPUT");
                }

                // validate successful system command execution
                if (messageToSend == null) {
                    System.out.println("ERROR executing system command");
                    messageToSend = "ERROR executing system command";
                }

                // validate successful message transmission
                System.out.println("Sending Message");
                if (ServerHelper.sendMessage(messageToSend, writer)) {
                    System.out.println("Message Sent");
                } else {
                    System.out.println("ERROR sending message");
                }

                // end of transmission to close socket
                ServerHelper.sendMessage("END", writer);
                writer.flush();
            }

        } catch (IOException e) {
            // program is halted for any IO exception
            throw new RuntimeException(e);
        }

    }
}




