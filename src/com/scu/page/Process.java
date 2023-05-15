package com.scu.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Process {
    private Random rand;
    private int id = 0;
    private int size;
    public int frameSize;
    private long time;
    public int duration;
    private ArrayList<Integer> pages;   // All possible pages on the disk
    public ArrayList<Integer> frame;   // Size = 4, the piece of memory

    public Process(int id) {
        rand = new Random();
        int[] sizePool = {5, 11, 17, 31};
        int randIdx = rand.nextInt(sizePool.length);

        this.id = id;
        this.size = sizePool[randIdx];
        this.frameSize = 4;
        this.pages = new ArrayList<>();
        this.frame = new ArrayList<>();
        this.time = System.currentTimeMillis();
        this.duration = rand.nextInt(5) + 1;

        for (int i = 0; i < size; i++) {
            pages.add(i);
        }
    }

    public int locate(int idx) {
        System.out.println("Current is idx: " + idx);
        int probability = rand.nextInt(10) + 1;
        int nextIdx = -1;

        if (probability <= 7) {
            int[] nextPool = {-1, 0, 1};

            while(nextIdx < 0 || nextIdx > size - 1) {
                nextIdx = idx + nextPool[rand.nextInt(nextPool.length)];
                System.out.println("70%" + nextIdx);
            }
        } else {
            if (idx == 0) {
                System.out.println("30% and idx is 0");
                return rand.nextInt(size - 2) + 2;
            } else if (idx == size - 1) {
                System.out.println("30% and idx is size - 1");
                return rand.nextInt(size - 2);
            } else {
                ArrayList<Integer> candidateBuffer = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    if (i == idx - 1 || i == idx || i == idx + 1) {
                        continue;
                    } else {
                        candidateBuffer.add(i);
                    }
                }
                System.out.println("30% and idx is normal case");
                return candidateBuffer.get(rand.nextInt(candidateBuffer.size()));
            }
        }
        return nextIdx;
    }

    @Override
    public String toString() {
        return String.format(
                "PID: %d, Size: %d, Arrival Time: %d, Duration: %d",
                id, size, time, duration
        );
    }
}
