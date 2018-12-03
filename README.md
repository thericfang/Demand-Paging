# Demand-Paging

## Introduction
This program is a simulation of demand paging with a frame table and a page table. 

## Use Instructions
Compile the Main Java file with `javac Main.java`.
Run with `java Main [args]` 
The args are given on NYU Classes, it should be in format m, p, s, j, n, r, where m,p,s,j,n are all ints, and r is the algorithm given as a string
For example, input-01 given on classes can be run as:
`java Main 10 10 20 1 10 lru`

Please make sure that the random-numbers file is in the same folder as the program. If it is not, an exception will be thrown.

## Output
The program will output into the console, as a summary of process page faults, average residency, total residency, and total number of page faults.