package com.scu.page;

import com.scu.page.PageReplacement.*;

public class Init {
    public static void main (String[] args) {
        for (int i = 0; i < 10; i++) {
            Process p = new Process();
            LRU lru = new LRU();
            System.out.println(lru);
        }
    }
}
