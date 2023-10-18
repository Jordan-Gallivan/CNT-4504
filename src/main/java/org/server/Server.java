package org.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Server
{
    private Socket socket = null;
    public Server(int port)
    {
        try
        {
            ServerSocket server = new ServerSocket(port);
            System.out.println("SERVER STARTED");
            long startTime = System.currentTimeMillis();
            System.out.println("WAITING ON THE CLIENT TO CONNECT");

            socket = server.accept();
            System.out.println("CLIENT ACCEPTED");

            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));


            String line = "-1";
            while(!line.equals("99")) {
                try {
                    line = in.readLine();

                    System.out.println("The client entered: " + line);

                    switch(line)
                    {
                        case "date_and_time":
                            Date currentDate = new Date();
                            sendMessage("THE DATE IS: " + currentDate.toString());
                            break;
                        case "uptime":
                            long currentTime = System.currentTimeMillis();
                            String totalTime = String.valueOf(currentTime - startTime);
                            sendMessage("THE TOTAL RUNTIME OF THE SERVER IS: " + totalTime + " Ms");
                            break;
                        case "memory_use":
                            sendMessage("The Memory Usage is: " + String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
                             + " bytes");
                            break;
                        case "netstat":
                            executeSystemCommand("netstat");
                            break;
                        case "current_users":
                            //executeSystemCommand("net user");
                            executeSystemCommand("who");
                            break;
                        case "running_processes":
                            sendMessage("running process");     //temp Holder
                            //executeSystemCommand("tasklist");
                            executeSystemCommand("ps");
                            break;
                        default:
                            sendMessage("NO MATCHES FOUND FOR CLIENT INPUT");
                            System.out.println("NO MATCHES FOUND FOR CLIENT INPUT");
                    }
                }
                catch (IOException e)
                {
                    throw new RuntimeException();
                }
            }
            System.out.println("CLOSING CONNECTION");


            socket.close();
            server.close();
            in.close();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the port: ");
        int serverPort = scanner.nextInt();
        Server server = new Server(serverPort);
    }




    private String executeSystemCommand(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            sendMessage(line);
        }
        return line;
    }
    private void sendMessage(String s) throws IOException {
        try {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            writer.println(s);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}




