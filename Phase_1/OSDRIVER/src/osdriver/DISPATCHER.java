/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osdriver;

import java.util.LinkedList;

/**
 *
 * @author CARLOSANDRES
 */
public class DISPATCHER {
    
    
    public DISPATCHER()
    {
        
    }
    
    
    public void assignProcessToCPU(MEMORY M1, CPU CPU1, LinkedList<PCB> pcbList, LinkedList<JOBLIST> ReadyQueue, LinkedList<JOBLIST> RuningQueue, LinkedList<JOBLIST> WaitingQueue)
    {
        if(ReadyQueue.size()!=0)
        {
            //set CPU's PC
            int jobcurrent=ReadyQueue.getFirst().getjobID();
            
            CPU1.setPC(pcbList.get((ReadyQueue.getFirst().getjobID())-1).getprogramCounter());
            CPU1.setmemoryRangeMin(pcbList.get((ReadyQueue.getFirst().getjobID())-1).getstartAddressRAM());
            CPU1.setmemoryRangeMax(pcbList.get((ReadyQueue.getFirst().getjobID())-1).getendAddressRAM());
            RuningQueue.add(ReadyQueue.pollFirst());
            pcbList.get((RuningQueue.getFirst().getjobID())-1).setStatus("Runing");
            pcbList.get((RuningQueue.getFirst().getjobID())-1).setcpuId(CPU1.getCpuID());
            CPU1.setJobIDRuning(jobcurrent);
            loadToCACHE(CPU1, pcbList.get((RuningQueue.getFirst().getjobID())-1), M1, pcbList);
        } else {
            System.err.println("There are Not more Jobs in Ready Queue");
        }
    }

    
    public void loadToCACHE(CPU CPU1, PCB currentPCB, MEMORY M1, LinkedList<PCB> pcbList)
    {
        
        for(int j=currentPCB.getstartAddressRAM(); j<=currentPCB.getendAddressRAM(); j++)
        {
            CPU1.addCacheElement(M1.readMemory(CPU1, 1, j));
        }
         
    }
    
    public void SwitchingContext(MEMORY M1, LinkedList<PCB> pcbList, CPU CPU1, LinkedList<JOBLIST> WaitingQueue, LinkedList<JOBLIST> RuningQueue)
    {
        pcbList.get((CPU1.getJobIDRuning())-1).setStatus("Waiting");
        pcbList.get((CPU1.getJobIDRuning())-1).setprogramCounter(CPU1.getPC());
        CPU1.setPC(-1);
        WaitingQueue.add(RuningQueue.removeFirst());
        System.out.println("IO Request of Job# " + CPU1.getJobIDRuning());
        
        //TRANSFERING DATA CHANGED IN CACHE TO RAM
        
        PCB currentPCB = pcbList.get((CPU1.getJobIDRuning())-1);
        for(int i=0; i<currentPCB.getSizeDataChanged(); i++)
        {
            int addCACHE = currentPCB.getDataChangedElement(i);
            int addRAM = currentPCB.getstartAddressRAM();
            M1.wMEMORY(1, addRAM+addCACHE, CPU1.getCacheElement(addCACHE));
        }
        CPU1.resetCACHE();

    }

}
