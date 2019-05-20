/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osdriver;

import java.util.Collections;
import java.util.LinkedList;


/**
 *
 * @author CARLOSANDRES
 */
public class SCHEDULER {
    
    private String algoritmType;
    private int RAMSize;
    
    
    public SCHEDULER()
    {
        algoritmType="priority";
        RAMSize=0;
    }
    
    public SCHEDULER(String newalgoritmType, int newRAMSize)
    {
        algoritmType=newalgoritmType;
        RAMSize=newRAMSize;
    }
    
    public String getalgoritmType() { return algoritmType;}
    public void setalgoritmType(String at) { algoritmType = at;}
    
    public int getRAMSize() { return RAMSize;}
    public void setRAMSize(int i) { RAMSize = i;}
    
      
    public void sortJobList(LinkedList<JOBLIST> jobListDisk)
    {
        
        switch (algoritmType) {
        case "priority": Collections.sort(jobListDisk);
                break;
        case "FIFO":    
                break;
        default: Collections.sort(jobListDisk);
                break;
        }
                   
    }
    
    public void buildReadyQueue(LinkedList<JOBLIST> jobListDisk, LinkedList<JOBLIST> ReadyQueue, MEMORY M1)
    {
        int maxsizeJobs=0;
        int counter=0;
        
        int jobListDiskSize = jobListDisk.size();
        while(maxsizeJobs<(M1.getRamCapacity()-M1.getRamUsed()) && counter<jobListDiskSize )
        {
            maxsizeJobs=maxsizeJobs+(jobListDisk.get(counter).getTotalSize());
            counter=counter+1;
        }
        if(counter<jobListDiskSize)
        {
            counter=counter-1;
        }
        
        for(int i=0; i<counter; i++)
        {
            ReadyQueue.add(jobListDisk.get(i));
        }
        for(int i=0; i<counter; i++)
        {
            jobListDisk.removeFirst();
        }
    }
    
    public void loadJobsToRAM(LinkedList<JOBLIST> ReadyQueue, MEMORY M1, LinkedList<PCB> pcbList)
    {
        for(int i=0; i<ReadyQueue.size(); i++)
        {
            for(int j=pcbList.get(ReadyQueue.get(i).getjobID()-1).getstartAddressDISK(); j<=pcbList.get(ReadyQueue.get(i).getjobID()-1).getendAddressDISK(); j++)
            {
                M1.wMEMORY(1, (M1.rMemory(0, j)));
            }
            
            int tempStartAddressRAM = (M1.getRamUsed()-pcbList.get(ReadyQueue.get(i).getjobID()-1).getTotalSize());       
            pcbList.get(ReadyQueue.get(i).getjobID()-1).schedPCB(tempStartAddressRAM, M1.getRamUsed()-1);
            pcbList.get(ReadyQueue.get(i).getjobID()-1).setStatus("Ready");
            
        }   
    }
    
}
