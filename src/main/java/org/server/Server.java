package org.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
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

                    if (line.equals("date_and_time")) {
                        System.out.println("THE DATE IS: " + (new Date()).toString());
                    }
                    else if (line.equals("uptime")) {
                        long currentTime = System.currentTimeMillis();
                        System.out.println("THE TOTAL RUNTIME OF THE SERVER IS: " + (currentTime - startTime));
                        String totalTime = String.valueOf(currentTime - startTime);

                        sendMessage(totalTime);

                    }
                    else if (line.equals("memory_use")) {
                        System.out.println("THE SIZE IS: " + socket.getSendBufferSize());

                    }
                    else if (line.equals("netstat")) {

                    }
                    else if (line.equals("current_users")) {
                        System.out.println("THE USERS ARE");
                        System.out.println("--------------");
                    }
                    else if (line.equals("running_processes")) {

                    }
                    else {
                        line = "99";
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
        Server server = new Server(5000);
    }

    private void sendMessage(String s) throws IOException {
        try {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            writer.println(s);

            //this.writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
//mvm package
//java -cp target/CNT_4504_Project1-1.0-SNAPSHOT.jar org.server.Server