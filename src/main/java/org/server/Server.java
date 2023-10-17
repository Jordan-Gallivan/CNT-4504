package org.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Server
{
    private Socket socket = null;
    private ArrayList<String> users = new ArrayList<String>();
    private String hostname = null;

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
//                    if (hostname == null) {
////                        line = in.readUTF();
//                        client_Input = in.read();
//                        //hostname = line;
//
//                        //users.add(hostname);
//                        //System.out.println(hostname);
//                    }

                    line = in.readLine();

                    System.out.println("The client entered: " + line);

                    switch(line)
                    {
                        case "date_and_time":
                            System.out.println("THE DATE IS: " + (new Date()).toString());
                            Date currentDate = new Date();

                            sendMessage(currentDate.toString());
                            break;
                        case "uptime":
                            long currentTime = System.currentTimeMillis();
                            System.out.println("THE TOTAL RUNTIME OF THE SERVER IS: " + (currentTime - startTime));
                            String totalTime = String.valueOf(currentTime - startTime);

                            sendMessage("THE TOTAL RUNTIME OF THE SERVER IS: " + totalTime + " Ms");
                            break;
                        case "memory_use":
                            System.out.println("THE SIZE IS: " + socket.getSendBufferSize());

                            sendMessage(String.valueOf(socket.getSendBufferSize()));
                            break;
                        case "netstat":

                            sendMessage("netstat");         //temp Holder
                            break;
                        case "current_users":

                            sendMessage("current Users");       //temp Holder
                            break;
                        case "running_processes":

                            sendMessage("running process");     //temp Holder
                            break;
                        default:
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
//mvm package
//java -cp target/CNT_4504_Project1-1.0-SNAPSHOT.jar org.server.Server