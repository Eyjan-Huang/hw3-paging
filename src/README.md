# Table of Contents

---
- [Maintainers](#maintainers)
- [Table of Contents](#table-of-contents)
- [Architecture](#architecture)
- [Algorithm Introduction](#algorithm-introduction)
---

# Maintainers
- [@Yingjian Huang](https://github.com/Eyjan-Huang)
- [@Zelu Liang](https://github.com/VKLED)


# Architecture
```yaml
- hw3
    - src
        - com.scu.page
          - Init.java
          - PageReplacement.java
          - Process.java
    - .gitignore
    - README.md
```

1. `Init.java` is used to initialize the `Process and Replacement Algorithm`. The entrance of program.
2. `PageReplacement.java` contains the `FIFO, LRU, Optimal and Random Pick algorithm`
3. `Process.java` defines all the information of a process.


# Algorithm Introduction
## 1. FIFO
```python3
1. Assume memory size equals to 4, initially nothing inside:
memory -> [null, null, null, null]

2. Sequence of number will be paged in to the memory, let's asssume from 1 to 4.
memory -> [1, 2, 3, 4]

3. Now, there is a new number 5 comes, we replace the position which comes first. In this case, 1 will be replaced.
memory -> [5, 2, 3, 4]

4. If 6 comes sequencely, we replace 2 in this case, as 2 is the eldest one comes to the memory, so on so forth.
memory -> [5, 6, 3, 4]

5. If 4 comes again, that is hit. But the next phase 3 still be replaced if misses.
```

## 2. LRU
```python3
1. Same as FIFO, intially the memory is empty. Beyond that, we need to maintain another LRU cache (LinkedList + Hashmap).
memory -> [null, null, null, null]     cache -> [null]

2. If 1 comes, we page it in memory and cache.
memory -> [1, null, null, null]     cache -> [1 -> null]

3. Similar process until 4.
memory -> [1, 2, 3, 4]     cache -> [4 -> 3-> 2- > 1 -> null]

4. If here 1 comes again, we hit the memory, but LRU cache will be updated, 1 is recently used so put to head.
memory -> [1, 2, 3, 4]     cache -> [1 -> 4 -> 3-> 2 -> null]

5. If 5 comes, then 2 will be replaced as it is in the tail of the cache.
memory -> [1, 5, 3, 4]    cache -> [5 -> 1 -> 4 -> 3 -> null]
```

## 3. Optimal
```python3
1. For each process, initailly the memory is empty.
memory -> [null, null, null, null]

2. A sequence of references is comming, let's assume 0 to 3
memory -> [0, 1, 2, 3]

3. The next page reference is 4, 4 is not in the memory, we have a miss, now we want to replace a page from the future reference, from the hashmap nextAccess, we get page 2 occurs after 0,1,3, so the page to be replaced is page 2 . The memory layout after replacement is:
memory -> [0, 1, 4, 3]

4. The next page reference is 3, 3 is in the memory, we have a hit.
memory -> [0, 1, 4, 3]

```

## 4. RandomPick
```python3
1. For each process, initailly the memory is empty.
memory -> [null, null, null, null]

2.  A sequence of references is comming, let's assume 0 to 3
memory -> [0, 1, 2, 3]

3. The next page reference is 4, 4 is not in the memory, we have a miss. Randomly select a page to be replaced, like page 1. The memory layout after replacement is:
memory -> [0, 4, 2, 3]

4. The next page reference is 3, 3 is in the memory, we have a hit.
memory -> [0, 1, 4, 3]

```