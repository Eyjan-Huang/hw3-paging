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
//        queue.stream().forEach(System.out::println);

        PageReplacementAlgorithm[] algorithmsPool = new PageReplacementAlgorithm[] {
                new FIFO(), new LRU(), new Optimal(), new RandomPick()
        };

        // Execute each algorithm for 1 mins
        for(PageReplacementAlgorithm algorithm : algorithmsPool) {
            // Deep copy the original queue for each algorithm execution
            LinkedList<Process> queue = deepCopy(processQueue);
            System.out.println(queue);
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
