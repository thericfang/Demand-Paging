import java.util.*;
import java.io.*;
public class Main {
    public static ArrayList<Integer> randomNumList = new ArrayList<Integer>(); 
    public static void main (String args[]) throws IOException {
        int machineSize = 0;
        int pageSize = 0; 
        int processSize = 0;
        int jobMix = 0;
        int numOfReferences = 0;
        String replacementAlg = null;

        try {
            machineSize = Integer.parseInt(args[0]);
            pageSize = Integer.parseInt(args[1]);
            processSize = Integer.parseInt(args[2]);
            jobMix = Integer.parseInt(args[3]);
            numOfReferences = Integer.parseInt(args[4]);
            replacementAlg = args[5]; 
        }
        catch (Exception ex) {
            System.out.println(ex);
            System.exit(0);
        }
        randomNumbersReader();
        System.out.println(machineSize);
        System.out.println(pageSize);
        System.out.println(processSize);
        System.out.println(jobMix);
        System.out.println(numOfReferences);
        System.out.println(replacementAlg);
        
        // List<FrameTableEntry> frameTable = new ArrayList<FrameTableEntry>();
        // Frame Table with Frame Table Entries; initially empty
        int numOfPages = machineSize / pageSize;
        FrameTableEntry frameTable = new FrameTableEntry[numOfPages];

        for (int i = 0; i < numOfPages; i++) {
            frameTable.add(new FrameTableEntry(pageSize, i)); // adds frame table entries of size pageSize and pageNum of i
        }
        for (FrameTableEntry f : frameTable) {
            System.out.println(f);
        }
        List<Process> processList = new ArrayList<Process>();
        
        switch (jobMix) {
            case 1: {
                processList.add(new Process(numOfReferences, processSize, pageSize, 1, 1, 0, 0));
                break;
            }
            case 2: {
                processList.add(new Process(numOfReferences, processSize, pageSize, 1, 1, 0, 0));
                processList.add(new Process(numOfReferences, processSize, pageSize, 2, 1, 0, 0));
                processList.add(new Process(numOfReferences, processSize, pageSize, 3, 1, 0, 0));
                processList.add(new Process(numOfReferences, processSize, pageSize, 4, 1, 0, 0));
                break;
            }
            case 3: {
                processList.add(new Process(numOfReferences, processSize, pageSize, 1, 0, 0, 0));
                processList.add(new Process(numOfReferences, processSize, pageSize, 2, 0, 0, 0));
                processList.add(new Process(numOfReferences, processSize, pageSize, 3, 0, 0, 0));
                processList.add(new Process(numOfReferences, processSize, pageSize, 4, 0, 0, 0));
                break;
            }
            case 4: {
                processList.add(new Process(numOfReferences, processSize, pageSize, 1, 0.75, 0.25, 0));
                processList.add(new Process(numOfReferences, processSize, pageSize, 2, 0.75, 0, 0.25));
                processList.add(new Process(numOfReferences, processSize, pageSize, 3, 0.75, 0.125, 0.125));
                processList.add(new Process(numOfReferences, processSize, pageSize, 4, 0.5, 0.125, 0.125));
            }
            default: ;
        }

    
        simulateLRU(frameTable, processList);

        
    }

    public static void simulateLRU (List<FrameTableEntry> frameTable, List<Process> processList) {
        int quantumCounter = 0;
        Integer randomNumberCounter = 0;
        List<Process> terminatedList = new ArrayList<Process>();
        Iterator<Process> it = processList.iterator();
        int referenceAddress = 0;
        int cycle = 1;
        while (processList.size()!=terminatedList.size()) { // Do until processes terminate
            Process p;
            if (!it.hasNext()) { // Iterate through processes. If there is no next process, go back to beginning.
                it = processList.iterator();
                p = it.next();
            }
            else {
                p = it.next();
            }
            while (quantumCounter != 3) { // Round robin with quantum = 3
                if (p.isFirstTranslation()) {
                    // System.out.println("Random Number Used " + randomNumList.get(randomNumberCounter));
                    p.setFirstTranslation();         
                }
                else { 
                    p.setCurrentReference();                    
                }
                System.out.print("Process " + p.getProcessNumber() + " references address " + p.getCurrentReferenceAddress() + " (Page " + p.getCurrentPageNumber() + 
                ") at time " + cycle + ": ");
                if (isPageFault(frameTable, p)) { // If there's a page fault, page fault counter++ and evict a frame for page.
                    p.incrementPageFaultCounter(); 
                    evictAndReplaceLRU(frameTable, p, cycle);
                   
                    // System.out.println("Reference Address " + p.getCurrentReferenceAddress());
                    // System.out.println("Process " + p.getProcessNumber() + " Page number " + p.getCurrentPageNumber());
                }
                else {
                    int processNumber = p.getProcessNumber();
                    int pageNumber = p.getCurrentPageNumber();
                    for (FrameTableEntry walk : frameTable) {
                        if (walk.equals(processNumber, pageNumber)) {
                            System.out.println("Hit at frame " + walk.getFrameNumber());
                            frameTable.add(walk);
                            frameTable.remove(walk.getFrameNumber());
                            break;
                        }
                    }
                }
                // for (FrameTableEntry f : frameTable) {
                //     System.out.println("Frame Number "+ f.getFrameNumber() + ": Process " + f.getProcessNumber() + " Page " + f.getPageNumber());
                // }
                
                randomNumberCounter+= p.setNextTranslation(randomNumList, randomNumberCounter); // add to random number counter
                
                cycle++;
                quantumCounter++;
                if (p.incrementReferences()) { // Will return true if number of references is reached
                    terminatedList.add(p);
                    quantumCounter = 3;
                }
            }
            quantumCounter = 0;
        }
        printOutput(terminatedList);
    }

