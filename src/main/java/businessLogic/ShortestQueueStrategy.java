package businessLogic;

import model.Server;
import model.Task;

import java.util.List;

public class ShortestQueueStrategy implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task t) {
        Server shortestQueue = servers.get(0);

        for (Server server : servers) {
            if (server.getTaskCount() < shortestQueue.getTaskCount()) {
                shortestQueue = server;
            }
        }
        shortestQueue.addTask(t);
    }
}