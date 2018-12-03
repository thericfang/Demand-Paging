public class FrameTableEntry {
    private int pageNumber;
    private boolean occupied;
    private int[] range;
    private int processNumber;
    private int pageSize;
    private int frameNumber;
    private int cycleLoaded; 
    private Process curProcess;
    public FrameTableEntry (int pageSize, int frameNumber) { // default constructor creates an empty frame table
        occupied = false;
        range = new int[2];
        this.pageSize = pageSize;
        this.frameNumber = frameNumber;
    }

    public boolean equals(int processNumber, int pageNumber) { // Is a hit
        return processNumber == this.processNumber && pageNumber == this.pageNumber;

    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getProcessNumber() {
        return processNumber;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public int getCycleLoaded() {
        return cycleLoaded;
    }

    public Process getProcess() {
        return curProcess;
    }

    public void setEntry(int processNumber, int pageNumber, Process p) {
        occupied = true;
        this.processNumber = processNumber;
        this.pageNumber = pageNumber;
        curProcess = p;
    }

    public void setCycleLoaded(int cycleLoaded) {
        this.cycleLoaded = cycleLoaded;
    }

    @Override
    public String toString() {
        return "Page Number: " + pageNumber + " Empty: " + !occupied;
    }



}