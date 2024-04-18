package businessLogic;

import businessLogic.Scheduler;
import model.Server;
import model.Task;
import singleton.TimeCounterSingleton;

import java.io.BufferedWriter;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

public class SimulationManager implements Runnable {
    private int numberOfClients;
    private int timeLimit;
    private int maxProcessingTime;
    private int minProcessingTime;
    private int maxArrivalTime;
    private int minArrivalTime;
    private Scheduler scheduler;
    private List<Task> generatedTasks;
    private final PriorityQueue<Task> waitingClients = new PriorityQueue<>((o1, o2) -> o1.getArrivalTime() - o2.getArrivalTime());
    private final Random random = new Random();
    private BufferedWriter logWriter;
    private Set<Task> dispatchedTasks = new HashSet<>();
    private int totalServiceTime;
    private TimeCounter timeCounter = TimeCounterSingleton.getTimeCounter();
    public SimulationManager(int numberOfClients, int numberOfQueues, int maxTasksPerServer, int timeLimit,
                             int minArrivalTime, int maxArrivalTime,
                             int minProcessingTime, int maxProcessingTime) {
        this.numberOfClients = numberOfClients;
        this.timeLimit = timeLimit;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minArrivalTime = minArrivalTime;
        this.scheduler = new Scheduler(numberOfQueues, maxTasksPerServer);
        this.generatedTasks = new ArrayList<>();
        this.scheduler.changeStrategy(SelectionPolicy.SHORTEST_TIME);
        this.totalServiceTime = 0;
        generateRandomTasks();
        try {
            logWriter = new BufferedWriter(new FileWriter("simulation_log.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<Task> getGeneratedTasks() {
        return generatedTasks;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    private void generateRandomTasks() {
        for (int i = 0; i < numberOfClients; i++) {
            int arrivalTime = minArrivalTime + random.nextInt(maxArrivalTime - minArrivalTime + 1);
            int serviceTime = minProcessingTime + random.nextInt(maxProcessingTime - minProcessingTime + 1);
            totalServiceTime += serviceTime;
            Task newTask = new Task(i, arrivalTime, serviceTime);
            generatedTasks.add(newTask);
            waitingClients.offer(newTask);
        }
    }

    @Override
    public void run() {
        try {
            logWriter.write("Simulation started.\n");

            while(timeCounter.getTime() < timeLimit) {
                timeCounter.incrementTime();
                System.out.println(timeCounter.getTime());
                while (!waitingClients.isEmpty() && waitingClients.peek().getArrivalTime() <= timeCounter.getTime()) {
                    Task task = waitingClients.poll();
                    scheduler.dispatchTask(task);
                    dispatchedTasks.add(task);
                }
                logState(timeCounter.getTime());
                Results.findPeakHour(scheduler.getServers(),timeCounter.getTime());
                Thread.sleep(1000);
            }
            int totalClients = generatedTasks.size();
            double averageWaitingTime = Results.calculateAverageWaitingTime(scheduler.getServers(), totalClients);

            logWriter.write("Average Waiting Time: " + averageWaitingTime + "\n");
            logWriter.write("Average Service Time: " + ((double) totalServiceTime / numberOfClients) + "\n");
            logWriter.write("\nSimulation ended.\n");
            logWriter.flush();
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        } finally {
            finishSimulation();
        }
    }
    private void logState(int currentTime) throws IOException {
        logWriter.write("Time " + currentTime + "\nWaiting clients: ");
        for (Task task : generatedTasks) {
            if (!dispatchedTasks.contains(task) && task.getArrivalTime() > currentTime) {
                logWriter.write(task.toString() + "; ");
            }
        }
        logWriter.write("\n");

        for (int i = 0; i < scheduler.getServers().size(); i++) {
            Server server = scheduler.getServers().get(i);
            String queueState = server.getLoggableState();
            logWriter.write("Queue " + (i + 1) + ": " + (queueState.isEmpty() ? "closed" : queueState) + "\n");
            logWriter.write("Total Waiting Time: " + server.getWaitingTime().get() + " seconds\n");
        }

        logWriter.write("\n");
        logWriter.flush();
    }
    private void finishSimulation() {
        try {
            if (logWriter != null) {
                logWriter.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing log writer: " + e.getMessage());
        }
    }

}