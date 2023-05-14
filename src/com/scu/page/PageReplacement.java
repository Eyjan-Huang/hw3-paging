package com.scu.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class PageReplacement {
    static abstract class PageReplacementAlgorithm {
        String name;
        int faults = 0, hits = 0;
        ArrayList<HashMap<Thread, Process>> memory = new ArrayList<>();
        final int SIZE = 25;

        abstract void run(LinkedList<Process> queue);

        public boolean inMemory(Process p) {
            return memory.contains(p);
        }

        public String toString() {
            return String.format(
                    "%s:\nHits: %d\tFaults: %d",
                    name, hits, faults
            );
        }
    }

    static class FIFO extends PageReplacementAlgorithm {
        public FIFO() {
            this.name = "FIFO";
        }

        @Override
        void run(LinkedList<Process> queue) {

        }
    }

    static class LRU extends PageReplacementAlgorithm {
        public LRU() {
            this.name = "LRU";
        }

        @Override
        void run(LinkedList<Process> queue) {

        }
    }

    static class Optimal extends PageReplacementAlgorithm {
        public Optimal() {
            this.name = "Optimal";
        }

        @Override
        void run(LinkedList<Process> queue) {

        }
    }

    static class RandomPick extends PageReplacementAlgorithm {
        public RandomPick() {
            this.name = "RandomPick";
        }

        @Override
        void run(LinkedList<Process> queue) {

        }
    }
}
