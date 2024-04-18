package model;
import businessLogic.TimeCounter;
import singleton.TimeCounterSingleton;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger totalWaitingTime;
    private AtomicInteger waitingTime;
    private TimeCounter timeCounter = TimeCounterSingleton.getTimeCounter();
    public AtomicInteger getTotalWaitingTime() {
        return totalWaitingTime;
    }
    public Server() {
        this.tasks = new LinkedBlockingQueue<>();
        this.waitingTime = new AtomicInteger(0);
        this.totalWaitingTime = new AtomicInteger(0);
    }
    public int getTaskCount() {
        return tasks.size();
    }
    public void addTask(Task task) {
        tasks.offer(task);
        waitingTime.addAndGet(task.getServiceTime());
    }
    public AtomicInteger getWaitingTime() {
        return waitingTime;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Task currentTask = tasks.peek();
                if (currentTask != null) {
                    currentTask.setWaitTime(timeCounter.getTime() - currentTask.getArrivalTime());
                    System.out.println(currentTask.getWaitTime() + " task:" + currentTask.getId());
                    totalWaitingTime.set(totalWaitingTime.get() + currentTask.getWaitTime());
                    while (currentTask.getServiceTime() > 0) {
                        waitingTime.decrementAndGet();
                        Thread.sleep(1000);
                        currentTask.decrementServiceTime();
                    }
                    tasks.poll();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    public Task[] getTasks() {
        return tasks.toArray(new Task[0]);
    }
    public String getLoggableState() {
        if (tasks.isEmpty()) {
            return "closed";
        } else {
            StringBuilder builder = new StringBuilder();
            for (Task task : tasks) {
                builder.append(task.toString()).append("; ");
            }
            return builder.toString();
        }
    }

}
