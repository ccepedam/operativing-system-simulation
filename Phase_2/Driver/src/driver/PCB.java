/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;

import java.util.LinkedList;

/**
 *
 * @author CARLOSANDRES
 */
public class PCB {
    
    Driver dr = new Driver();

    public String[] regArr;
    public String IR; // Instruction Register
    public int PC;
    private boolean inRam;
    
    public boolean PagFault;
    public String TFault;
    public int CurrentPageFault;

    private long timerWaiting=0L; 
    private long timerTotal=0L; 
    private int NumberIO=0;
    private long absStartTime=0L;
    private long absEndTime= 0L;
    
    LinkedList<Integer> JobPages = new LinkedList<>();
    
    
    // default constructor
    public PCB (int i, int o, int t, int jSize, int jPri, int jID, int addrSet, LinkedList<Integer> tempJobPages)
    {
        inputBufferSize = i;
        outputBufferSize = o;
        tempBufferSize = t;
        codeSize = jSize;
        priority = jPri;
        jobID = jID;
        status = "new"; //{new, ready, running, waiting, finished}
        totalSize = codeSize + inputBufferSize + outputBufferSize + tempBufferSize;
        regArr = new String[16];
        java.util.Arrays.fill(regArr,"0000");
        IR = "";
        PC = 0;
        inRam = false;
        PagFault = false;
        TFault="NF";
        CurrentPageFault=0;
        //romAddr = addrSet;
        
        for(int j = 0; j<tempJobPages.size(); j++)
        {
            JobPages.add(tempJobPages.get(j));
        }
        
    }

    private int jobID;
    private int cpuId;// ID assigned to CPU (for multiprocessor system)
    private int codeSize;//extracted from the job control line
    private Integer priority;// of the processes, extracted from the job control line
    private String status;
    private int totalSize; // jobSize + inputbuffer + outputbuffer + tempbuffer

    private int inputBufferSize;
    private int outputBufferSize;
    private int tempBufferSize;

    private int startAddress; // physical address 0-1023
    private int endAddress; // physical address 0-1023

    //private int romAddr;

    private int inputBufferStartAddress;
    private int outputBufferStartAddress;

    private String getIR() {return IR;}
    private void setIR(String i) { IR = i;}


    // ACCESSORS & MUTATORS
    public int getJobPage(int p) {return JobPages.get(p);}  //Return the Page number
    public int getJobPageSize() {return JobPages.size();}
    
    public int getInputBufferSize() { return inputBufferSize;}
    public void setInputBufferSize(int i) { inputBufferSize = i;}

    public int getOutputBufferSize() {return outputBufferSize;}
    public void setOutputBufferSize(int i) { outputBufferSize = i;}

    public int getTempBufferSize() {return tempBufferSize;}
    public void setTempBufferSize(int i) { tempBufferSize = i;}

    public int getPC() {return Integer.parseInt(regArr[0]);}
    public void setPC(int i) { regArr[0] = Integer.toString(i);}

    public int getStartAddress() {return startAddress;}
    public void setStartAddress(int i) { startAddress = i;}

    public int getEndAddress() {return endAddress;}
    public void setEndAddress(int i) { endAddress = i;}

    //public int getRomAddress() {return romAddr;}

    public int getJobID() {return jobID;}
    public void setJobID(int i) { jobID = i;}

    public int getCodeSize() {return codeSize;}
    public void setCodeSize(int i) { codeSize = i;}

    public int getPriority() {return priority;}
    public void setPriority(int i) { priority = i;}

    public String getStatus() {return status;}
    public void setStatus(String i) { status = i;}

    public int getTotalSize() {return totalSize;}
    public void setTotalSize(int i) { totalSize = i;}

    public int getInputBufferStartAddress() {return inputBufferStartAddress;}
    public void setInputBufferStartAddress(int i) { inputBufferStartAddress = i;}

    public int getOutputBufferStartAddress() {return outputBufferStartAddress;}
    public void setOutputBufferStartAddress(int i) { outputBufferStartAddress = i;}

    public boolean isInRam() {return inRam;}
    public void isInRam(boolean i) { inRam = i;}
    
    
    public long getTimeW() {return timerWaiting;}
    public void setTimeW(long t) { timerWaiting = timerWaiting + t;}
    
    public long getTimeT() {return timerTotal;}
    public void setTimeT(long t) { timerTotal = timerTotal + t;}
    
    public int getNIO() {return NumberIO;}
    public void setNIO(int t) { NumberIO = NumberIO + t;}
    
    public long getabsStartTime() {return absStartTime;}
    public void setabsStartTime(long t) { absStartTime = t;}
    
    public long getabsEndTime() {return absEndTime;}
    public void setabsEndTime(long t) { absEndTime = t;}
    
    
    
}
