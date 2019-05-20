/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osdriver;

import java.util.Collections;
import java.util.Comparator;
/**
 *
 * @author CARLOSANDRES
 */
public class JOBLIST implements Comparable<JOBLIST> {
    
    private int jobID;              
    private Integer priority;
    private int totalSize;
    
    public JOBLIST()
    {
        jobID=-1;              
        priority=0;
        totalSize=0;
    }
    
    // ACCESSORS & MUTATORS
    public int getjobID() { return jobID;}
    public void setjobID(int id) { jobID = id;}
    
    public Integer getpriority() { return priority;}
    public void setpriority(Integer pr) { priority = pr;}
    
    public int getTotalSize() {return totalSize;}
    public void setTotalSize(int i) { totalSize = i;}
       
    
    public void loaderJOBLIST(int newjobID, Integer newpriority, int newtotalSize)
    {
        jobID=newjobID;              
        priority=newpriority;
        totalSize=newtotalSize;
    }
    
    public String printJOBLIST()
    {
        String TempPrint="";
        TempPrint = 
        "\n" + "jobID " + jobID + 
        "\n" + "priority " + priority +
        "\n" + "totalSize " + totalSize;
                       
        return TempPrint;
    }
    
       
    @Override
    public int compareTo(JOBLIST jl) 
    {
        int comparedP = jl.getpriority();
        if (this.getpriority() > comparedP) {
                return 1;
        } else if (this.getpriority() == comparedP) {
                return 0;
        } else {
                return -1;
        }
    }
    
}
