/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osdriver;

import java.util.*;
import java.util.LinkedList;

/**
 *
 * @author CARLOSANDRES
 */
public class CPU {
 
    
    private int PC;
    private String IR; //Instruction Register
    private int JobIDRuning;
    private static final int CpuID=1;
    private int memoryRangeMin;
    private int memoryRangeMax;
    
    //CACHE ********************************************************************
   
    private ArrayList<String> CACHE = new ArrayList<>();

    
    public int getCacheUsed() {return CACHE.size();}
    
    public String getCacheElement(int address) {return CACHE.get(address);}
    public void addCacheElement(String data) {CACHE.add(data);}
    public void setCacheElement(int i, String data) {CACHE.set(i, data);}
    
    public void resetCACHE() 
    {    
        CACHE.clear();
    }
    //**************************************************************************
    
    
    
    
    public CPU()
    {
        PC=-1;
        IR="";
        JobIDRuning=-1;
        memoryRangeMin=-1;
        memoryRangeMax=-1;
    }
    
    public int  getPC() {return PC;}
    public void setPC(int i) { PC = i;}
    
    public int  getJobIDRuning() {return JobIDRuning;}
    public void setJobIDRuning(int i) { JobIDRuning = i;}
    
    public int  getmemoryRangeMin() {return memoryRangeMin;}
    public void setmemoryRangeMin(int i) { memoryRangeMin = i;}
    
    public int  getmemoryRangeMax() {return memoryRangeMax;}
    public void setmemoryRangeMax(int i) { memoryRangeMax = i;}
    
    public int  getCpuID() {return CpuID;}
       
   
    
    public void runCPU(DISPATCHER DIS1, DMA DMA1, CPU CPU1, MEMORY M1, DECODE DC, FETCH FT, EFFADD EFF, EXECUTE EX, LinkedList<PCB> pcbList, LinkedList<JOBLIST> ReadyQueue, LinkedList<JOBLIST> RuningQueue, LinkedList<JOBLIST> WaitingQueue, LinkedList<JOBLIST> TerminatedQueue, LinkedList<JOBLIST> IOInstQueue)
    {
   
        if(WaitingQueue.size()>0) //if I/O data (must contain # of data to R or W, data if to write and addresses) run DMAChanel
        {
            System.out.println("Into WaitingQueue execution DMA");
            DMA1.runDMA(CPU1, FT, EFF, M1, DC, pcbList, ReadyQueue, WaitingQueue);
            
        }else {
            System.out.println("There are Not Jobs for IO Instructions in WaitingQueue");
        }
        
        if(RuningQueue.size()>0) 
        {
            ComputeOnly(DIS1, CPU1, M1, DC, FT, EFF, EX, pcbList, RuningQueue, WaitingQueue, TerminatedQueue);
        }else {
            System.out.println("There are Not more Jobs in Runing Queue");
        }

    }
    
    private void ComputeOnly(DISPATCHER DIS1, CPU CPU1, MEMORY M1, DECODE DC, FETCH FT, EFFADD EFF, EXECUTE EX, LinkedList<PCB> pcbList, LinkedList<JOBLIST> RuningQueue, LinkedList<JOBLIST> WaitingQueue, LinkedList<JOBLIST> TerminatedQueue)
    {
    
        int newTypeAdd=1; //Type Phisical address

        while(pcbList.get(JobIDRuning-1).getStatus()!="Finished" && pcbList.get(JobIDRuning-1).getStatus()!="Waiting")
        {
            IR=CACHE.get(PC);
            DC.DecodeInstruction(IR);
            System.out.println("PC value " + PC);
            System.out.println("IR value " + IR);                
            EX.runEXECUTE(DIS1, FT, EFF, M1, CPU1, DC, pcbList, RuningQueue, WaitingQueue, TerminatedQueue);
            PC=PC+1;
        }
        CPU1.setJobIDRuning(-1);
        
    }
    
}

