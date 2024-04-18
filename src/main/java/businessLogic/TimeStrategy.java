package businessLogic;

import model.Server;
import model.Task;

import java.util.List;

public class TimeStrategy implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task t) {
        Server minTimeQueue = servers.get(0);
        int minTime = minTimeQueue.getWaitingTime().get();

        for (Server server : servers) {
            int serverTime = server.getWaitingTime().get();
            if (serverTime < minTime) {
                minTime = serverTime;
                minTimeQueue = server;
            }
        }
        minTimeQueue.addTask(t);
    }
}