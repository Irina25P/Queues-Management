package businessLogic;

import model.Server;
import model.Task;

import java.util.*;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServers;
    private Strategy strategy;
    public Scheduler(int maxNoServers, int maxTasksPerServers) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServers = maxTasksPerServers;
        this.servers = new ArrayList<>();
        this.strategy = new ShortestQueueStrategy();

        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server();
            servers.add(server);
            Thread thread = new Thread(server);
            thread.start();
        }
    }
    public List<Server> getServers() {
        return servers;
    }
    public void changeStrategy(SelectionPolicy policy) {
        if(policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ShortestQueueStrategy();
        } else if(policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new TimeStrategy();
        }
    }
    public void dispatchTask(Task task) {
        if (this.strategy == null) {
            throw new IllegalStateException("Strategy not set");
        }
        List<Server> minimums = new ArrayList<>();
        int timeMin = servers.getFirst().getWaitingTime().get();
        int cont = 1;
        minimums.add(servers.getFirst());
        for(Server server : servers) {
            if(server.getWaitingTime().get() < timeMin) {
                cont = 1;
                minimums = new ArrayList<>();
                minimums.add(server);
            }
            else if(server.getWaitingTime().get() == timeMin) {
                cont++;
                minimums.add(server);
            }
        }
        if(cont == 1) {
            changeStrategy(SelectionPolicy.SHORTEST_TIME);
            this.strategy.addTask(this.servers, task);
        }
        else if(cont > 1) {
            changeStrategy(SelectionPolicy.SHORTEST_QUEUE);
            this.strategy.addTask(minimums, task);
        }
    }
}