/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osdriver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author CARLOSANDRES
 */
public class OSDRIVER {

    /**
     * @param args the command line arguments
     */
    
       
    public static void main(String[] args) {
                
        //Initializing instances________________________________________________
        
        
        //PCB List
        LinkedList<PCB> pcbList = new LinkedList<PCB>();
        
        //Queues
        LinkedList<JOBLIST> jobListDisk = new LinkedList<JOBLIST>();
        LinkedList<JOBLIST> ReadyQueue = new LinkedList<JOBLIST>();
        LinkedList<JOBLIST> RuningQueue = new LinkedList<JOBLIST>();
        LinkedList<JOBLIST> WaitingQueue = new LinkedList<JOBLIST>();
        LinkedList<JOBLIST> TerminatedQueue = new LinkedList<JOBLIST>();
        LinkedList<JOBLIST> IOInstQueue = new LinkedList<JOBLIST>();
        
        //Instantiating objects Classes
        MEMORY M1 = new MEMORY();
        LOADER L1 = new LOADER();
        SCHEDULER S1 = new SCHEDULER();
        DISPATCHER DIS1 = new DISPATCHER();
        CPU CPU1 = new CPU();
        EXECUTE EX = new EXECUTE();
        DECODE DC = new DECODE();
        FETCH FT = new FETCH();
        EFFADD EFF = new EFFADD();
        DMA DMA1 = new DMA();
        
        //File to read
        //The list of Jobs must have the JobID starting in 1 and increasing along the all list.
        String addressFile = "C:\\Users\\CARLOSANDRES\\Documents\\NetBeansProjects\\OSDRIVER\\src\\Program-File.txt";
        
                
        //Using LOADER
        L1.runLOADER(M1,addressFile, pcbList, jobListDisk);
        
                
        while(jobListDisk.size()>0)
        {
            //Using SCHEDULER
            S1.setalgoritmType("priority");
            S1.setRAMSize(M1.getRamCapacity());
            S1.sortJobList(jobListDisk);
            S1.buildReadyQueue(jobListDisk, ReadyQueue, M1);
            S1.loadJobsToRAM(ReadyQueue, M1, pcbList);

            //Using Dispatcher and CPU
            while(ReadyQueue.size()>0 || WaitingQueue.size()>0)
            {
                DIS1.assignProcessToCPU(M1, CPU1, pcbList, ReadyQueue, RuningQueue, WaitingQueue);
                CPU1.runCPU(DIS1, DMA1, CPU1, M1, DC, FT, EFF, EX, pcbList, ReadyQueue, RuningQueue, WaitingQueue, TerminatedQueue, IOInstQueue);
                
            }

            
            while(!TerminatedQueue.isEmpty())
            {
                int jobid = TerminatedQueue.getFirst().getjobID();
                int addinBuffRAM=(pcbList.get(jobid-1).getstartAddressRAM())+pcbList.get(jobid-1).getcodeSize();
                int addinBuffDisk=(pcbList.get(jobid-1).getstartAddressDISK())+pcbList.get(jobid-1).getcodeSize();
                int size = pcbList.get(jobid-1).getendAddressRAM()-addinBuffRAM;
                for(int j=0; j<=size; j++)
                {
                    M1.wMEMORY(0, (addinBuffDisk+j), M1.rMemory(1, (addinBuffRAM+j)));
                }
                TerminatedQueue.removeFirst();
            }
            
            M1.resetRAM();

            WaitforInterrupt();
            
        }
        
 
        
        System.out.println("DISK AFTER ACTUALIZATION ***********************************************");
        for (String disk : M1.getDisk()) {
            System.out.println(disk);
        }
        System.out.println("\n" + "\n" + "\n");
        
        
        System.out.println("jobListDisk");
        for (JOBLIST jobListDisk1 : jobListDisk) {
            System.out.println(jobListDisk1.printJOBLIST());
        }
        System.out.println("\n" + "\n" + "\n");
        
        System.out.println("ReadyQueue");
        for (JOBLIST ReadyQueue1 : ReadyQueue) {
            System.out.println(ReadyQueue1.printJOBLIST());
        }
        System.out.println("\n" + "\n" + "\n");
        
        System.out.println("RuningQueue");
        for (JOBLIST RuningQueue1 : RuningQueue) {
            System.out.println(RuningQueue1.printJOBLIST());
        }
        System.out.println("\n" + "\n" + "\n");
        
        System.out.println("Terminated");
        for (JOBLIST TerminatedQueue1 : TerminatedQueue) {
            System.out.println(TerminatedQueue1.printJOBLIST());
        }
        System.out.println("\n" + "\n" + "\n");
    }
    
    public static void WaitforInterrupt()
    {
        //Code for WaitforInterrupt();
    }
       
}
