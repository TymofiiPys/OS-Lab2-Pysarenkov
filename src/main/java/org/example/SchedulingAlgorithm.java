package org.example;// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;

public class SchedulingAlgorithm {

    public static Results run(int runtime, Vector<sProcess> processVector, int quantum, Results result, String resultsFile) {
        int i = 0;
        int comptime = 0; //Time since the beginning of simulation execution
        int currentProcess = 0;
        int previousProcess = 0;
        int size = processVector.size(); //total amount of processes
        int completed = 0;//amount of processes completed
        double cpuTimeEntitled = 0; //time of execution which is entitled to each process

        result.schedulingType = "Interactive";
        result.schedulingName = "Guaranteed Scheduling";
        try {
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            sProcess process = processVector.elementAt(currentProcess);
            out.println("Process: " + currentProcess + " registered... (" + comptime
                    + " " + process.cputime + " " + process.ioblocking + " "
                    + process.cpudone + " " + process.cpuTimeRatio + ")");

            while (comptime < runtime) {
                if (process.cpudone == process.cputime) {
                    completed++;

                    out.println("Process: " + currentProcess + " completed... (" + comptime
                            + " " + process.cputime + " " + process.ioblocking + " "
                            + process.cpudone + " " + process.cpuTimeRatio + ")");

                    if (completed == size) {
                        result.compuTime = comptime;
                        out.close();
                        return result;
                    }

                    double minRatio = Double.MAX_VALUE;
                    for (i = size - 1; i >= 0; i--) {
                        process = processVector.elementAt(i);
                        process.cpuTimeRatio = (double) Math.round(100 * process.cpudone / cpuTimeEntitled) / 100;
                        if (process.cpudone < process.cputime && previousProcess != i
                                && process.cpuTimeRatio < minRatio) {
                            currentProcess = i;
                            minRatio = process.cpuTimeRatio;
                        }
                    }
                    process = processVector.elementAt(currentProcess);

                    out.println("Process: " + currentProcess + " registered... (" + comptime
                            + " " + process.cputime + " " + process.ioblocking + " "
                            + process.cpudone + " " + process.cpuTimeRatio + ")");
                }

                if (process.ionext == process.ioblocking) {
                    out.println("Process: " + currentProcess + " I/O blocked... (" + comptime
                            + " " + process.cputime + " " + process.ioblocking + " "
                            + process.cpudone + " " + process.cpuTimeRatio + ")");

                    process.numblocked++;
                    process.ionext = 0;
                    process.quantumnext = 0;

                    previousProcess = currentProcess;
                    double minRatio = Double.MAX_VALUE;
                    for (i = size - 1; i >= 0; i--) {
                        process = processVector.elementAt(i);
                        process.cpuTimeRatio = (double) Math.round(100 * process.cpudone / cpuTimeEntitled) / 100;
                        if (process.cpudone < process.cputime && previousProcess != i
                                && process.cpuTimeRatio < minRatio) {
                            currentProcess = i;
                            minRatio = process.cpuTimeRatio;
                        }
                    }
                    process = processVector.elementAt(currentProcess);

                    out.println("Process: " + currentProcess + " registered... (" + comptime
                            + " " + process.cputime + " " + process.ioblocking + " "
                            + process.cpudone + " " + process.cpuTimeRatio + ")");
                }
                if (process.quantumnext == quantum && completed != size - 1) {
                    process.cpuTimeRatio = (double) Math.round(100 * process.cpudone / cpuTimeEntitled) / 100;
                    out.println("Process: " + currentProcess + " quantum elapsed... (" + comptime
                            + " " + process.cputime + " " + process.ioblocking + " "
                            + process.cpudone + " " + process.cpuTimeRatio + ")");

                    process.quantumnext = 0;

                    previousProcess = currentProcess;
                    double minRatio = Double.MAX_VALUE;
                    for (i = size - 1; i >= 0; i--) {
                        process = processVector.elementAt(i);
                        process.cpuTimeRatio = (double) Math.round(100 * process.cpudone / cpuTimeEntitled) / 100;
                        if (process.cpudone < process.cputime && previousProcess != i
                                && process.cpuTimeRatio < minRatio) {
                            currentProcess = i;
                            minRatio = process.cpuTimeRatio;
                        }
                    }
                    process = processVector.elementAt(currentProcess);

                    out.println("Process: " + currentProcess + " registered... (" + comptime
                            + " " + process.cputime + " " + process.ioblocking + " "
                            + process.cpudone + " " + process.cpuTimeRatio + ")");
                }
                process.cpudone++;
                if (process.ioblocking > 0) {
                    process.ionext++;
                }
                if (quantum > 0) {
                    process.quantumnext++;
                }
                comptime++;
                cpuTimeEntitled = (double) comptime / size;
            }
            out.close();
        } catch (IOException e) { /* Handle exceptions */ }
        result.compuTime = comptime;
        return result;
    }
}
