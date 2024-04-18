package businessLogic;

import model.Server;
import model.Task;

import java.util.List;

public class Results {
    public static int peakHour = 0;
    public static int  maxClients = 0;
    public static double calculateAverageWaitingTime(List<Server> servers, int totalClients) {
        int waitingTimeTotal = 0;
        for (Server server : servers) {
            waitingTimeTotal += server.getTotalWaitingTime().get();
        }
        return totalClients > 0 ? (double) waitingTimeTotal / totalClients : 0.0;
    }
    public static double calculateAverageServiceTime(List<Task> tasks) {
        int totalServiceTime = 0;
        for (Task task : tasks) {
            totalServiceTime += task.getServiceTime();
        }
        return (double) totalServiceTime / tasks.size();
    }
    public static void findPeakHour(List<Server> servers, int simulationTime) {

            int clientsAtThisHour = 0;
            for (Server server : servers) {
                for(Task task: server.getTasks())
                    clientsAtThisHour++;
            }
            if (clientsAtThisHour > maxClients) {
                maxClients = clientsAtThisHour;
                peakHour = simulationTime;
            }
    }
}
