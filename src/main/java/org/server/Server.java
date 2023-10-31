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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the port: ");
        int port = scanner.nextInt();
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            loop: while (true) {

                System.out.println("SERVER STARTED");

                System.out.println("WAITING ON THE CLIENT TO CONNECT");

                Socket socket = serverSocket.accept();
                System.out.println("CLIENT ACCEPTED");

                ServerHelper serverHelper = new ServerHelper(socket);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String messageToSend;

                String line = in.readLine();

                System.out.println("The client entered: " + line);

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
                        //executeSystemCommand("net user");
                        messageToSend = serverHelper.executeSystemCommand("who");
                        break;
                    case "running_processes":
                        messageToSend = "running process" + serverHelper.executeSystemCommand("ps");
                        //executeSystemCommand("tasklist");
                        break;
                    case "EXIT":
                        writer.flush();
                        socket.close();
                        break loop;
                    default:
                        messageToSend = "NO MATCHES FOUND FOR CLIENT INPUT";
                        System.out.println("NO MATCHES FOUND FOR CLIENT INPUT");
                }

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
                serverHelper.sendMessage("END", writer);

                writer.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}




