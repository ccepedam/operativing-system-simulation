/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;


import static driver.CPU.dr;
import static driver.Driver.dequeNew;
import static driver.Driver.dequeReady;
import static driver.Driver.dequeWaiting;
import static driver.Driver.frameTable;
import static driver.Driver.mem;
import static driver.Driver.pageTable;
import java.util.LinkedList;

/**
 *
 * @author CARLOSANDRES
 */
public class ServicingFault implements Runnable
{
    static Driver dr;
    public static PCB cur; // Current Job
    public static int NumberPageFault=0;
    
    public ServicingFault(Driver obj) throws InterruptedException
    {
        dr = obj;//new Driver();
    }
    
    public void run()
    {
        /*try {
            dr.acquireLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error acquiring lock!");
        }*/
        
        //Thread Stamp
        System.out.println(Thread.currentThread().getName()+ " Starting Servicing Fault");
        
        while(dequeWaiting.size()>0)
        {
            cur=dequeWaiting.getFirst();
            NumberPageFault=cur.CurrentPageFault;
            SmallLongTermSched();
            if(frameTable.freeFramesThere()==0)
            {
                break;
            }
        }
        
        /*try {
            dr.releaseLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error releasing lock!");
        }*/

    }
    
    
    public static void SmallLongTermSched()
    {

        if(frameTable.freeFramesThere()>0)
        {
            Page TempPage = pageTable.getPage(cur.getJobPage(NumberPageFault));
            Frame TempFrame = frameTable.useFrame();
            mem.WritePageToFrame(TempPage, TempFrame);
            TempPage.setinRam(true);
            TempPage.setequFrame(TempFrame.getframeNumber());
            TempFrame.setisUsed(true);
            TempFrame.setequPage(TempPage.getpageNumber());
            TempFrame.setJobID(TempPage.getJobID());

            cur.setStatus("ready");
            cur.isInRam(true);
            dequeReady.addFirst(cur);
            dequeWaiting.removeFirst();
            cur.CurrentPageFault=0;
            cur.PagFault = false;
            cur.TFault="NF";
        }
      
    }
}
