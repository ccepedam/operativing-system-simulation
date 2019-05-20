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
public class MEMORY {
    
    private static int diskCapacity = 2048;
    private static int ramCapacity = 1024;
    private static String [] DISK = new String [diskCapacity];
    private static String [] RAM = new String [ramCapacity];
       
    private int diskCounter = 0;
    private int ramCounter = 0;
    
    private int diskUsed = 0;
    private int ramUsed = 0;   
    
    public int getDiskCapacity() {return diskCapacity;}
    public int getRamCapacity() {return ramCapacity;}
    public int getDiskUsed() {diskUsed = diskCounter; return diskUsed;}
    public int getRamUsed() {ramUsed = ramCounter; return ramUsed;}
    
    public void resetDisk() 
    {
        for (int i=0; i<DISK.length; i++ )
        {
            DISK[i]=null;
        }
        diskCounter = 0;
    }
    
    public void resetRAM() 
    {
        for (int i=0; i<RAM.length; i++ )
        {
            RAM[i]=null;
        }
        ramCounter = 0;
    }
    
    public String[] getDisk() 
    {
        
        String [] TempDISK = new String [2048];
        for (int i=0; i<2048; i++ )
        {
            TempDISK[i]= DISK[i];
        }
        return TempDISK;
    }
    
    public String[] getRAM() 
    {
        
        String [] TempRAM = new String [1024];
        for (int i=0; i<1024; i++ )
        {
            TempRAM[i]= RAM[i];
        }
        return TempRAM;
    }
    
    //*********************************************************************************************************************
    public void writeMEMORY(CPU CPU1, int type, int address, String info)
    {
        if (address>= CPU1.getmemoryRangeMin() && address<= CPU1.getmemoryRangeMax())
        {
            
            if (type == 0) // "type Disk = 0"
            {
                if(address<diskCapacity)
                {
                    DISK[address] = info;
                } else{System.err.println("Invalid address");}
            } else if (type == 1) // "type RAM = 1"
            {
                if(address<ramCapacity)
                {
                    RAM[address] = info;
                } else{System.err.println("Invalid address");}
            }
            else {System.err.println("invalid Memory Type"); System.exit(0);}
            
        }else {System.out.println("Not allowed address to CPU*****************************************************************88"); System.exit(0);}
        
    }
    
    public String readMemory(CPU CPU1, int type, int address)
    {
        String Temp ="";
        
        if (address>= CPU1.getmemoryRangeMin() && address<= CPU1.getmemoryRangeMax())
        {
           
            if (type == 0) // "type Disk = 0"
            {
                if(address<diskCapacity)
                {
                    Temp = DISK[address];
                } else{System.err.println("Invalid address");}
            } else if (type == 1) // "type RAM = 1"
            {
                if(address<ramCapacity)
                {
                    Temp = RAM[address];
                } else{System.err.println("Invalid address");}
            }
            else  {System.err.println("invalid Memory Type"); System.exit(0);}
        }else {System.out.println("Not allowed address to CPU*****************************************************************88"); System.exit(0);}
        
        return Temp;
    }
//*********************************************************************************************************************
    
//*********************************************************************************************************************
    public void writeMEMORYDMA(PCB currentPCB, int type, int address, String info)
    {
        if (address>= currentPCB.getstartAddressRAM() && address<= currentPCB.getendAddressRAM())
        {
            
            if (type == 0) // "type Disk = 0"
            {
                if(address<diskCapacity)
                {
                    DISK[address] = info;
                } else{System.err.println("Invalid address");}
            } else if (type == 1) // "type RAM = 1"
            {
                if(address<ramCapacity)
                {
                    RAM[address] = info;
                } else{System.err.println("Invalid address");}
            }
            else {System.err.println("invalid Memory Type"); System.exit(0);}
            
        }else {System.out.println("Not allowed address to CPU*****************************************************************88"); System.exit(0);}
        
        
        
    }
    
    public String readMemoryDMA(PCB currentPCB, int type, int address)
    {
        String Temp ="";
        
        if (address>= currentPCB.getstartAddressRAM() && address<= currentPCB.getendAddressRAM())
        {
           
            if (type == 0) // "type Disk = 0"
            {
                if(address<diskCapacity)
                {
                    Temp = DISK[address];
                } else{System.err.println("Invalid address");}
            } else if (type == 1) // "type RAM = 1"
            {
                if(address<ramCapacity)
                {
                    Temp = RAM[address];
                } else{System.err.println("Invalid address");}
            }
            else  {System.err.println("invalid Memory Type"); System.exit(0);}
        }else {System.out.println("Not allowed address to CPU*****************************************************************88"); System.exit(0);}
        
        return Temp;
    }
//***********************************************************************************************************************
        
    
    public void wMEMORY(int type, int address, String info)
    {
        if (type == 0) // "type Disk = 0"
        {
            if(address<diskCapacity)
            {
                DISK[address] = info;
            } else{System.err.println("Invalid address");}
        } else if (type == 1) // "type RAM = 1"
        {
            if(address<ramCapacity)
            {
                RAM[address] = info;
            } else{System.err.println("Invalid address");}
        }
        else {System.err.println("invalid Memory Type");}
    }
    
    public void wMEMORY(int type, String info)
    {
               
        if (type == 0) // "type Disk = 0"
        {
            if(diskCounter<diskCapacity)
            {
                DISK[diskCounter] = info;
                diskCounter = diskCounter+1;
            } else{System.err.println("Disk Capacity exceeded");}
        } else if (type == 1) // "type RAM = 1"
        {
            if(ramCounter<ramCapacity)
            {
                RAM[ramCounter] = info;
                ramCounter = ramCounter+1;
            } else{System.err.println(" RAM Capacity exceeded");}
        }
        else {System.err.println("invalid Memory Type");}
    }
    
    
    public String rMemory(int type, int address)
    {
        String Temp ="";
        if (type == 0) // "type Disk = 0"
        {
            if(address<diskCapacity)
            {
                Temp = DISK[address];
            } else{System.err.println("Invalid address");}
        } else if (type == 1) // "type RAM = 1"
        {
            if(address<ramCapacity)
            {
                Temp = RAM[address];
            } else{System.err.println("Invalid address");}
        }
        else {System.err.println("invalid Memory Type");}
                
        return Temp;
    }
    
    
    
}
