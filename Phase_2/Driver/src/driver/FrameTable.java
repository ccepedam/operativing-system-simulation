/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;

import static driver.Driver.mem;
import static driver.Driver.unit;
import java.util.LinkedList;

/**
 *
 * @author CARLOSANDRES
 */
public class FrameTable {
    
    
    private final int RamSize = 1024;
    private final int frameSize = 4;
    
    private final int NumberOfFrames=RamSize/frameSize;
    
    private LinkedList<Frame> framesRam = new LinkedList<>();
    private LinkedList<Integer> freeFrames = new LinkedList<>();
    
    FrameTable()
    {
        for(int i=0; i<NumberOfFrames; i++)
        {
            Frame NewFrame = new Frame(i, i*4);
            framesRam.add(NewFrame);
        }
        for(int i=0; i<framesRam.size(); i++)
        {
            if(!framesRam.get(i).getisUsed())
            {
                freeFrames.addLast(framesRam.get(i).getframeNumber());
            }
        }
    }
    
    public Frame getFrame(int i)
    {
        return framesRam.get(i);
    }
    
    public Frame useFrame()
    {       
        return framesRam.get(freeFrames.pop());
    }
    

    public void releaseFrame(Frame f)
    {
        /*try {
            unit.acquireLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error acquiring lock!");
        }*/
        
        f.setJobID(-1);
        f.setisUsed(false);
        f.setequPage(-1);
        freeFrames.addLast(f.getframeNumber());
        mem.ResetFrame(f);
        //freePages.sort(null);
        
        /*try {
            unit.releaseLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error releasing lock!");
        }*/
      
    }
    
    public int freeFramesThere()
    {
        return freeFrames.size();
    }
    
}
