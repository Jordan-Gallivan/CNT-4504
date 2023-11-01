package org.client;

public class TestResults {
    public int command;
    public int numRequests;
    public long totalTime;
    public double averageTime;

    public TestResults(int command, int numRequests, long totalTime, double averageTime) {
        this.command = command;
        this.numRequests = numRequests;
        this.totalTime = totalTime;
        this.averageTime = averageTime;
    }
}
