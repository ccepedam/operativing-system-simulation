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
public class PageTable {
    
    private final int DiskSize = 2048;
    private final int pageSize = 4;
    
    private final int NumberOfPages=DiskSize/pageSize;
    
    private LinkedList<Page> pagesDisk = new LinkedList<>();
    private LinkedList<Integer> freePages = new LinkedList<>();
    
    PageTable()
    {
        
        for(int i=0; i<NumberOfPages; i++)
        {
            Page NewPage = new Page(i, i*4);
            pagesDisk.add(NewPage);
        }
        
        for(int i=0; i<pagesDisk.size(); i++)
        {
            if(!pagesDisk.get(i).getisUsed())
            {
                freePages.addLast(pagesDisk.get(i).getpageNumber());
            }
        }
    }
    
    public Page getPage(int i)
    {
        return pagesDisk.get(i);
    }
    
    public Page usePage()
    {       
        return pagesDisk.get(freePages.pop());
    }
    

    public void releasePage(Page p)
    {
        p.setJobID(-1);
        p.setisUsed(false);
        p.setinRam(false);
        p.setequFrame(-1);
        p.setcounter(0);
        freePages.addLast(p.getpageNumber());
        //freePages.sort(null);
    }
    
    public int freePagesThere()
    {
        return freePages.size();
    }
    
}
