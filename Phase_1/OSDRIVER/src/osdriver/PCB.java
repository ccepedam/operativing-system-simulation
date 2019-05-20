/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osdriver;

import java.util.ArrayList;

/**
 *
 * @author CARLOSANDRES
 */
public class PCB {
    
    //DATA from LOADER
    private int jobID;              //Job ID
    private int codeSize;           //Number of instructions extracted from the job control line
    private Integer priority;       //Priority of the processes, extracted from the job control line
    private int inputBufferSize;
    private int outputBufferSize;
    private int tempBufferSize;
    private int startAddressDISK;   // Start physical address in Disk 0-2048
    private int endAddressDISK;     // End physical address in Disk 0-2048
    
    
    //DATA from Scheduler
    private int startAddressRAM;            // Start physical address in RAM 0-1024
    private int endAddressRAM;              // End physical address in RAM 0-1024
    private int startAddressInputBufferRAM; // Start physical address of Input Buffer
    private int startAddressOutputBufferRAM;// Start physical address of Input Buffer
    
    
    //Multiprocessor  
    private int cpuId;                      // ID assigned to CPU (for multiprocessor system)
    
    //Others
    private int pcPCB;
    private int programCounter;             // the job's pc holds the address of the instruction to fetch
    private String status;
    private int totalSize;                  // jobSize + inputbuffer + outputbuffer + tempbuffer
    private String[] regArr = new String [16]; //**************The register were designed as String of binary data**************

    
    //DATA CHANGED IN CACHE********************************************************************
   
    private ArrayList<Integer> DATACHANGED = new ArrayList<>();
    private int maxNumberDataChanged;
    
    public int getmaxNumberDataChanged() {return maxNumberDataChanged;}
    
    public int getSizeDataChanged() {return DATACHANGED.size();}
    
    public int getDataChangedElement(int address) {return DATACHANGED.get(address);}
        
    public void addDataChangedElement(int data) 
    {
        DATACHANGED.add(data);
        maxDATACHANGED();
    }
    
    public void resetDATACHANGED() 
    {    
        DATACHANGED.clear();
    }
    
    private void maxDATACHANGED() 
    {    
        if(maxNumberDataChanged<DATACHANGED.size())
        {
            maxNumberDataChanged=DATACHANGED.size();
        }
    }
    //**************************************************************************
    
        
    // default constructor
    
    public PCB ()
    {
        jobID=-1;
        codeSize=0;
        priority=-1;
        inputBufferSize=0;
        outputBufferSize=0;
        tempBufferSize=0;
        startAddressDISK=-1;
        endAddressDISK=-1;

        startAddressRAM=-1;
        endAddressRAM=-1;
        startAddressInputBufferRAM=-1;
        startAddressOutputBufferRAM=-1;

        cpuId=0;

        pcPCB=startAddressRAM;
        programCounter=0;     // the job's pc holds the address of the instruction to fetch
        status=null;
        totalSize = codeSize + inputBufferSize + outputBufferSize + tempBufferSize;
        maxNumberDataChanged=0;
        java.util.Arrays.fill(regArr,"0000");
    }
    
    // ACCESSORS & MUTATORS
    public int getjobID() { return jobID;}
    public void setjobID(int id) { jobID = id;}
    
    public int getcodeSize() { return codeSize;}
    public void setcodeSize(int cs) { codeSize = cs;}
    
    public Integer getpriority() { return priority;}
    public void setpriority(Integer pr) { priority = pr;}
    
    public int getInputBufferSize() { return inputBufferSize;}
    public void setInputBufferSize(int i) { inputBufferSize = i;}

    public int getOutputBufferSize() {return outputBufferSize;}
    public void setOutputBufferSize(int i) { outputBufferSize = i;}

    public int getTempBufferSize() {return tempBufferSize;}
    public void setTempBufferSize(int i) { tempBufferSize = i;}
    
    public int getstartAddressDISK() {return startAddressDISK;}
    public void setstartAddressDISK(int i) { startAddressDISK = i;}
    
    public int getendAddressDISK() {return endAddressDISK;}
    public void setendAddressDISK(int i) { endAddressDISK = i;}
    
    
    public int getstartAddressRAM() {return startAddressRAM;}
    public void setstartAddressRAM(int i) { startAddressRAM = i;}
    
