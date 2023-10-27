package org.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class ClientHelper {

    private final Socket socket;
    private final Map<Integer, String> OPERATIONS;
    private final List<Integer> NUM_OF_REQUESTS;
    private final Set<Integer> NUM_OF_REQUESTS_SET;

    public ClientHelper(Socket socket) {
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
        this.socket = socket;

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
    public boolean sendMessages(int request, int n) {
        // pull user inputted request from Operations Map
        String serverCommand = OPERATIONS.get(request);

        LocalTime start = LocalTime.now();

        ThreadPool threadPool = new ThreadPool();

            // call the user request the defined number of times and print response.
            for (int i = 0; i < n; i++) {
                ClientThread thread = new ClientThread(socket, serverCommand, i);
                thread.start();
                threadPool.addThread(thread);
            }
        while (threadPool.threadsRunning());

        Duration duration = Duration.between(start, LocalTime.now());

            // TODO: print average time format
            System.out.println("The total time for all calls: " + duration.toMillis());
            System.out.printf("The average time for all calls %f \n", (double) duration.toMillis() / (double)  n);

        return true;
    }

    private class ClientThread extends Thread {
        private final Socket socket;
        private final String serverCommand;
        private final int iteration;

        public ClientThread(Socket socket, String serverCommand, int iteration) {
            this.socket = socket;
            this.serverCommand = serverCommand;
            this.iteration = iteration;
        }

        public void run() {
            try {
                OutputStream output = this.socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println(serverCommand);
                System.out.println("iteration: " + iteration +  " message Sent");
                LocalTime start = LocalTime.now();

                // receive response
                InputStream serverResponse = null;
                serverResponse = socket.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(serverResponse));
                StringBuilder response = new StringBuilder();

                System.out.println("iteration: " + iteration +  " waiting response");
                // print response
                String serverResponseText = reader.readLine();
                while (!serverResponseText.equals("END")) {
                    response.append(serverResponseText);
                    response.append("\n");
                    serverResponseText = reader.readLine();
                }
                Duration duration = Duration.between(start, LocalTime.now());
                System.out.println(response + "\n" +
                        "Iteration: " + iteration + " request took " + duration.toMillis() + "milli-seconds\n");

            } catch (IOException e) {
                // TODO: update exceptions handling9
                throw new RuntimeException(e);
            }
        }
    }

    private class ThreadPool {
        private Set<Thread> threads;

        public ThreadPool() {
            threads = new HashSet<>();
        }

        public boolean threadsRunning() {
            if (threads.isEmpty()) {
                return true;
            }
            Set<Thread> revisedSet = new HashSet<>();
            for(Thread thread: threads) {
                if (thread.isAlive()) {
                    revisedSet.add(thread);
                }
            }
            threads = revisedSet;
            return !threads.isEmpty();
        }

        public void addThread(Thread thread) {
            threads.add(thread);
        }


    }
}
