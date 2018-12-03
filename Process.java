import java.util.*;
public class Process {
    private int numOfReferences;
    private int curReferences;
    private int processSize;
    private int pageSize;
    private int processNumber;
    private double a; 
    private double b;
    private double c;
    private boolean firstTranslation;
    private int currentReferenceAddress;
    private int pageFaultCounter;
    private int currentPageNumber;
    private int residencyTime;
    private int evictions;
    private int nextReferenceAddress;
    public Process (int n, int processSize, int pageSize, int processNumber, double a, double b, double c) {
        numOfReferences = n;
        this.processSize = processSize;
        this.processNumber = processNumber;
        this.a = a;
        this.b = b;
        this.c = c;
        this.pageSize = pageSize;
    }

    public boolean incrementReferences () { // returns true if curReferences == numOfReferences, so terminate
        curReferences++;
        return curReferences == numOfReferences;
    }

    public boolean isFirstTranslation() {
        return !firstTranslation;
    }

    public int getProcessNumber() {
        return processNumber;
    }

    public int getProcessSize() {
        return processSize;
    }

    public int getCurrentReferenceAddress() {
        return currentReferenceAddress;
    }
    
    public int getPageSize() {
        return pageSize;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber() {
        currentPageNumber = currentReferenceAddress / pageSize;
    }

    public void setFirstTranslation() {
        firstTranslation = true;
        currentReferenceAddress = (111 * processNumber) % processSize;
        setCurrentPageNumber();
    }

    public int setNextTranslation(ArrayList<Integer> randomNumList, int randomNumberCounter) { // returns how much to increment randomnumbercounter by
        int counter = 0;
        int r = randomNumList.get(randomNumberCounter);
        // System.out.println("Random number used: " + r);
        // System.out.println(randomNumberCounter);
        double y = r / (Integer.MAX_VALUE + 1d);
        System.out.println(r +" ratio: " + y);
        counter++;
        if (y < a) {
            nextReferenceAddress = (currentReferenceAddress + 1) % processSize; 
        }
        else if (y < a + b) {
            nextReferenceAddress = (currentReferenceAddress - 5 + processSize) % processSize;
        }
        else if (y < a + b + c) {
            nextReferenceAddress = (currentReferenceAddress + 4) % processSize;
        }
        else {
            r = randomNumList.get(counter + randomNumberCounter);
            counter++;
            System.out.println(r);
            nextReferenceAddress = r % processSize;
        }
        return counter;

        // System.out.println("Next Reference Address: " + nextReferenceAddress);
        
    }

    public void setCurrentReference() {
        currentReferenceAddress = nextReferenceAddress;
        setCurrentPageNumber();
    }

    public void incrementPageFaultCounter() {
        pageFaultCounter++;
    }

    public void addResidencyTime(int cycle) {
        residencyTime += cycle;
    }
    
    public int getResidency() {
        return residencyTime;
    }

    public int getPageFaultCounter() {
        return pageFaultCounter;
    }

    public void incrementEvictions() {
        evictions++;
    }

    public int getEvictions() {
        return evictions;
    }

    

    
}