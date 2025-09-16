/*
You are building a monitoring service. A critical component of this service is a log rate limiter that ensures the system does not get overwhelmed by excessive logging. The log rate limiter should allow a maximum of 10 log entries per second. If the limit is exceeded, additional log entries should be discarded until the next second.
Implement a class LogRateLimiter with the following methods:
- void log(String message): Logs a message if the rate limit has not been exceeded. If the limit is exceeded, the message should be discarded.
- int getLogCount(): Returns the number of log entries that have been successfully logged in the current second.
You can use System.currentTimeMillis() to get the current time in milliseconds.
*/

Public class TheLogRateLimiter {
    private final int MAX_LOGS_PER_SECOND = 10;
    private long currentSecond;
    private int logCount;
    private final Object lock = new Object();

    public TheLogRateLimiter() {
        this.currentSecond = System.currentTimeMillis() /1000;
        this.logCount = 0;
    }
    public void log(String message) {
        long now = System.currentTimeMillis() /1000;
        synchronized (lock) {
            if (now != currentSecond) {
                currentSecond = now;
                logCount = 0;   
    }
            if (logCount < MAX_LOGS_PER_SECOND) {
                System.out.println(message);
                logCount++;
            }
        }
    }
    public int getLogCount() {
        long now = System.currentTimeMillis() /1000;
        synchronized (lock) {
            if (now != currentSecond) {
                currentSecond = now;
                logCount = 0;
            }
            return logCount;
        }
    }


}
