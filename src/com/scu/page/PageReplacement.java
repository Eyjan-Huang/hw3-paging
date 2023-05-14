package com.scu.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class PageReplacement {
    static abstract class PageReplacementAlgorithm implements Runnable{
        String name;
        int faults = 0, hits = 0;
        Queue<Process> queue;
        ArrayList<HashMap<Thread, Process>> memory = new ArrayList<>();
        final int SIZE = 25;

        public PageReplacementAlgorithm(LinkedList<Process> queue) {
            this.queue = queue;
        }

        public String toString() {
            return String.format(
                    "%s:\nHits: %d\tFaults: %d, %s",
                    name, hits, faults, queue
            );
        }
    }

    static class FIFO extends PageReplacementAlgorithm {
        public FIFO(LinkedList<Process> queue) {
            super(queue);
            this.name = "FIFO";
        }

        @Override
        public void run() {

        }
    }

    static class LRU extends PageReplacementAlgorithm {
        public LRU(LinkedList<Process> queue) {
            super(queue);
            this.name = "LRU";
        }

        @Override
        public void run() {

        }
    }

    static class Optimal extends PageReplacementAlgorithm {
        public Optimal(LinkedList<Process> queue) {
            super(queue);
            this.name = "Optimal";
        }


        @Override
        public void run() {

        }
    }

    static class RandomPick extends PageReplacementAlgorithm {
        public RandomPick(LinkedList<Process> queue) {
            super(queue);
            this.name = "RandomPick";
        }

        @Override
        public void run() {

        }
    }
}
