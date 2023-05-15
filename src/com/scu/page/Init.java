package com.scu.page;

import com.scu.page.PageReplacement.*;

import java.util.LinkedList;
import java.util.Queue;

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

        // Execute each algorithm for 1 min
        for(PageReplacementAlgorithm algorithm : algorithmsPool) {
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

            System.out.println(algorithm.name + "***" + algorithm.faults + "***" + 
                                algorithm.hits + "***" + ((float)algorithm.hits/algorithm.faults) + '\n');
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
