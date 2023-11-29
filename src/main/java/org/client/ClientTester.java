//package org.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * User facing Client software to process requests with, and return results of server.
 */
public class ClientTester {
    public static void main(String[] args) {

        System.out.println("Welcome to the CNT 4504 Project 1 Client.  Please enter the Hostname and Port.");
        System.out.println("Hostname: ");
        String hostname = "139.62.210.155";
        System.out.println("Port: ");
        int port = 4900;
        System.out.println("Connecting to the server...");

        ClientHelperTester client = new ClientHelperTester(hostname, port);

        System.out.println("Running Tests...");
        List<Integer> options = new ArrayList<>();
        options.add(0);
        options.add(1);
        options.add(2);
        options.add(3);
        options.add(4);
        options.add(5);

        List<Integer> numRequests = new ArrayList<>();
        numRequests.add(1);
        numRequests.add(5);
        numRequests.add(10);
        numRequests.add(15);
        numRequests.add(20);
        numRequests.add(25);
        numRequests.add(100);

        List<TestResults> results = new ArrayList<>();
        List<TestResults> tempResults;
        long runningTotal;
        double totalAverage;

        for (Integer option: options) {
            tempResults = new ArrayList<>();
            runningTotal = 0;
            for (Integer num: numRequests) {
                for (int i = 0; i < 20; i++){
                    tempResults.add(client.sendMessages(option, num));
                }
                for (TestResults temp: tempResults) {
                    runningTotal += temp.totalTime;
                }
                totalAverage = (double) runningTotal / 20.0;
                results.add(new TestResults(option, num, (long) totalAverage, totalAverage / (num)));
            }
        }

        for (TestResults result: results) {
            System.out.println("***************************************************");
            System.out.println("Operation: " + result.command);
            System.out.println("Number of Requests: " + result.numRequests);
            System.out.println("Total time: " + result.totalTime);
            System.out.println("Average time: " + result.averageTime);
            System.out.println("***************************************************");
            System.out.println();
        }
    }


}

