package org.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClientOperations {

    private final Socket socket;
    private final PrintWriter writer;
    private final ImmutableMap<Integer, String> OPERATIONS = ImmutableMap.<Integer, String>builder()
            .put(0, "date_and_time")
            .put(1, "uptime")
            .put(2, "memory_use")
            .put(3, "netstat")
            .put(4, "current_users")
            .put(5, "running_processes")
            .build();
    private final ImmutableList<Integer> REQUESTS = ImmutableList.<Integer>builder()
            .add(1)
            .add(5)
            .add(10)
            .add(15)
            .add(20)
            .add(25)
            .build();
    private final Set<Integer> REQUESTS_SET;

    public ClientOperations(Socket socket) {
        REQUESTS_SET = new HashSet<>(REQUESTS);
        this.socket = socket;
        try {
            OutputStream output = this.socket.getOutputStream();
            this.writer = new PrintWriter(output, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean validOperation(int operation) {
        return OPERATIONS.containsKey(operation);
    }

    public boolean validRequests(int request) {
        return REQUESTS_SET.contains(request);
    }

    public void displayOperations() {
        System.out.println(
                """
                        0. Request the Date and Time on the Server
                        1. Request how long the server has been running since last boot-up
                        2. Request the current memory usage on the server
                        3. Request the network connections on the server
                        4. Request the the users currently connected to the server
                        5. Request a list of hte programs currently running on the server
                        99. Exit the program
                        """
        );
    }

    public void displayRequests() {
        StringBuilder requestString = new StringBuilder();
        requestString.append("(");

        REQUESTS.forEach((req) -> {
            requestString.append(req);
            requestString.append(", ");
        });

        requestString.setLength(requestString.length() - 2);
        requestString.append(")");

        System.out.println(requestString);
    }

    public boolean sendMessages(int input, int requests) {
        try {
            String serverCommand = OPERATIONS.get(input);
            for (int i = 0; i < requests; i++) {
                writer.println(serverCommand);
                InputStream serverResponse = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(serverResponse));
                String serverResponseText = reader.readLine();;
                while (serverResponseText != null) {
                    serverResponseText = reader.readLine();
                    System.out.println(serverResponseText);
                }
            }
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return false;
        }
        return true;
    }
}
