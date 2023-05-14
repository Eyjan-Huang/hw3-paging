package com.scu.page;

import com.scu.page.PageReplacement.*;

import java.util.LinkedList;
import java.util.Queue;

public class Init {
    public static void main (String[] args) {
        int numOfProcess = 150;
        Queue<Process> queue = new LinkedList<>();

        for(int i = 0; i < numOfProcess; i++) {
            queue.offer(new Process(i));
        }
        queue.stream().forEach(System.out::println);
    }
}
