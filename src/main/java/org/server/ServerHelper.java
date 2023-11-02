package org.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;

/** Static Helper class to execute system commands and transmit all messages to the client. */
public class ServerHelper {

    /**
     * Runs the provided system command.
     * @param command Command to be run.
     * @return String result of system command.  Null if unsuccessful command.
     */
    public static String executeSystemCommand(String command) {
        StringBuilder systemCommandOutput = new StringBuilder();

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                systemCommandOutput.append(line);
                systemCommandOutput.append("\n");
            }
            reader.close();

        } catch (IOException e) {
            // log error and return null to indicate unsuccessful execution of command
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
        return systemCommandOutput.toString();

    }

    /**
     * Transmits provided message to client.
     * @param messageToSend
     * @param writer BufferedWriter in use for communication with client.
     * @return True if message was successfully sent.
     */
    public static boolean sendMessage(String messageToSend, BufferedWriter writer) {
        try {
            writer.write(messageToSend);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            // log error and return false to indicate unsuccessful message transmission
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            return false;
        }
        return true;
    }

}
