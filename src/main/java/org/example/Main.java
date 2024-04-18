package org.example;

import businessLogic.SimulationManager;
import gui.SimulationFrame;

public class Main {
    public static void main(String[] args) {
//        int numberOfClients = 4;
//        int numberOfQueues = 2;
//        int maxTasksPerServer = 50;
//        int timeLimit = 60;
//        int minArrivalTime = 2;
//        int maxArrivalTime = 30;
//        int minProcessingTime = 2;
//        int maxProcessingTime = 4;
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