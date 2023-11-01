package org.client;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Helper class to generate Threads for client requests and display results to user.
 */
public class ClientHelperTester {

    private final String hostname;
    private final int port;
    private final Map<Integer, String> OPERATIONS;
    private final List<Integer> NUM_OF_REQUESTS;
    private final Set<Integer> NUM_OF_REQUESTS_SET;

    public ClientHelperTester(String hostname, int port) {
        OPERATIONS = new HashMap<>();
        OPERATIONS.put(0, "date_and_time");
        OPERATIONS.put(1, "uptime");
        OPERATIONS.put(2, "memory_use");
        OPERATIONS.put(3, "netstat");
        OPERATIONS.put(4, "current_users");
        OPERATIONS.put(5, "running_processes");

        NUM_OF_REQUESTS = new ArrayList<>();
        NUM_OF_REQUESTS.add(1);
        NUM_OF_REQUESTS.add(5);
        NUM_OF_REQUESTS.add(10);
        NUM_OF_REQUESTS.add(15);
        NUM_OF_REQUESTS.add(20);
        NUM_OF_REQUESTS.add(25);

        NUM_OF_REQUESTS_SET = new HashSet<>(NUM_OF_REQUESTS);
        this.hostname = hostname;
        this.port = port;

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
        StringBuilder sb = new StringBuilder();
        sb.append("0. Request the Date and Time on the Server\n");
        sb.append("1. Request how long the server has been running since last boot-up\n");
        sb.append("2. Request the current memory usage on the server\n");
        sb.append("3. Request the network connections on the server\n");
        sb.append("4. Request the the users currently connected to the server\n");
        sb.append("5. Request a list of the programs currently running on the server\n");
        sb.append("99. Exit the program");

        System.out.println(sb);
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
    public TestResults sendMessages(int request, int n) {
        // pull user inputted request from Operations Map
        String serverCommand = OPERATIONS.get(request);

        // initialize current time for calculating average
        LocalTime start = LocalTime.now();

        // initialize threadpool
        ExecutorService executor = Executors.newFixedThreadPool(n);

        // create threads for each client request
        for (int i = 0; i < n; i++) {
            Runnable thread = new ClientThread(serverCommand, i);
            executor.execute(thread);
        }

        // shutdown and halt current thread until all threads complete execution
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println("ERROR.  Client Requests timed out.");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        Duration duration = Duration.between(start, LocalTime.now());

//        System.out.println("The total time for all calls: " + duration.toMillis());
        double average = (double) duration.toMillis() / (double)  n;
//        System.out.println("The average time for all calls: " + average + "milliseconds" );
        return new TestResults(request, n, duration.toMillis(), average);
    }

    /**
     * Sends the keyword "EXIT" to the server to close the connection.
     */
    public void sendExitMessage() {
        Runnable thread = new ClientThread("EXIT", 0);
        thread.run();

    }

    /**
     * Client Thread to send request to Server and print response.  Each Thread creates a new socket and the socket is
     * closed when the response is terminated with the key word "END".
     */
    private class ClientThread implements Runnable {
        private final String serverCommand;
        private final int iteration;

        public ClientThread(String serverCommand, int iteration) {
            this.serverCommand = serverCommand;
            this.iteration = iteration;
        }

        public void run() {
            // try with resources, create new socket
            try (Socket socket = new Socket(hostname, port)) {
                // initialize current time for calculation of request time
                LocalTime start = LocalTime.now();

                // initialize readers/writers and send request to server
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println(serverCommand);
                writer.flush();
                StringBuilder response = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;

                // receive response form server until response is terminated with keyword "END"
                while (!(line = reader.readLine()).equals("END")) {
                    response.append(line);
                    response.append("\n");
                }

                // flush reader/writers.  Closing the reader closes the wrapped socket see below link
                // https://stackoverflow.com/questions/30225934/closing-bufferedwriter-reader-affects-other-instances-bound-to-the-same-socket
                output.flush();
                reader.close();

                Duration duration = Duration.between(start, LocalTime.now());
//                System.out.println(response + "Iteration: " + iteration +
//                        " request took " + duration.toMillis() + "milliseconds");
            } catch (IOException | NullPointerException e) {
                System.out.println("ERROR in Thread");
                System.out.println(e.getMessage());
            }
        }
    }
}
