/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;

import java.util.LinkedList;

/**
 *
 * @author CARLOSANDRES
 */
public class CacheFrameTable {
    
    private LinkedList<CacheFrame> CacheInstructions = new LinkedList<>();
    private LinkedList<CacheFrame> CacheInput = new LinkedList<>();
    private LinkedList<CacheFrame> CacheOutput = new LinkedList<>();
    private LinkedList<CacheFrame> CacheTemp = new LinkedList<>();
    private int Count1;
    private int Count2;
    private int Count3;
    private int Count4;
    
    
    CacheFrameTable()
    {
        Count1=0;
        Count2=0;
        Count3=0;
        Count4=0;
    }
    
    public CacheFrame getCFrameIns(int i)
    {
        return CacheInstructions.get(i);
    }
    
    public CacheFrame getCFrameInpB(int i)
    {
        return CacheInput.get(i);
    }
    
    public CacheFrame getCFrameOutB(int i)
    {
        return CacheOutput.get(i);
    }
    
    public CacheFrame getCFrameTempB(int i)
    {
        return CacheTemp.get(i);
    }
    
    public CacheFrame useFrameIns()
    {       
        CacheFrame TempCFrame = new CacheFrame(Count1);
        CacheInstructions.add(TempCFrame);
        Count1++;
        return CacheInstructions.get(Count1-1);
    }
    public CacheFrame useFrameInpB()
    {       
        CacheFrame TempCFrame = new CacheFrame(Count2);
        CacheInput.add(TempCFrame);
        Count2++;
        return CacheInput.get(Count2-1);
    }
    public CacheFrame useFrameOutB()
    {       
        CacheFrame TempCFrame = new CacheFrame(Count3);
        CacheOutput.add(TempCFrame);
        Count3++;
        return CacheOutput.get(Count3-1);
    }
    public CacheFrame useFrameTempB()
    {       
        CacheFrame TempCFrame = new CacheFrame(Count4);
        CacheTemp.add(TempCFrame);
        Count4++;
        return CacheTemp.get(Count4-1);
    }

    
    public void releaseCFrameIns()
    {
        CacheInstructions.clear();
    }
    public void releaseCFrameInpB(CacheFrame cf)
    {
        CacheInput.clear();
    }
    public void releaseCFrameOutB(CacheFrame cf)
    {
        CacheOutput.clear();
    }
    public void releaseCFrameTempB(CacheFrame cf)
    {
        CacheTemp.clear();
    }
    
}
   