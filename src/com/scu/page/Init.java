package com.scu.page;

import com.scu.page.PageReplacement.*;

import java.util.LinkedList;
import java.util.Queue;

public class Init {
    public static void main (String[] args) {
        int numOfProcess = 150;
        Queue<Process> processQueue = new LinkedList<>();

        // Generate 150 processes
        for(int i = 0; i < numOfProcess; i++) {
            processQueue.offer(new Process(i));
        }
        processQueue.stream().forEach(System.out::println);

        PageReplacementAlgorithm[] algorithmsPool = new PageReplacementAlgorithm[] {
                new FIFO(deepCopy(processQueue)),
                new LRU(deepCopy(processQueue)),
                new Optimal(deepCopy(processQueue)),
                new RandomPick(deepCopy(processQueue))
        };

        // Execute each algorithm for 1 min
        for(PageReplacementAlgorithm algorithm : algorithmsPool) {
            System.out.println(algorithm);
            for (int i = 0; i < 25; i++){
                Thread processThread = new Thread(algorithm);
                processThread.start();
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
