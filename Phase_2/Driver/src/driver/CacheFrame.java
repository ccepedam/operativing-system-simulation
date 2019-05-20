/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;

import java.util.Vector;

/**
 *
 * @author CARLOSANDRES
 */
public class CacheFrame {
    
    
    private int cframeNumber=-1;
    private int equFrame=-1;
    private int JobID = -1;
    private Vector<String> DataCache;
    
    CacheFrame(int number)
    {
        cframeNumber = number;
        DataCache = new Vector<>(4);
    }
    
    // ACCESSORS & MUTATORS
    public int getcframeNumber() { return cframeNumber;}
            
    public int getequFrameRam() {return equFrame;}
    public void setequFrameRam(int ep) { equFrame = ep;}
    
    public int getJobID() {return JobID;}
    public void setJobID(int id) { JobID = id;}
    
    public String getData(int i) {return DataCache.get(i);}
    public void addData(int i, String line) { DataCache.add(i, line);}
    public void setData(int i, String line) { DataCache.set(i, line);}
    
    
}

