package org.server;
import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ServerThread extends Thread{
    private Socket socket;
    public ServerThread(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String line;
            String messageToSend;
           do {
                line = reader.readLine();

                switch (line){
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
                        messageToSend = "THE MEMORY USAGE IS: " + totalMemory + " bytes";
                        break;
                    case "netstat":
                        messageToSend = ServerHelper.executeSystemCommand("netstat");
                        System.out.println("entered netstat");
                        break;
                    case "current_users:":
                        messageToSend = ServerHelper.executeSystemCommand("who");
                        break;
                    case "running_processes":
                        messageToSend = "Running Processes: " + ServerHelper.executeSystemCommand("ps");
                        break;
                    case "EXIT":
                        writer.flush();
                        socket.close();
                    default:
                        messageToSend = "NO MATCHES FOUND FOR CLIENT INPUT";
                        System.out.println("NO MATCHES FOUND FOR CLIENT INPUT");
                }
                System.out.println(messageToSend);
                String sbText = new StringBuilder(messageToSend).toString();
                System.out.println(sbText);
               writer.println(sbText);
               writer.println("END");
               writer.flush();
            }
            while(!line.equals("END"));
            socket.close();
        }catch (IOException e)
        {
            System.out.println("Server Exception");
            //e.printStackTrace();
        }
    }
}
