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

public class Server
{

    public static void main(String[] args)
    {
        //take input from user to define a port
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the port: ");
        int port = scanner.nextInt();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            //creates a loop to continuously keep the server running and checks for new connections
            loop: while (true) {

                System.out.println("SERVER STARTED");

                System.out.println("WAITING ON THE CLIENT TO CONNECT");

                Socket socket = serverSocket.accept();
                System.out.println("CLIENT ACCEPTED");

                //creates a serverhelper object to aid with .executeSystemCommand and .sendMessage
                ServerHelper serverHelper = new ServerHelper(socket);

                /*
                 * in Is a buffered reader to read in information from the client
                 * writer Is a buffered writer that writes information back to the client
                 * messageToSend String that contains the information to be send back to client
                 * prints out the client input to the server terminal
                 */
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String messageToSend;

                String line = in.readLine();

                System.out.println("The client entered: " + line);

                /*
                 * Checks the client input against expected inputs and executes instructions based on input case
                 * If the client input is "EXIT", closes the writer, socket, and closes the loop entirely
                 * If the client input does not match any expected inputes, sets messageToSend to
                 *   "NO MATCHES FOUND FOR CLIENT INPUT" to be sent back to client later
                 */
                switch (line) {
                    case "date_and_time":
                        Date currentDate = new Date();
                        messageToSend = "THE DATE IS: " + currentDate.toString();
                        break;
                    case "uptime":
                        messageToSend = "THE TOTAL UPTIME OF THE SERVER IS: " +
                                serverHelper.executeSystemCommand("uptime");
                        break;
                    case "memory_use":
                        long totalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        messageToSend = "The Memory Usage is: " + totalMemory + " bytes";
                        break;
                    case "netstat":
                        messageToSend = serverHelper.executeSystemCommand("netstat");
                        break;
                    case "current_users":
                        messageToSend = serverHelper.executeSystemCommand("who");
                        break;
                    case "running_processes":
                        messageToSend = "running process" + serverHelper.executeSystemCommand("ps");
                        break;
                    case "EXIT":
                        writer.flush();
                        socket.close();
                        break loop;
                    default:
                        messageToSend = "NO MATCHES FOUND FOR CLIENT INPUT";
                        System.out.println("NO MATCHES FOUND FOR CLIENT INPUT");
                }

                /*
                 * if the messageToSend is still null, sets messageToSend to "ERROR executing system command
                 *   to be sent back to client
                 * Otherwise send messageToSend back using serverHelper.sendMessage using writer created eariler
                 *   or send an error message to communicate there was an issue sending the message
                 */
                if (messageToSend == null) {
                    System.out.println("ERROR executing system command");
                    messageToSend = "ERROR executing system command";
                }
                System.out.println("Sending Message");
                if (serverHelper.sendMessage(messageToSend, writer)) {
                    System.out.println("Message Sent");
                } else {
                    System.out.println("ERROR sending message");
                }
                //sends "END" message so that client knows we are done communicating until next request
                serverHelper.sendMessage("END", writer);

                writer.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}




