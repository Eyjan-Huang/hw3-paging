package com.scu.page;

import java.util.*;

public class PageReplacement {
    static abstract class PageReplacementAlgorithm implements Runnable{
        String name;
        int faults = 0, hits = 0, procNum = 0, referenceCount = 0;
        boolean hitSign;
        int evictedPage;
        public Queue<Process> queue;
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

        public synchronized String processRecord(Process process) {
            StringBuilder memoryMap = new StringBuilder("<");
            String empMem = ".";
            memory.forEach((thread,proc) -> {
                if (proc == null) {
                    memoryMap.append(empMem + ",");
                } else {
                    memoryMap.append(proc.id + ",");
                }
            });
            memoryMap.deleteCharAt(memoryMap.length() - 1);
            memoryMap.append(">");

            return String.format(
                "%d, Process id: %d, Exit, Page Size: %d, Service Duration: %d, \nMemory: %s\n",
                System.currentTimeMillis(), process.id, process.size, process.duration, memoryMap.toString()
            );
        }

        public void initialize(LinkedList<Process> queue) {
            hits = 0;
            faults = 0;
            memory.clear();
            this.queue = queue;
        }

        public synchronized void referenceRecord(int id, int reference, boolean sig, int evicted){
            if (sig) {
                System.out.println("<" + System.currentTimeMillis() + ", procId: " + id + 
                                    ", reference:" + reference + ", " +"Hit>");
            } else if (evicted >= 0) {
                System.out.println("<" + System.currentTimeMillis() + ", procId: " + id + 
                                    ", reference:" + reference + ", " + "Miss" + ", evictedPage: "+ evicted + ">");
            } else{
                System.out.println("<" + System.currentTimeMillis() + ", procId: " + id + 
                                    ", reference:" + reference + ", " + "Miss>");
            }
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

                procNum++;
                memory.put(Thread.currentThread(), currentProcess);
                ArrayList<Integer> currentFrame = currentProcess.frame;
                int limit = (int) (currentProcess.duration / 0.1);
                int accessPage = 0;

                for (int i = 0; i < limit; i++) {
                    if (currentFrame.contains(accessPage)) {
                        hits++;
                        hitSign = true;
                    } else {
                        faults++;
                        hitSign = false;

                        if (currentFrame.size() < currentProcess.frameSize) {
                            currentFrame.add(accessPage);
                            evictedPage = -1;
                        } else {
                            evictedPage = currentFrame.get(i % currentProcess.frameSize);
                            currentFrame.set(i % currentProcess.frameSize, accessPage);
                        }
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (referenceCount > 0){
                        referenceRecord(currentProcess.id, accessPage, hitSign, evictedPage);
                        referenceCount--;
                    }
                    accessPage = currentProcess.locate(accessPage);
                }

                // Each time a process completes, print a record
                // System.out.println(processRecord(currentProcess));
                memory.put(Thread.currentThread(), null);
                currentProcess.frame.clear();
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
            while (!queue.isEmpty()) {
                Process currentProcess = fetchProcess();

                if (currentProcess == null) {
                    break;
                }

                procNum++;
                memory.put(Thread.currentThread(), currentProcess);
                ArrayList<Integer> currentFrame = currentProcess.frame;
                int limit = (int) (currentProcess.duration / 0.1);
                int accessPage = 0;
                LRUCache<Integer, Integer> cache = new LRUCache<>(currentProcess.frameSize);
                Map.Entry<Integer, Integer> eldestEntry;

                for (int i = 0; i < limit; i++) {
                    if (currentFrame.contains(accessPage)) {
                        hits++;
                        hitSign = true;
                        cache.get(accessPage);
                    } else {
                        faults++;
                        hitSign = false;

                        if (currentFrame.size() < currentProcess.frameSize) {
                            currentFrame.add(accessPage);
                            int idx = currentFrame.indexOf(accessPage);
                            cache.put(accessPage, idx);
                            evictedPage = -1;
                        } else {
                            eldestEntry = cache.entrySet().iterator().next();
                            int targetIdx = eldestEntry.getValue();
                            evictedPage = currentFrame.get(targetIdx);
                            currentFrame.set(targetIdx, accessPage);
                            cache.put(accessPage, targetIdx);
                        }
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (referenceCount > 0){
                        referenceRecord(currentProcess.id, accessPage, hitSign, evictedPage);
                        referenceCount--;
                    }
                    accessPage = currentProcess.locate(accessPage);
                }

                // Each time a process completes, print a record
                // System.out.println(processRecord(currentProcess));
                memory.put(Thread.currentThread(), null);
                currentProcess.frame.clear();
            }
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

                procNum++;
                // update memory
                memory.put(Thread.currentThread(), currentProcess);
                
                // get page references
                Queue<Integer> references = new LinkedList<>();
                HashMap<Integer, Queue<Integer>> nextAccess = new HashMap<>();
                int cur_page = 0;
                int loopTimes = (int) (currentProcess.duration / 0.1);
                ArrayList<Integer> currentFrame = currentProcess.frame;
                
                for (int i = 0; i < loopTimes; i++){
                    // nextAccess to store the indices of the pages in the reference queue
                    if (nextAccess.containsKey(cur_page)) {
                        nextAccess.get(cur_page).offer(i);
                    } else {
                        Queue<Integer> temp = new LinkedList<>();
                        temp.offer(i);
                        nextAccess.put(cur_page, temp);
                    }
                    references.offer(cur_page);
                    cur_page = currentProcess.locate(cur_page);
                }

                // process execution
                for (int i = 0; i < loopTimes; i++){
                    // get the reference page number
                    int page = references.poll();
                    nextAccess.get(page).poll();
                    if (!currentFrame.contains(page)){
                        faults++;
                        hitSign = false;

                        if (currentFrame.size() < currentProcess.frameSize){
                            // page frames not full
                            currentFrame.add(page);
                            evictedPage = -1;
                        } else {
                            int maxIndex = -1;
                            int maxPage = -1;

                            // check every page in the frame
                            for (int framePage : currentFrame) {

                                if (nextAccess.get(framePage).peek() == null) {
                                    // this page is not in the future page references
                                    maxPage = framePage;
                                    break;
                                }

                                int index = nextAccess.get(framePage).peek();
                                if (index > maxIndex) {
                                    maxIndex = index;
                                    maxPage = framePage;
                                }
                            }
                            evictedPage = currentFrame.get(currentFrame.indexOf(maxPage));
                            currentFrame.set(currentFrame.indexOf(maxPage), page);
                        }
                    } else {
                        hits++;
                        hitSign = true;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (referenceCount > 0){
                        referenceRecord(currentProcess.id, page, hitSign, evictedPage);
                        referenceCount--;
                    }
                }

                // Each time a process completes, print a record
                // System.out.println(processRecord(currentProcess));
                memory.put(Thread.currentThread(), null);
                currentProcess.frame.clear();
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

                procNum++;
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
                        hitSign = true;
                    } else {
                        faults++;
                        hitSign = false;

                        if (currentFrame.size() < currentProcess.frameSize) {
                            // page frames are not full
                            currentFrame.add(currentPage);
                            evictedPage = -1;
                        } else {
                            // Selects a page to be evicted at random
                            int randIdx = rand.nextInt(currentProcess.frameSize);
                            evictedPage = currentFrame.get(randIdx);
                            currentFrame.set(randIdx, currentPage);
                        }
                    }

                    // wait for 100 milisecs
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (referenceCount > 0){
                        referenceRecord(currentProcess.id, currentPage, hitSign, evictedPage);
                        referenceCount--;
                    }

                    // update current page
                    currentPage = currentProcess.locate(currentPage);
                }

                // Each time a process completes, print a record
                // System.out.println(processRecord(currentProcess));
                memory.put(Thread.currentThread(), null);
                currentProcess.frame.clear();
            }
        }
    }

    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        public LRUCache(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return super.size() > capacity;
        }
    }
}
