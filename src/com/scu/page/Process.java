package com.scu.page;

import java.util.ArrayList;
import java.util.Random;

public class Process {
    private int id = 0;
    private int size;
    private long time;
    private int duration;
    private ArrayList<Integer> pages;   // All possible poges on the disk
    private ArrayList<Integer> frame;   // Size = 4, the piece of memory

    public Process(int id) {
        Random rand = new Random();
        int[] sizePool = {5, 11, 17, 31};
        int randIdx = rand.nextInt(sizePool.length);

        this.id = id;
        this.size = sizePool[randIdx];
        this.frame = new ArrayList<>();
        this.time = System.currentTimeMillis();
        this.duration = rand.nextInt(5) + 1;

        for (int i = 0; i < size; i++) {
            pages.add(i);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "PID: %d, Size: %d, Arrival Time: %d, Duration: %d",
                id, size, time, duration
        );
    }
}
