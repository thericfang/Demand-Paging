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
        System.out.println("The machine size is " + machineSize + ".");
        System.out.println("The page size is " + pageSize + ".");
        System.out.println("The process size is " + processSize + ".");
        System.out.println("The job mix number is " + jobMix + ".");
        System.out.println("The number of references per process is " + numOfReferences + ".");
        System.out.println("The replacement algorithm is " + replacementAlg + ".");
        
        int numOfPages = machineSize / pageSize;
        FrameTableEntry[] frameTable = new FrameTableEntry[numOfPages];
       
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

    
        simulate(frameTable, processList, replacementAlg);

        
    }

    public static void simulate(FrameTableEntry[] frameTable, List<Process> processList, String replacementAlg) {
        int quantumCounter = 0;
        List<Process> terminatedList = new ArrayList<Process>();
        Iterator<Process> it = processList.iterator();
        Queue<FrameTableEntry> LRU = new LinkedList<FrameTableEntry>();
        Stack<FrameTableEntry> LIFO = new Stack<FrameTableEntry>();
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
                // System.out.print("Process " + p.getProcessNumber() + " references address " + p.getCurrentReferenceAddress() + " (Page " + p.getCurrentPageNumber() + 
                // ") at time " + cycle + ": ");

                boolean hit = resolveAddress(frameTable, p, LRU); // Resolve Address and return true if there is a hit
                if (!hit) { // If there was not a hit, aka if there was a page fault, evict
                    evictAndReplace(frameTable, p, cycle, replacementAlg, LRU, randomNumList, LIFO);
                }
                
                
                p.setNextTranslation(randomNumList); // add to random number counter
                
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

    public static boolean resolveAddress(FrameTableEntry[] frameTable, Process p, Queue<FrameTableEntry> LRU) {
        // Convert referenceAddress to a page number
        int processNumber = p.getProcessNumber();
        int pageNumber = p.getCurrentPageNumber();
        for (int i = 0; i < frameTable.length; i++) {
            if (frameTable[i] != null && frameTable[i].equals(processNumber, pageNumber)) {
                // System.out.println("Hit at frame " + i);
                LRU.remove(frameTable[i]);
                LRU.add(frameTable[i]);
                return true;
            }
        }
        return false;
        

    }

    public static void evictAndReplace(FrameTableEntry[] frameTable, Process p, int cycle, String replacementAlg, Queue<FrameTableEntry> LRU, ArrayList<Integer> randomNumList, Stack<FrameTableEntry> LIFO) {
        p.incrementPageFaultCounter(); // Increment page fault counter
        boolean processed = false;
        for (int i = frameTable.length-1; i >= 0; i--) {
            if (frameTable[i] == null) { // If frames are free
                // System.out.println("Page Fault. Using free frame " + i + " for initial loading");
                FrameTableEntry temp = new FrameTableEntry(p.getPageSize(), i);
                temp.setEntry(p.getProcessNumber(), p.getCurrentPageNumber(), p);
                temp.setCycleLoaded(cycle);
                frameTable[i] = temp;
                LRU.add(temp);
                LIFO.add(temp);
                processed = true;
                break;
            }
        }
        if (!processed) {
            // Evict a frame depending on algorithm
            if (replacementAlg.equalsIgnoreCase("lru")) { // Determine which frame is the least recently used
                FrameTableEntry temp = LRU.poll();
                // System.out.println("Page Fault. Evicting page " + temp.getPageNumber() + " of process " + temp.getProcessNumber() + " from frame " + temp.getFrameNumber());
                int residency = cycle - temp.getCycleLoaded();
                    // Add residency to process
                temp.getProcess().incrementEvictions(); // Increment evictions to calculate average residency
                temp.getProcess().addResidencyTime(residency); 
                temp.setEntry(p.getProcessNumber(), p.getCurrentPageNumber(), p);
                temp.setCycleLoaded(cycle);
                LRU.add(temp);
            }
            else if (replacementAlg.equalsIgnoreCase("random")) {
                int r = randomNumList.get(0);
                randomNumList.remove(0);
                int i = r % frameTable.length;
                // double y = r / (Integer.MAX_VALUE + 1d);
                // System.out.println(r + "ratio:" + y);
                // int i = (int)(y * frameTable.length - 1);
                
                FrameTableEntry temp = frameTable[i];
                // System.out.println("Page Fault. Evicting page " + temp.getPageNumber() + " of process " + temp.getProcessNumber() + " from frame " + temp.getFrameNumber());
                int residency = cycle - temp.getCycleLoaded();
                    // Add residency to process
                temp.getProcess().incrementEvictions(); // Increment evictions to calculate average residency
                temp.getProcess().addResidencyTime(residency); 
                temp.setEntry(p.getProcessNumber(), p.getCurrentPageNumber(), p);
                temp.setCycleLoaded(cycle);
            }
            else if (replacementAlg.equalsIgnoreCase("lifo")) {
                FrameTableEntry temp = LIFO.peek();
                // System.out.println("Page Fault. Evicting page " + temp.getPageNumber() + " of process " + temp.getProcessNumber() + " from frame " + temp.getFrameNumber());
                int residency = cycle - temp.getCycleLoaded();
                    // Add residency to process
                temp.getProcess().incrementEvictions(); // Increment evictions to calculate average residency
                temp.getProcess().addResidencyTime(residency); 
                temp.setEntry(p.getProcessNumber(), p.getCurrentPageNumber(), p);
                temp.setCycleLoaded(cycle);
                LIFO.add(temp);
            }

        }
    
        
        
        
    }


    public static void printOutput(List<Process> terminatedList) {
        double overallAverageResidency = 0;
        double totalEvictions = 0;
        double totalResidency = 0;
        int totalFaults = 0;
        for (Process p : terminatedList) {
            if (p.getEvictions() == 0) { // If evictions is 0, there is no residency.
                System.out.println("Process " + p.getProcessNumber() + " had " + p.getPageFaultCounter() + " faults.");
                System.out.println("\tWith no evictions, the average residency is undefined.");
                totalResidency += p.getResidency();
            }
            else {
                double averageResidency = p.getResidency() / (double)p.getEvictions();
                totalEvictions += p.getEvictions();
                totalResidency += p.getResidency();
                System.out.println("Process " + p.getProcessNumber() + " had " + p.getPageFaultCounter() + " faults and " 
                    + averageResidency + " average residency.");
                overallAverageResidency += averageResidency;
             

            } 
            totalFaults += p.getPageFaultCounter();
            
        }
        if (totalEvictions == 0) {
            System.out.println("\nThe total number of faults is " + totalFaults + ".");
            System.out.println("\tWith no evictions, the overall average residency is undefined.");
 
        }
        else {
            overallAverageResidency = totalResidency / (double)totalEvictions;
            System.out.println("\nThe total number of faults is " + totalFaults + " and the overall average residency is " + overallAverageResidency + ".");
        }
       
        
    }



}
