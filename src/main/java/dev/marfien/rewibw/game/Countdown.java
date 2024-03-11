package dev.marfien.rewibw.game;

public interface Countdown {

    void start();

    void stop();

    boolean isRunning();

    int getSeconds();

}
