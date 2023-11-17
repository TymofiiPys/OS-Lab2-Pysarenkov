package org.example;// This file contains the main() function for the Scheduling
// simulation.  Init() initializes most of the variables by
// reading from a provided file.  SchedulingAlgorithm.Run() is
// called from main() to run the simulation.  Summary-Results
// is where the summary results are written, and Summary-Processes
// is where the process scheduling summary is written.

// Created by Alexander Reeder, 2001 January 06

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

public class Scheduling {

    private static int processnum = 5;
    private static int runtimeAverage = 1000;
    private static int runtimeStandardDev = 100;
    private static int blocktimeAverage = 200;
    private static int blocktimeStandardDev = 10;
    private static int quantum = 50;
    private static int runtime = 1000;
    private static Vector<sProcess> processVector = new Vector<>();
    private static Results result = new Results("null", "null", 0);
    private static String resultsFile = "Summary-Results";
    private static String proccessFile = "Summary-Processes";

    private static void Init(String file) {
        File f = new File(file);
        String line;
        int cputime = 0;
        int ioblocking = 0;
        double X = 0.0;
        double Y = 0.0;

        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
//            DataInputStream in = new DataInputStream(new FileInputStream(f));
            while ((line = in.readLine()) != null) {
                if (line.startsWith("numprocess")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    processnum = Common.s2i(st.nextToken());
                }
                if (line.startsWith("run_time_average")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    runtimeAverage = Common.s2i(st.nextToken());
                }
                if (line.startsWith("run_time_stddev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    runtimeStandardDev = Common.s2i(st.nextToken());
                }
                if (line.startsWith("block_time_average")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    blocktimeAverage = Common.s2i(st.nextToken());
                }
                if (line.startsWith("block_time_stddev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    blocktimeStandardDev = Common.s2i(st.nextToken());
                }
                if (line.startsWith("summary_file")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    resultsFile = st.nextToken();
                }
                if (line.startsWith("log_file")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    proccessFile = st.nextToken();
                }
                if (line.startsWith("runtime")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    runtime = Common.s2i(st.nextToken());
                }
            }
            in.close();
            for (int i = 0; i < processnum; i++) {
                X = Common.R1();
                while (X == -1.0) {
                    X = Common.R1();
                }
                X = X * runtimeStandardDev;
                cputime = (int) X + runtimeAverage;
                if(cputime < 0){
                    cputime = 500;
                }
                Y = Common.R1();
                while (Y == -1.0) {
                    Y = Common.R1();
                }
                Y = Y * blocktimeStandardDev;
                ioblocking = (int) Y + blocktimeAverage;
                if(ioblocking < 0){
                    ioblocking = 100;
                }
                processVector.addElement(new sProcess(cputime, ioblocking));
            }
        } catch (IOException e) { /* Handle exceptions */ }
    }

    private static void debug() {
        int i = 0;

        System.out.println("processnum " + processnum);
        System.out.println("meandevm " + runtimeAverage);
        System.out.println("standdev " + runtimeStandardDev);
        int size = processVector.size();
        for (i = 0; i < size; i++) {
            sProcess process = processVector.elementAt(i);
            System.out.println("process " + i + " " + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.numblocked);
        }
        System.out.println("runtime " + runtime);
    }

    public static void main(String[] args) {
        int i = 0;

        if (args.length != 1) {
            System.out.println("Usage: 'java Scheduling <INIT FILE>'");
            System.exit(-1);
        }
        File f = new File(args[0]);
        if (!(f.exists())) {
            System.out.println("Scheduling: error, file '" + f.getName() + "' does not exist.");
            System.exit(-1);
        }
        if (!(f.canRead())) {
            System.out.println("Scheduling: error, read of " + f.getName() + " failed.");
            System.exit(-1);
        }
        System.out.println("Working...");
        Init(args[0]);
        result = SchedulingAlgorithm.run(runtime, processVector, quantum, result, proccessFile);
        try {
            //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            out.println("Scheduling Type: " + result.schedulingType);
            out.println("Scheduling Name: " + result.schedulingName);
            out.println("Simulation Run Time: " + result.compuTime);
            out.println("Runtime Mean: " + runtimeAverage);
            out.println("Runtime Standard Deviation: " + runtimeStandardDev);
            out.println("Blocktime Mean: " + blocktimeAverage);
            out.println("Blocktime Standard Deviation: " + blocktimeStandardDev);
            out.println("Quantum: " + quantum);
            out.println("Time entitled for each process: " + (runtime / processVector.size()));
            out.println("Process #\tCPU Time\tIO Blocking\tCPU Completed\tCPU Blocked");
            for (i = 0; i < processVector.size(); i++) {
                sProcess process = processVector.elementAt(i);
                out.print(i);
                if (i < 100) {
                    out.print("\t\t");
                } else {
                    out.print("\t");
                }
                out.print(process.cputime);
                if (process.cputime < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.print(process.ioblocking);
                if (process.ioblocking < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.print(process.cpudone);
                if (process.cpudone < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.println(process.numblocked + " times");
            }
            out.close();
        } catch (IOException e) { /* Handle exceptions */ }
        System.out.println("Completed.");
    }
}

