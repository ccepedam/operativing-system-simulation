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
public class Page {
    
    private int pageNumber=-1;
    private int startAddress=-1;
    private boolean isUsed=false;
    private boolean inRam=false;
    private int equFrame=-1;
    private boolean isFull = false;
    private int counter = 0; 
    private int JobID = -1;
    private String Type="";
    
    
    Page(int newpageNumber, int newstartAddress)
    {
        pageNumber = newpageNumber;
        startAddress = newstartAddress;
    }
    
    // ACCESSORS & MUTATORS
    public int getpageNumber() { return pageNumber;}
    //public void setpageNumber(int pgn) { pageNumber = pgn;}
            
    public int getstartAddress() { return startAddress;}
    //public void setstartAddress(int sa) { startAddress = sa;}
    
    public boolean getisUsed() { return isUsed;}
    public void setisUsed(boolean iu) { isUsed = iu;}
    
    public boolean getinRam() { return inRam;}
    public void setinRam(boolean ir) { inRam = ir;}
    
    public int getequFrame() {return equFrame;}
    public void setequFrame(int ef) { equFrame = ef;}
    
    public String getType() {return Type;}
    public void setType(String t) { Type = t;}
    
    public boolean getisFull() 
    { 
        if(counter==4)
        {
            isFull=true;
        }else
        {
            isFull=false;
        }
        return isFull;
    }
    
    public int getcounter() {return counter;}
    public void setcounter(int c) { counter = c;}
 
    public int getJobID() {return JobID;}
    public void setJobID(int id) { JobID = id;}
    
}
