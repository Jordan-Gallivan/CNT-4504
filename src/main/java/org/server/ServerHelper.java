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

    public String executeSystemCommand(String command) {
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
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
        return systemCommandOutput.toString();

    }
    public boolean sendMessage(String messageToSend, BufferedWriter writer) {
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
