package org.example;

import businessLogic.SimulationManager;
import gui.SimulationFrame;

public class Main {
    public static void main(String[] args) {
//        int numberOfClients = 50;
//        int numberOfQueues = 5;
//        int maxTasksPerServer = 50;
//        int timeLimit = 60;
//        int minArrivalTime = 2;
//        int maxArrivalTime = 40;
//        int minProcessingTime = 1;
//        int maxProcessingTime = 7;
//
//        SimulationManager manager = new SimulationManager(numberOfClients,numberOfQueues, maxTasksPerServer, timeLimit,
//                minArrivalTime, maxArrivalTime, minProcessingTime, maxProcessingTime);
//        Thread simulationThread = new Thread(manager);
//        simulationThread.start();
//
//        try {
//            simulationThread.join();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            System.err.println("Simulation was interrupted.");
//        }
//        System.out.println("Simulation complete. Check 'simulation_log.txt' for the log of events.");
//        manager.printGeneratedTasks();
        new SimulationFrame();
    }
}