/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;


import static driver.CPU.dr;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Iterator;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.*;
import java.util.ArrayDeque;

/**
 *
 * @author CARLOSANDRES
 */
public class Driver implements Comparator {

    public static int addressSet = 0;

    public static Driver unit = new Driver();
    public static CPU cpu = new CPU(unit);
    
    public static PageTable pageTable = new PageTable();
    public static FrameTable frameTable = new FrameTable();
    public static MEMORY mem = new MEMORY();

    private static Semaphore locks = new Semaphore(1, true); 
    public static ExecutorService executor;
    
    public static long TimeW = 0;
    public static long TimeT = 0;
    public static long TotalJobs=0;
    public static long STARTTIME = 0L;
    
    private static int user_sched;
    private static String user_filename;
    private static int user_numCpus = 1;
    
   
    public static LinkedList<PCB> dequeNew = new LinkedList<>();
    public static LinkedList<PCB> dequeReady = new LinkedList<>();
    public static LinkedList<PCB> dequeRunning = new LinkedList<>();
    public static LinkedList<PCB> dequeWaiting = new LinkedList<>();
    public static LinkedList<PCB> dequeFinished = new LinkedList<>();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException
    {
        //TODO code application logic here
        TakeInput();
        executor = Executors.newFixedThreadPool(user_numCpus);
        Loader();
        STARTTIME = System.currentTimeMillis();
        LongTermSched();
        while(dequeReady.size()>0 || dequeWaiting.size()>0)
        {
            Dispatcher();
            if(dequeWaiting.size()>0)
            {
                //(new Thread(new ServicingFault(unit))).start();
                ServicingFault SF = new ServicingFault(unit);
                SF.run();
            }
        }
       
        CleanUp(); // cleans up ram and resets all values to zero.
        mem.PrintDisk();
        
    }
    
