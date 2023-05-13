package com.scu.page;

import java.util.Random;

public class Process {
    private static int ID = 0;
    private int size;
    private long time;
    private int duration;

    public Process () {
        Random rand = new Random();
        int[] sizePool = {5, 11, 17, 31};
        int randIdx = rand.nextInt(sizePool.length);

        ID++;
        this.size = sizePool[randIdx];
        this.time = System.currentTimeMillis();
        this.duration = rand.nextInt(5) + 1;
    }

    @Override
    public String toString () {
        return String.format(
                "PID: %d, Size: %d, Arrival Time: %d, Duration: %d",
                ID, size, time, duration
        );
    }
}
