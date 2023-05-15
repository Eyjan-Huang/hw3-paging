package com.scu.page;

import java.util.*;

public class PageReplacement {
    static abstract class PageReplacementAlgorithm implements Runnable{
        String name;
        int faults = 0, hits = 0;
        final Queue<Process> queue;
        LinkedHashMap<Thread, Process> memory = new LinkedHashMap<>();
        final int SIZE = 25;

        public PageReplacementAlgorithm(LinkedList<Process> queue) {
            this.queue = queue;
        }

        public Process fetchProcess() {
            synchronized (queue) {
                return (! queue.isEmpty()) ? queue.poll() : null;
            }
        }

        public String toString() {
            return String.format(
                    "%s:\nHits: %d\tFaults: %d, %s",
                    name, hits, faults, queue
            );
        }

        public String processRecord(Process process) {
            return String.format(
                "%d, Process id: %d, Exit, Page Size: %d, Service Duration: %d, Memory: ",
                System.currentTimeMillis(), process.id, process.size, process.time
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
            while (!queue.isEmpty()) {
                Process currentProcess = fetchProcess();

                if (currentProcess == null) {
                    break;
                }

                memory.put(Thread.currentThread(), currentProcess);
                ArrayList<Integer> currentFrame = currentProcess.frame;
                int limit = (int) (currentProcess.duration / 0.1);
                int accessPage = 0;

                for (int i = 0; i < limit; i++) {
                    if (currentFrame.contains(accessPage)) {
                        hits++;
                    } else {
                        faults++;

                        if (currentFrame.size() < currentProcess.frameSize) {
                            currentFrame.add(accessPage);
                        } else {
                            currentFrame.set(i % currentProcess.frameSize, accessPage);
                        }
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    accessPage = currentProcess.locate(accessPage);
                }
            }
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
            while (!queue.isEmpty()) {
                Process currentProcess = fetchProcess();

                if (currentProcess == null) {
                    break;
                }

                // update memory
                memory.put(Thread.currentThread(), currentProcess);
                
                // get page references
                Queue<Integer> references = new LinkedList<>();
                int cur_page = 0;
                int loopTimes = (int) (currentProcess.duration / 0.1);
                ArrayList<Integer> currentFrame = currentProcess.frame;
                
                for (int i = 0; i < loopTimes; i++){
                    references.add(cur_page);
                    cur_page = currentProcess.locate(cur_page);
                }

                // process execution
                for (int i = 0; i < loopTimes; i++){
                    // get the reference page number
                    int page = references.poll();
                    if (!currentFrame.contains(page)){
                        faults++;
                        if (currentFrame.size() < currentProcess.frameSize){
                            // page frames not full
                            currentFrame.add(page);
                        } else {
                            int maxIndex = -1;
                            int maxPage = -1;

                            // check every page in the frame
                            for (int framePage : currentFrame) {
                                int index = Arrays.asList(references).indexOf(framePage);

                                if (index == -1) {
                                    // this page is not in the future page references
                                    maxPage = framePage;
                                    break;
                                }

                                if (index > maxIndex) {
                                    maxIndex = index;
                                    maxPage = framePage;
                                }
                            }
                            currentFrame.set(currentFrame.indexOf(maxPage), page);
                        }
                    } else {
                        hits++;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static class RandomPick extends PageReplacementAlgorithm {
        public RandomPick(LinkedList<Process> queue) {
            super(queue);
            this.name = "RandomPick";
        }

        @Override
        public void run() {
            while(!queue.isEmpty()) {
                Process currentProcess = fetchProcess();

                if (currentProcess == null) {
                    break;
                }

                // update memory
                memory.put(Thread.currentThread(), currentProcess);

                // process execution
                int loopTimes = (int) (currentProcess.duration / 0.1);
                int currentPage = 0;
                ArrayList<Integer> currentFrame = currentProcess.frame;
                Random rand = new Random();

                for (int i = 0; i < loopTimes; i++) {
                    if (currentFrame.contains(currentPage)) {
                        hits++;
                    } else {
                        faults++;

                        if (currentFrame.size() < currentProcess.frameSize) {
                            // page frames are not full
                            currentFrame.add(currentPage);
                        } else {
                            // Selects a page to be evicted at random
                            currentFrame.set(rand.nextInt(currentProcess.frameSize), currentPage);
                        }
                    }

                    // update current page
                    currentPage = currentProcess.locate(currentPage);

                    // wait for 100 milisecs
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Each time a process completes, print a record
                // System.out.println(processRecord(currentProcess));
            }
        }
    }
}
