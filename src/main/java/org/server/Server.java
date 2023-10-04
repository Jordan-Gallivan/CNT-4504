package org.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Server
{
    private Socket socket   = null;
    private ServerSocket server   = null;
    private DataInputStream in =  null;
    private ArrayList<String> users = new ArrayList<String>();
    private String hostname = null;
    private long startTime = -1;
    private long currentTime = -1;

    private String line = "";
    public Server(int port)
    {
        try
        {
            server = new ServerSocket(port);
            System.out.println("SERVER STARTED");
            startTime = System.currentTimeMillis();
            System.out.println("WAITING ON THE CLIENT TO CONNECT");

            socket = server.accept();
            System.out.println("CLIENT ACCEPTED");

            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            while(!line.equals("99")) {
                try {
                    if (hostname == null) {
                        line = in.readUTF();
                        hostname = line;
                        users.add(hostname);
                        System.out.println(hostname);
                    }
                    line = in.readUTF();

                    if (line.equals("0")) {
                        System.out.println("THE DATE IS: " + (new Date()).toString());
                    } else if (line.equals("1")) {
                        currentTime = System.currentTimeMillis();
                        System.out.print("THE TOTAL RUNTIME OF THE SERVER IS: " + (startTime - currentTime));
                    } else if (line.equals("2")) {
                        System.out.println("THE SIZE IS: " + socket.getSendBufferSize());
                    } else if (line.equals("3")) {

                    } else if (line.equals("4")) {
                        System.out.println("THE USERS ARE");
                        System.out.println("--------------");
                    } else if (line.equals("5")) {

                    }
                }
                catch (IOException i)
                {
                    System.out.println(i);
                }
            }
            System.out.println("CLOSING CONNECTION");

            socket.close();
            in.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }
    public static void main(String[] args)
    {
        Server server = new Server(5000);
    }
}
//mvm package
//java -cp target/CNT_4504_Project1-1.0-SNAPSHOT.jar org.server.Server