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

public class ClientHelper {

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
    private final ImmutableList<Integer> NUM_OF_REQUESTS = ImmutableList.<Integer>builder()
            .add(1)
            .add(5)
            .add(10)
            .add(15)
            .add(20)
            .add(25)
            .build();
    private final Set<Integer> NUM_OF_REQUESTS_SET;

    public ClientHelper(Socket socket) {
        NUM_OF_REQUESTS_SET = new HashSet<>(NUM_OF_REQUESTS);
        this.socket = socket;

        // initialize Print Writer
        try {
            OutputStream output = this.socket.getOutputStream();
            this.writer = new PrintWriter(output, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Determines if the provided operation is valid.
     * @param operation operation to be validated.
     * @return True if valid operation.
     */
    public boolean validOperation(int operation) {
        return OPERATIONS.containsKey(operation);
    }

    /**
     * Determines if the provided request is valid.
     * @param request request to be validated.
     * @return True if the request is valid.
     */
    public boolean validRequests(int request) {
        return NUM_OF_REQUESTS_SET.contains(request);
    }

    /**
     * Prints the selectable operations to the console.
     */
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

    /**
     * Generates a string comprised of the valid requests.
     * Example: (1, 5, 10, 15, 20)
     */
    public String displayRequests() {
        StringBuilder requestString = new StringBuilder();
        requestString.append("(");

        NUM_OF_REQUESTS.forEach((req) -> {
            requestString.append(req);
            requestString.append(", ");
        });

        requestString.setLength(requestString.length() - 2);
        requestString.append(")");

        return requestString.toString();
    }

    /**
     * Sends the user inputted request (input) the requested number of times (n).
     * @param request The requested operation of the server.
     * @param n The number of times the n is to be made.
     * @return True if messages were successfully sent.  False if an exception is thrown indicating a failed message
     *  delivery or reception.
     */
    public boolean sendMessages(int request, int n) {
        try {
            // pull user inputted request from Operations Map
            String serverCommand = OPERATIONS.get(request);

            // call the user request the defined number of times and print response.
            for (int i = 0; i < n; i++) {
                // send request to server
                writer.println(serverCommand);

                // receive response
                InputStream serverResponse = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(serverResponse));

                // print response
                String serverResponseText = reader.readLine();
                while (serverResponseText != null) {
                    serverResponseText = reader.readLine();
                    System.out.println(serverResponseText);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            return false;
        }
        return true;
    }
}