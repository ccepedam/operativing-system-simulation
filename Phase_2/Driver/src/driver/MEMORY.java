/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;

import static driver.Driver.unit;
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
    
    public void PrintDisk() 
    {
        for (int i=0; i<DISK.length; i++ )
        {
            System.out.println(DISK[i]);
        }
    }
    
    
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
    
    
    public void LWriteToDisk(Page p, String line)
    {
        /*try {
            unit.acquireLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error acquiring lock!");
        }*/
        
        int offset = p.getcounter();
        int baseAddress = p.getstartAddress();
        int address = baseAddress + offset;
        if(address<diskCapacity)
        {
            DISK[address] = line;
        } else{System.err.println("Invalid address");}
        
        /*try {
            unit.releaseLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error releasing lock!");
        }*/
    }
    
    
    public void WritePageToFrame(Page p, Frame f)
    {
        /*try {
            unit.acquireLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error acquiring lock!");
        }*/
        
        for(int i=0; i<4; i++)  //Warning if the page or frame size change
        {
            String TempLine = DISK[p.getstartAddress()+i];
            RAM[f.getstartAddress()+i] = TempLine;
        }
        
        /*try {
            unit.releaseLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error releasing lock!");
        }*/
    }
    
    public String ReadRam(Frame f, int offset)
    {
        /*try {
            unit.acquireLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error acquiring lock!");
        }*/
        
        String TempLine = RAM[f.getstartAddress()+offset];
        
        
        /*try {
            unit.releaseLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error releasing lock!");
        }*/
        
        return TempLine;
    }
    
    public void WriteCacheToFrame(CacheFrame c, Frame f)
    {
        try {
            unit.acquireLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error acquiring lock!");
        }
        
        String TempLine;
        for(int i=0; i<4; i++)  //Warning if the page or frame size change
        {
            TempLine = c.getData(i);
            RAM[f.getstartAddress()+i] = TempLine;
        }
        
        try {
            unit.releaseLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error releasing lock!");
        }
        
    }
    
    public void WriteFrameToPage(Frame f, Page p)
    {
        /*try {
            unit.acquireLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error acquiring lock!");
        }*/
        
        for(int i=0; i<4; i++)  //Warning if the page or frame size change
        {
            String TempLine = RAM[f.getstartAddress()+i];
            DISK[p.getstartAddress()+i]=TempLine;
        }
        
        /*try {
            unit.releaseLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error releasing lock!");
        }*/
        
    }
    
    public void ResetFrame(Frame f)
    {
        /*try {
            unit.acquireLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error acquiring lock!");
        }*/
        
        for(int i=0; i<4; i++)  //Warning if the page or frame size change
        {
            RAM[f.getstartAddress()+i]=null;
        }
        
        /*try {
            unit.releaseLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error releasing lock!");
        }*/
        
    }
        
}