    public int getendAddressRAM() {return endAddressRAM;}
    public void setendAddressRAM(int i) { endAddressRAM = i;}
    
    public int getstartAddressInputBufferRAM() {return startAddressInputBufferRAM;}
    public void setstartAddressInputBufferRAM(int i) { startAddressInputBufferRAM = i;}
    
    public int getstartAddressOutputBufferRAM() {return startAddressOutputBufferRAM;}
    public void setstartAddressOutputBufferRAM(int i) { startAddressOutputBufferRAM = i;}
    
    
    public String getStatus() {return status;}
    public void setStatus(String i) { status = i;}
    
    public int getTotalSize() {return totalSize;}
    public void setTotalSize(int i) { totalSize = i;}
    
    public int getcpuId() {return cpuId;}
    public void setcpuId(int i) { cpuId = i;}
    
    public int getpcPCB() {pcPCB=startAddressRAM; return pcPCB;}
    public void setpcPCB(int i) 
    { 
        this.setstartAddressRAM(i);
        pcPCB=startAddressRAM;
    }
        
    public void setRegister(int numberReg, String DataReg)
    {
        regArr[numberReg]=DataReg;
    }
    public String getRegister(int numberReg)
    {
        String TempReg=null;
        TempReg=regArr[numberReg];
        return TempReg;
    }
    
    
    
    public int getprogramCounter() {return programCounter;}
    public void setprogramCounter(int i) { programCounter = i;}
    
    
    
    public void loaderPCB (int jID, int jSize, int jPri, int i, int o, int t, int sAD, int eAD)
    {
        
        jobID=jID;
        codeSize = jSize;
        priority = jPri;
        inputBufferSize = i;
        outputBufferSize = o;
        tempBufferSize = t;
        startAddressDISK=sAD;
        endAddressDISK=eAD;
        
        cpuId=1;
        status = "new";
        totalSize = codeSize + inputBufferSize + outputBufferSize + tempBufferSize;
        java.util.Arrays.fill(regArr,"0000");
    }

    public void schedPCB (int sAR, int eAR)
    {
        startAddressRAM=sAR;
        endAddressRAM=eAR;
        startAddressInputBufferRAM=startAddressRAM+codeSize;
        startAddressOutputBufferRAM=startAddressRAM+codeSize+inputBufferSize;
        
    }
    
    public String printPCB()
    {
        String TempPrint="";
        TempPrint = 
        "\n" + "jobID " + jobID + 
        "\n" + "codeSize " + codeSize + 
        "\n" + "priority " + priority +
        "\n" + "inputBufferSize " + inputBufferSize +
        "\n" + "outputBufferSize " + outputBufferSize +
        "\n" + "tempBufferSize " + tempBufferSize +
        "\n" + "startAddressDISK " + startAddressDISK +
        "\n" + "endAddressDISK " + endAddressDISK + 
        "\n" + "cpuId " + cpuId +
        "\n" + "status " + status +
        "\n" + "totalSize " + totalSize +
        "\n" + "startAddressRAM " + startAddressRAM +
        "\n" + "endAddressRAM " + endAddressRAM +
        "\n" + "startAddressInputBufferRAM " +startAddressInputBufferRAM +
        "\n" + "startAddressOutputBufferRAM " + startAddressOutputBufferRAM;
                       
        return TempPrint;
    }
       
    public void state()
    {
        //record of environment that is saved on interrupt
        //including the pc, registers, permissions, buffers, caches,
        //active pages/blocks
    }
    
    public void sched()
    {
        //burst-time, priority, queue-type, time-slice, remain-time
    }                    
    
    public void accounts()
    {
        // cpu-time, time_limit, time_delays, start/end times, io-times
    }                   
    
    public void memories()
    {
        // page_table_base, pages, page_size
        // base_registers- logical/ physical map, limit_reg
    }                    

    public void progeny()
    {
        // child-procid, child_code_pointers
    }

    public void parent()
    {
        //parent ptr;
        //points to parent (if these process is spawned, else null)
    }

    public void resources()
    {
        // file-pointers, io-devices, unit#, open-file tables
    }


    public void status_info()
    {
        // points to 'ready-list of active processs' or
        //resource list on blocked processes
    }
}
