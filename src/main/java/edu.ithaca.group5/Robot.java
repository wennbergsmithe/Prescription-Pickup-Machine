package edu.ithaca.group5;

public class Robot{

    Thread thread;
    DBConnector dbConnector;
    boolean running;
    public Robot(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    public void validateOrders() {
        while(running) {
            dbConnector.validateAllOrders();
            try {
                Thread.sleep(5000);
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