    private static void TakeInput()
    {
        Scanner input = new Scanner(System.in);

        boolean userInput = false;

        do {
            System.out.println("Please enter 0 for FCFS and 1 for Priority Scheduling: ");
            while (!input.hasNextInt()) input.next();
            user_sched = input.nextInt();
            if (user_sched == 0 || user_sched == 1)
                userInput = true;
        } while (!userInput);

        System.out.println("Please enter the # of CPUs: ");
        while (!input.hasNextInt()) input.next();
        user_numCpus = input.nextInt();
    }
    
    
    public static void Loader()
    {
        int jobID = -1;
        int jobSize = -1;
        int jobPriority = -1;
        LinkedList<Integer> tempJobPages = new LinkedList<>();
        
        try {
            
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\CARLOSANDRES\\Documents\\NetBeansProjects\\Driver\\src\\Program-File.txt"));
            String line;
            while ((line = br.readLine()) != null)
            {
                
                if(pageTable.freePagesThere()>0)
                {
                    Page CurrentPage = pageTable.usePage();
                    
                    if (line.contains("// JOB")) // If JOB, store in disk
                    {
                        // Logic to convert job ID from hexadecimal to decimal.
                        int nextSpace = line.indexOf(' ', 7);
                        String tempID = line.substring(7, nextSpace);
                        jobID = Integer.parseInt(tempID,16);
                        line = line.substring(nextSpace + 1);

                        // Converts and store job size as decimal value
                        nextSpace = line.indexOf(' ');
                        String tempSize = line.substring(0,nextSpace);
                        jobSize = Integer.parseInt(tempSize,16);
                        line = line.substring(nextSpace +1);

                        // Converts and stores job priority as decimal value
                        jobPriority = Integer.parseInt(line,16);

                        //Set Job ID on the current Page
                        CurrentPage.setJobID(jobID);
                        CurrentPage.setisUsed(true);
                        tempJobPages.add(CurrentPage.getpageNumber());
                        CurrentPage.setType("Ins");


                        // Read next line after the control card
                        for (int i = 0; i < jobSize; i++)
                        {
                            // Go to next line
                            line = br.readLine().substring(2);

                            // Store instructions in disk as 8-char HEX values
                            if(!CurrentPage.getisFull())
                            {
                                mem.LWriteToDisk(CurrentPage, line);
                            }else
                            {
                                if(pageTable.freePagesThere()>0)
                                {
                                    CurrentPage = pageTable.usePage();
                                }else
                                {
                                    System.err.println("There is not more space in Disk");
                                    System.exit(0);
                                }
                                CurrentPage.setJobID(jobID);
                                CurrentPage.setisUsed(true);
                                tempJobPages.add(CurrentPage.getpageNumber());
                                CurrentPage.setType("Ins");
                                mem.LWriteToDisk(CurrentPage, line);
                            }
                            CurrentPage.setcounter(CurrentPage.getcounter()+1);
                        }
                    }//End Instructions

                    line = br.readLine();
                    // If DATA control card, do this
                    if (line.contains("// Data"))
                    {
                        // Stores & convert data for Input Buffer
                        int nextSpace = line.indexOf(' ', 8);
                        String tempInputBuffer = line.substring(8,nextSpace);
                        int inputBufferSize = Integer.parseInt(tempInputBuffer, 16); // Converted to HEX
                        line = line.substring(nextSpace + 1);

                        // Stores & convert data for Output Buffer Size
                        nextSpace = line.indexOf(' ');
                        String tempOutputBufferSize = line.substring(0, nextSpace);
                        int outputBufferSize = Integer.parseInt(tempOutputBufferSize, 16);
                        line = line.substring(nextSpace +1);

                        // Stores & convert size of temp buffer
                        int tempBufferSize = Integer.parseInt(line, 16);

                        // Read next line after the control card
                        
                        //Creating PAge Tables for InputBuffer
                        if(pageTable.freePagesThere()>0)
                        {
                            CurrentPage = pageTable.usePage();
                            CurrentPage.setJobID(jobID);
                            CurrentPage.setisUsed(true);
                            tempJobPages.add(CurrentPage.getpageNumber());
                            CurrentPage.setType("InpB");
                        }else
                        {
                            System.err.println("There is not more space in Disk");
                            System.exit(0);
                        }
                        for (int i = 0; i < (inputBufferSize); i++)
                        {
                            // Go to next line
                            line = br.readLine().substring(2);

                            // Store instructions in disk as 8-char HEX values
                            if(!CurrentPage.getisFull())
                            {
                                mem.LWriteToDisk(CurrentPage, line);
                            }else
                            {
                                if(pageTable.freePagesThere()>0)
                                {
                                    CurrentPage = pageTable.usePage();
                                }else
                                {
                                    System.err.println("There is not more space in Disk");
                                    System.exit(0);
                                }
                                CurrentPage.setJobID(jobID);
                                CurrentPage.setisUsed(true);
                                tempJobPages.add(CurrentPage.getpageNumber());
                                CurrentPage.setType("InpB");
                                mem.LWriteToDisk(CurrentPage, line);
                            }
                            CurrentPage.setcounter(CurrentPage.getcounter()+1);
                        }
                        
                        //Creating Page Tables for OutputBuffer
                        if(pageTable.freePagesThere()>0)
                        {
                            CurrentPage = pageTable.usePage();
                            CurrentPage.setJobID(jobID);
                            CurrentPage.setisUsed(true);
                            tempJobPages.add(CurrentPage.getpageNumber());
                            CurrentPage.setType("OutB");
                        }else
                        {
                            System.err.println("There is not more space in Disk");
                            System.exit(0);
                        }
                        for (int i = 0; i < (outputBufferSize); i++)
                        {
                            // Go to next line
                            line = br.readLine().substring(2);

                            // Store instructions in disk as 8-char HEX values
                            if(!CurrentPage.getisFull())
                            {
                                mem.LWriteToDisk(CurrentPage, line);
                            }else
                            {
                                if(pageTable.freePagesThere()>0)
                                {
                                    CurrentPage = pageTable.usePage();
                                }else
                                {
                                    System.err.println("There is not more space in Disk");
                                    System.exit(0);
                                }
                                CurrentPage.setJobID(jobID);
                                CurrentPage.setisUsed(true);
                                tempJobPages.add(CurrentPage.getpageNumber());
                                CurrentPage.setType("OutB");
                                mem.LWriteToDisk(CurrentPage, line);
                            }
                            CurrentPage.setcounter(CurrentPage.getcounter()+1);
                        }
                        
                        //Creating Page Tables for TempBuffer
                        if(pageTable.freePagesThere()>0)
                        {
                            CurrentPage = pageTable.usePage();
                            CurrentPage.setJobID(jobID);
                            CurrentPage.setisUsed(true);
                            tempJobPages.add(CurrentPage.getpageNumber());
                            CurrentPage.setType("TemB");
                        }else
                        {
                            System.err.println("There is not more space in Disk");
                            System.exit(0);
                        }
                        for (int i = 0; i < (tempBufferSize); i++)
                        {
                            // Go to next line
                            line = br.readLine().substring(2);

                            // Store instructions in disk as 8-char HEX values
                            if(!CurrentPage.getisFull())
                            {
                                mem.LWriteToDisk(CurrentPage, line);
                            }else
                            {
                                if(pageTable.freePagesThere()>0)
                                {
                                    CurrentPage = pageTable.usePage();
                                }else
                                {
                                    System.err.println("There is not more space in Disk");
                                    System.exit(0);
                                }
                                CurrentPage.setJobID(jobID);
                                CurrentPage.setisUsed(true);
                                tempJobPages.add(CurrentPage.getpageNumber());
                                CurrentPage.setType("TemB");
                                mem.LWriteToDisk(CurrentPage, line);
                            }
                            CurrentPage.setcounter(CurrentPage.getcounter()+1);
                        }
                        
                        PCB temp = new PCB(inputBufferSize, outputBufferSize, tempBufferSize, jobSize, jobPriority, jobID, addressSet, tempJobPages);
                        dequeNew.addFirst(temp);
                        
                        while (!tempJobPages.isEmpty()) 
                        {
                            tempJobPages.removeFirst();
                        }

                    }//End Data
                    
                }else
                {
                    System.err.println("There is not more space in Disk");
                    System.exit(0);
                }
                line = br.readLine();
            }//End While
            br.close();
            
        }
        catch (IOException ex)
        {
            System.out.println("File Not Found!");
        }
    }

    
    public static void LongTermSched() throws InterruptedException
    {
        int NumberOfJobsFirstLoad=4;
        PCB current;
        if (user_sched == 1)
            dequeNew.sort(unit);
       
        //Initial Load (first four Pages per Job)
        while(dequeNew.size()>0 && frameTable.freeFramesThere()>0)
        {
            current=dequeNew.getFirst();
            for(int i = 0; i<NumberOfJobsFirstLoad; i++)
            {
                Page TempPage = pageTable.getPage(current.getJobPage(i));
                Frame TempFrame = frameTable.useFrame();
                mem.WritePageToFrame(TempPage, TempFrame);
                TempPage.setinRam(true);
                TempPage.setequFrame(TempFrame.getframeNumber());
                TempFrame.setisUsed(true);
                TempFrame.setequPage(TempPage.getpageNumber());
                TempFrame.setJobID(TempPage.getJobID());
            }
            current.setStatus("ready");
            current.isInRam(true);
            dequeReady.addFirst(current);
            dequeNew.removeFirst();
        }
    }
    
