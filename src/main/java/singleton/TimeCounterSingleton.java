package singleton;

import businessLogic.TimeCounter;

public class TimeCounterSingleton {
    private static final TimeCounter timeCounter = new TimeCounter();

    public static TimeCounter getTimeCounter() {
        return timeCounter;
    }
}

