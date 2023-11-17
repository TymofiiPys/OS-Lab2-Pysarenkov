package org.example;

public class sProcess {
    public int cputime; //	The total amount of run time allowed for this process
    public int ioblocking; // The amount of time in milliseconds to execute before blocking process
    public int cpudone; //	The total amount of time process has executed in milliseconds.
    public int ionext; // The total amount of time process has executed since last unblocking in milliseconds.
    public int quantumnext; // The total amount of time process has executed since being scheduled in milliseconds.
    public int numblocked; // Amount of times the process has been blocked
    public double cpuTimeRatio; // Ratio of time the process has actually executed to the time the process should
    // have executed, meaning, time entitled to the process.

    public sProcess(int cputime, int ioblocking) {
        this.cputime = cputime;
        this.ioblocking = ioblocking;
        this.cpudone = 0;
        this.ionext = 0;
        this.quantumnext = 0;
        this.numblocked = 0;
        this.cpuTimeRatio = 0.;
    }
}