    public static void Dispatcher() throws InterruptedException
    {

        if (executor.isTerminated())
            executor = Executors.newFixedThreadPool(4); // must instantiate a new executor if the old one was terminated (or it will throw error)
        while(dequeReady.size()>0)
        {
            PCB job = dequeReady.getFirst();
            job.setStatus("running");
            dequeRunning.addLast(job);
            dequeReady.removeFirst();
            executor.submit(new CPU(unit, job, pageTable, frameTable));
            //CPU tempCPU = new CPU (unit, job, pageTable, frameTable);
            //tempCPU.run();
            if(dequeWaiting.size()>0)
            {
                //(new Thread(new ServicingFault(unit))).start();
                ServicingFault SF = new ServicingFault(unit);
                SF.run();
            }
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void CleanUp() throws InterruptedException
    {
        PCB tempPCB;
        mem.resetRAM();
        for (PCB dequeFinished1 : dequeFinished)
        {
            tempPCB = dequeFinished1;
            System.out.println("Parametes JOB ID, Time Wainting, Time Total, Number of IO's ");
            System.out.println(tempPCB.getJobID());
            System.out.println(tempPCB.getTimeW());
            System.out.println(tempPCB.getTimeT());
            System.out.println(tempPCB.getNIO());
            System.out.println(tempPCB.getabsStartTime()-STARTTIME);
            System.out.println(tempPCB.getabsEndTime()-STARTTIME);
            System.out.println("***********************");
            TimeW=TimeW+tempPCB.getabsStartTime()-STARTTIME;
            TimeT=TimeT+tempPCB.getabsEndTime()-STARTTIME;
            TotalJobs=TotalJobs+1;
            System.out.println("ATTIMEW" + (TimeW/30));
            System.out.println("ATTIMEF" + (TimeT/30));
        }
    }
    
    public static void acquireLock() throws InterruptedException {
        locks.acquire();
    }  
    
    public static void releaseLock() throws InterruptedException {
        locks.release();
    }    
    

    @Override
    public int compare(Object block1, Object block2) {

        int P1 = ((PCB)block1).getPriority();
        int P2 = ((PCB)block2).getPriority();

        if(P1 == P2) {
            return 0;
        } else if (P1 > P2) {
            return 1;
        } else {
            return -1;
        }

    }
}

