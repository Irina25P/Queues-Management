package businessLogic;

import java.util.concurrent.atomic.AtomicInteger;

public class TimeCounter {
    private AtomicInteger time;

    public TimeCounter() {
        this.time = new AtomicInteger(0);
    }

    public int getTime() {
        return time.get();
    }

    public void incrementTime() {
        this.time.incrementAndGet();
    }
}
