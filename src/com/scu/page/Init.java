package com.scu.page;

import com.scu.page.PageReplacement.*;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class Init {
    public static void main (String[] args) {
        int numOfProcess = 150;
        final int sizeOfThread = 25;
        Queue<Process> processQueue = new LinkedList<>();
        Thread[] tPool = new Thread[sizeOfThread];

        // Generate 150 processes
        for(int i = 0; i < numOfProcess; i++) {
            processQueue.offer(new Process(i));
        }

        PageReplacementAlgorithm[] algorithmsPool = new PageReplacementAlgorithm[] {
                new FIFO(deepCopy(processQueue)),
                new LRU(deepCopy(processQueue)),
                new Optimal(deepCopy(processQueue)),
                new RandomPick(deepCopy(processQueue))
        };

        // Execute each algorithm for 5 runs of 1 minute
        for(PageReplacementAlgorithm algorithm : algorithmsPool) {
            Timer timer = new Timer();
            int delay = 0; // delay before the first run
            int period = 30 * 1000; // period between successive runs(1 min)
            int repetition = 5;

            timer.schedule(new TimerTask() {
                int count = 0; // counter for the number of runs
                float hitRatio = 0;

                public void run(){
                    count++;
                    System.out.println(algorithm.name + " run #" + count);

                    // varialbles initialize
                    algorithm.initialize(deepCopy(processQueue));
                    
                    // run the algorithm
                    for (int i = 0; i < sizeOfThread; i++){
                        tPool[i] = new Thread(algorithm);
                        tPool[i].start();
                    }
        
                    try {
                        for (int i = 0; i < sizeOfThread; i++) {
                            tPool[i].join();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    hitRatio += ((float)algorithm.hits/algorithm.faults);
                    System.out.println(algorithm.name + "***Miss" + algorithm.faults + "***Hits" + 
                                        algorithm.hits + "***Hits/Miss" + ((float)algorithm.hits/algorithm.faults) + '\n');

                    if (count == repetition) {
                        System.out.println(algorithm.name + " avgProcess: " + algorithm.procNum/repetition +
                                            "   avgHitMissRatio: " + hitRatio/repetition + '\n');
                        timer.cancel();
                    }
                }
            }, delay, period);

            // pause the execution of next loop
            try {
                Thread.sleep(period*repetition);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static LinkedList<Process> deepCopy(Queue<Process> q) {
        LinkedList<Process> copied = new LinkedList<>();

        for(Process element : q) {
            copied.offer(element);
        }

        return copied;
    }
}
