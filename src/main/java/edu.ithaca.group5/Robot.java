package edu.ithaca.group5;

public class Robot{

    Thread thread;
    DBConnector dbConnector;
    boolean running;
    int sleepTime;

    public Robot(DBConnector dbConnector, int sleepTime) {
        this.dbConnector = dbConnector;
        this.sleepTime = sleepTime;
    }

    public void validateOrders() {
        while(running) {
            dbConnector.validateAllOrders();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                running = false;
                System.out.println("Robot shutting down.");
            }
        }
    }

    public void stopValidating() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    public void startValidating() {
        stopValidating();
        running = true;
        thread = new Thread(this::validateOrders);
        thread.start();
    }
}
