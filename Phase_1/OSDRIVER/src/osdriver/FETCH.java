/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osdriver;

/**
 *
 * @author CARLOSANDRES
 */
public class FETCH {
    
    private String info;
    
    public void FETCH()
    {
        info="";
    }
        
    public String fetcthInfoDMA(int newDisplacement, PCB currentPCB, EFFADD EFF, MEMORY M1, int newTypeAdd)
    {
               
        int Address = EFF.getDIRAdd(newTypeAdd, newDisplacement, currentPCB);
        info=M1.readMemoryDMA(currentPCB, 1, Address);
        return info;
        
    }

}
