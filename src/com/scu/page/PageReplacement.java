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
            while (!queue.isEmpty()) {
                Process currentProcess;
                synchronized (queue) {
                    currentProcess = (! queue.isEmpty()) ? queue.poll() : null;
                }

                if (currentProcess != null) {
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
                Process p = queue.poll();
                
                //update memory
                memory.put(Thread.currentThread(),p);
                
                // get page references
                Queue<Integer> references = new LinkedList<Integer>();
                int cur_page = 0;
                int loopTimes = (int) (p.duration / 0.1);
                
                for (int i = 0; i < loopTimes; i++){
                    references.add(p.locate(cur_page));
                }

                // process execution
                for (int i=0; i < loopTimes; i++){
                    // get the reference page number
                    int page = references.poll();
                    if (!p.frame.contains(page)){
                        // miss
                        faults++;
                        if (p.frame.size() < 4){
                            // page frames not full
                            p.frame.add(page);
                        } else {
                            int maxIndex = -1;
                            int maxPage = -1;
                            // check every page in the frame
                            for (int j = 0; j < p.frame.size(); j++){
                                int framePage = p.frame.get(j);
                                int index = Arrays.asList(references).indexOf(framePage);
                                if (index == -1){
                                    // this page is not in the future page references
                                    maxPage = framePage;
                                    break;
                                }
                                if (index > maxIndex){
                                    maxIndex = index;
                                    maxPage = framePage;
                                }
                            }
                            p.frame.set(p.frame.indexOf(maxPage), page);
                        }
                    } else {
                        hits++;
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e){
                    e.printStackTrace();
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

        }
    }
}
