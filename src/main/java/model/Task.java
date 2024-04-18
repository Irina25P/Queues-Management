package model;

public class Task {
    private int id;
    private int arrivalTime;
    private int serviceTime;
    private int initialServiceTime;
    private int waitTime;
    public Task(int id, int arrivalTime, int serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.initialServiceTime = serviceTime;
    }
    public int getWaitTime() {
        return waitTime;
    }
    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }
    public int getId() {
        return id;
    }
    public int getArrivalTime() {
        return arrivalTime;
    }
    public int getServiceTime() {
        return serviceTime;
    }

    public int getInitialServiceTime() {
        return initialServiceTime;
    }

    public void setInitialServiceTime(int initialServiceTime) {
        this.initialServiceTime = initialServiceTime;
    }

    @Override
    public String toString() {
        return "(" + id + "," + arrivalTime + "," + serviceTime + ")";
    }
    public void decrementServiceTime() {
        if (this.serviceTime > 0) {
            this.serviceTime--;
        }
    }
}
