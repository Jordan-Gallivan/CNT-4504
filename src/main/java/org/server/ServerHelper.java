package org.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class ServerHelper {

    private final Socket socket;

    public ServerHelper(Socket socket)
    {
        this.socket = socket;

    }
    /**
     * creates a method that sends information to server terminal using Runtime.getRuntime().exec()
     * @param command is a string passed into the method from the server class
     */

    public String executeSystemCommand(String command) {
        //creates a String Builder to hold all information returned by the terminal
        StringBuilder systemCommandOutput = new StringBuilder();
        try {
            //executes the command passed in as an arguemnt
            Process process = Runtime.getRuntime().exec(command);
            //creates a buffered reader to read in the information returned from the terminal
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            /*creates a string to hold the next line of the terminal information
            *While that line is not null, append the line to the string builder created earlier
            * then create a new line in the string builder
             */
            String line;
            while ((line = reader.readLine()) != null) {
                systemCommandOutput.append(line);
                systemCommandOutput.append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
        return systemCommandOutput.toString();

    }
    /**
     * a method to send messages back to client using 2 arguments:
     *   The string we want to pass in and the writer we use to write it to client
     * @return returns true if the message was sent successfully
     */
    public boolean sendMessage(String messageToSend, BufferedWriter writer) {
        //writes the messageToSend back to the client
        try {
                writer.write(messageToSend);
                writer.newLine();
                writer.flush();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            return false;
        }
        return true;
    }

}
