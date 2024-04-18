package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import businessLogic.*;
import model.Server;
import model.Task;
import singleton.TimeCounterSingleton;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class SimulationFrame extends JFrame {
    private JTextField numberOfClientsField, numberOfQueuesField, simulationIntervalField;
    private JTextField minArrivalTimeField, maxArrivalTimeField, minServiceTimeField, maxServiceTimeField;
    private JButton startButton;
    private JTextArea logArea;
    private SimulationManager simulationManager;
    private TimeCounter timeCounter = TimeCounterSingleton.getTimeCounter();
    private List<JProgressBar> progressBars;
    public SimulationFrame() {

        setTitle("Queue Management Simulation");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        inputPanel.add(new JLabel("Number of Clients (N):"));
        numberOfClientsField = new JTextField("50");
        inputPanel.add(numberOfClientsField);

        inputPanel.add(new JLabel("Number of Queues (Q):"));
        numberOfQueuesField = new JTextField("5");
        inputPanel.add(numberOfQueuesField);

        inputPanel.add(new JLabel("Simulation Interval (MAX):"));
        simulationIntervalField = new JTextField("60");
        inputPanel.add(simulationIntervalField);

        inputPanel.add(new JLabel("Min Arrival Time:"));
        minArrivalTimeField = new JTextField("2");
        inputPanel.add(minArrivalTimeField);

        inputPanel.add(new JLabel("Max Arrival Time:"));
        maxArrivalTimeField = new JTextField("40");
        inputPanel.add(maxArrivalTimeField);

        inputPanel.add(new JLabel("Min Service Time:"));
        minServiceTimeField = new JTextField("1");
        inputPanel.add(minServiceTimeField);

        inputPanel.add(new JLabel("Max Service Time:"));
        maxServiceTimeField = new JTextField("7");
        inputPanel.add(maxServiceTimeField);
        this.simulationManager = new SimulationManager(Integer.parseInt(numberOfClientsField.getText()), Integer.parseInt(numberOfQueuesField.getText()), Integer.parseInt(numberOfClientsField.getText()), Integer.parseInt(simulationIntervalField.getText()), Integer.parseInt(minArrivalTimeField.getText()), Integer.parseInt(maxArrivalTimeField.getText()), Integer.parseInt(minServiceTimeField.getText()), Integer.parseInt(maxServiceTimeField.getText()));
        add(inputPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        progressBars = new ArrayList<>();
        JPanel progressPanel = new JPanel(new GridLayout(Integer.parseInt(numberOfQueuesField.getText()), 1));
        for (int i = 0; i < Integer.parseInt(numberOfQueuesField.getText()); i++) {
            JProgressBar progressBar = new JProgressBar(0, 50); // Assuming a max of 50 tasks per server
            progressBar.setStringPainted(true);
            progressPanel.add(progressBar);
            progressBars.add(progressBar);
        }

        add(progressPanel, BorderLayout.EAST);

        startButton = new JButton("Start Simulation");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });
        add(startButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void generateAndLogTasks(int numberOfClients, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime, BufferedWriter logWriter) throws IOException {
        logWriter.write("Generated clients:\n");
        Random random = new Random();
        for (int i = 0; i < numberOfClients; i++) {
            int arrivalTime = minArrivalTime + random.nextInt(maxArrivalTime - minArrivalTime + 1);
            int serviceTime = minServiceTime + random.nextInt(maxServiceTime - minServiceTime + 1);
            Task newTask = new Task(i + 1, arrivalTime, serviceTime);
            logWriter.write(newTask.toString() + "; ");
        }
        logWriter.write("\n\n");
        logWriter.flush();
    }

    private void startSimulation() {
        int numberOfClients = Integer.parseInt(numberOfClientsField.getText());
        int numberOfQueues = Integer.parseInt(numberOfQueuesField.getText());
        int simulationInterval = Integer.parseInt(simulationIntervalField.getText());
        int minArrivalTime = Integer.parseInt(minArrivalTimeField.getText());
        int maxArrivalTime = Integer.parseInt(maxArrivalTimeField.getText());
        int minServiceTime = Integer.parseInt(minServiceTimeField.getText());
        int maxServiceTime = Integer.parseInt(maxServiceTimeField.getText());

        logArea.append("Starting simulation...\n");
        logArea.setText("");

        Scheduler scheduler = new Scheduler(numberOfQueues, numberOfClients);
        scheduler.changeStrategy(SelectionPolicy.SHORTEST_TIME);

        final BufferedWriter[] logWriter = new BufferedWriter[1];
        try {
            logWriter[0] = new BufferedWriter(new FileWriter("simulation_log.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to create log file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Thread simulationThread = new Thread(() -> {
            int currentTime = 0;
            try {
                int peakHour;
                logWriter[0].write("Simulation started.\n");
                double averageServiceTime = Results.calculateAverageServiceTime(simulationManager.getGeneratedTasks());
                logWaitingClients(simulationManager.getGeneratedTasks(), currentTime, logWriter[0]);
                while (currentTime <= simulationInterval && !Thread.currentThread().isInterrupted()) {
                    int finalCurrentTime = currentTime;

                    List<Task> tasksToDispatch = new ArrayList<>();
                    Iterator<Task> taskIterator = simulationManager.getGeneratedTasks().iterator();
                    while (taskIterator.hasNext()) {
                        Task task = taskIterator.next();
                        if (task.getArrivalTime() <= finalCurrentTime) {
                            scheduler.dispatchTask(task);
                            tasksToDispatch.add(task);
                            taskIterator.remove();
                        }
                    }

                    String log = getStateLog(finalCurrentTime, scheduler, tasksToDispatch);
                    SwingUtilities.invokeLater(() -> logArea.append(log));
                    logWriter[0].write(log);
                    //Results.findPeakHour(simulationManager.getScheduler().getServers(), currentTime);
                    Results.findPeakHour(scheduler.getServers(),timeCounter.getTime());
                    currentTime++;
                    timeCounter.incrementTime();
                    Thread.sleep(1000);
                }

                int totalClients = simulationManager.getGeneratedTasks().size();
                double averageWaitingTime = Results.calculateAverageWaitingTime(scheduler.getServers(), numberOfClients);
                //int peakHour = Results.findPeakHour(scheduler.getServers(), simulationInterval);

                logArea.append("Average Waiting Time: " + averageWaitingTime + " seconds\n");
                logArea.append("Average Service Time: " + averageServiceTime + " seconds\n");
                logArea.append("Peak Hour: " + Results.peakHour);
                //logArea.append("Peak Hour: " + peakHour + "\n");
                SwingUtilities.invokeLater(() -> logArea.append("Simulation ended.\n"));
                logWriter[0].write("Simulation ended.\n");
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
                SwingUtilities.invokeLater(() -> logArea.append("Simulation interrupted.\n"));
            } finally {
                try {
                    logWriter[0].close();
                } catch (IOException e) {
                    System.err.println("Error closing log writer: " + e.getMessage());
                }
            }
        });

        simulationThread.start();
    }

    private void logWaitingClients(List<Task> tasks, int currentTime, BufferedWriter logWriter) throws IOException {
        StringBuilder waitingClientsBuilder = new StringBuilder();
        for (Task task : tasks) {
            if (task.getArrivalTime() > currentTime) {
                if (waitingClientsBuilder.length() > 0) {
                    waitingClientsBuilder.append("; ");
                }
                waitingClientsBuilder.append(task.toString());
            }
        }
        String waitingClients = waitingClientsBuilder.toString();
        logWriter.write(waitingClients);
    }


    private String getStateLog(int currentTime, Scheduler scheduler, List<Task> tasksToDispatch) {
        StringBuilder builder = new StringBuilder();
        builder.append("\nTime ").append(currentTime).append("\n");

        StringBuilder waitingClientsBuilder = new StringBuilder();
        for (Task task : tasksToDispatch) {
            waitingClientsBuilder.append(task.toString()).append("; ");
        }
        String waitingClients = waitingClientsBuilder.toString();
        builder.append("Added clients: ")
                .append(waitingClients.isEmpty() ? "\n" : waitingClients.trim() + "\n");

        for (int i = 0; i < scheduler.getServers().size(); i++) {
            Server server = scheduler.getServers().get(i);
            if(server.getTasks().length!=0)
                progressBars.get(i).setValue(100 - (int)((double)server.getTasks()[0].getServiceTime()/server.getTasks()[0].getInitialServiceTime()*100));
            builder.append("Queue ").append(i + 1).append(": ")
                    .append(server.getLoggableState().isEmpty() ? "closed" : server.getLoggableState()).append(("\n"));
        }

        return builder.toString();
    }


}