    public static void randomNumbersReader() {
        try {
            Scanner randomNumReader = new Scanner(new FileInputStream("random-numbers"));
            while (randomNumReader.hasNextInt()) {
                randomNumList.add(randomNumReader.nextInt());
            }
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public static boolean isPageFault (List<FrameTableEntry> frameTable, Process p) { // References the frame table. Returns true if there's a fault, false if there's a hit.
        // Convert referenceAddress to a page number
        // Things needed: process number, page number (range). 
        int processNumber = p.getProcessNumber();
        int pageNumber = p.getCurrentPageNumber();
        for (FrameTableEntry walk : frameTable) {
            if (walk.equals(processNumber, pageNumber)) {
                return false;
            }
        }
        return true;   

    }

    public static void evictAndReplaceLRU (List<FrameTableEntry> frameTable, Process p, int cycle) {
        FrameTableEntry temp = frameTable.get(0); // Maintain the frame numbers but add p process number and page number
        frameTable.remove(0);
        if (!temp.isOccupied()) { // if the entry is initially empty
            System.out.println("Page Fault. Using free frame " + temp.getFrameNumber() + " for initial loading");
            temp.setCycleLoaded(cycle); // add cycle loaded to FTE
            temp.setEntry(p.getProcessNumber(), p.getCurrentPageNumber(), p);
        }
        else { // Evicting existing entry
            System.out.println("Page Fault. Evicting page " + temp.getPageNumber() + " of process " + temp.getProcessNumber() + " from frame " + temp.getFrameNumber());
            int residency = cycle - temp.getCycleLoaded(); // Residency of evicted frame
            // Add residency to process
            temp.getProcess().incrementEvictions(); // Increment evictions to calculate average residency
            temp.getProcess().addResidencyTime(residency); 
            temp.setEntry(p.getProcessNumber(), p.getCurrentPageNumber(), p);
            temp.setCycleLoaded(cycle);
        }

        frameTable.add(temp); // the most recently edited entry was added to the end of the list. 
    }

    public static void printOutput(List<Process> terminatedList) {
        double overallAverageResidency = 0;
        double totalEvictions = 0;
        double totalResidency = 0;
        int totalFaults = 0;
        for (Process p : terminatedList) {
            if (p.getEvictions() == 0) { // If evictions is 0, there is no residency.
                System.out.println("\nProcess " + p.getProcessNumber() + " had " + p.getPageFaultCounter() + " faults.");
                System.out.println("\twith no evictions, the average residency is undefined");
                totalResidency += p.getResidency();
            }
            else {
                double averageResidency = p.getResidency() / (double)p.getEvictions();
                totalEvictions += p.getEvictions();
                totalResidency += p.getResidency();
                System.out.println("\nProcess " + p.getProcessNumber() + " had " + p.getPageFaultCounter() + " faults, "
                    + p.getEvictions() + " evictions, " + p.getResidency() + " cycles in residency, and " 
                    + averageResidency + " average residency");
                overallAverageResidency += averageResidency;
             

            } 
            totalFaults += p.getPageFaultCounter();
            
        }
        if (totalEvictions == 0) {
            System.out.println("\nThe total number of faults is " + totalFaults + ".");
            System.out.println("\twith no evictions, the overall average residency is undefined");
 
        }
        else {
            overallAverageResidency = totalResidency / (double)totalEvictions;
            System.out.println("\nThe total number of faults is " + totalFaults + " and the overall average residency is " + overallAverageResidency);
        }
       
        
    }



}
