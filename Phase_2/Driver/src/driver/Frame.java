/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;

/**
 *
 * @author CARLOSANDRES
 */
public class Frame {
    
    private int frameNumber=-1;
    private int startAddress=-1;
    private boolean isUsed=false;
    private int equPage=-1;
    private int equCache=-1;
    private int JobID = -1;
    
    Frame(int newFrameNumber, int newstartAddress)
    {
        frameNumber = newFrameNumber;
        startAddress = newstartAddress;
    }
    
    // ACCESSORS & MUTATORS
    public int getframeNumber() { return frameNumber;}
    //public void setframeNumber(int pgn) { frameNumber = pgn;}
            
    public int getstartAddress() { return startAddress;}
    //public void setstartAddress(int sa) { startAddress = sa;}
    
    public boolean getisUsed() { return isUsed;}
    public void setisUsed(boolean iu) { isUsed = iu;}
    
    public int getequPage() {return equPage;}
    public void setequPage(int ep) { equPage = ep;}
    
    public int getequCache() {return equCache;}
    public void setequCache(int ep) { equCache = ep;}
    
    public int getJobID() {return JobID;}
    public void setJobID(int id) { JobID = id;}
    
    
}
