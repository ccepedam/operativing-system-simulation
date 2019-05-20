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
public class EFFADD {
    
    
    private int Address;
    private int TypeAdd;
    
    public void EFFADD()
    {
        Address=-1;
        TypeAdd=-1; //If Logical = 0; Physical = 1;
    }
    
    // The next two functions are working with physical addresses.
    //Direct Adrressing
    public int getDIRAdd(int newTypeAdd, int newDisplacement, PCB currentPCB)
    {
        TypeAdd=newTypeAdd;
        switch (TypeAdd) {
            case 0:     //Code for LOGICAL address as an input. To be implemented on Phase 2.
                
                break;
                
            case 1:
                Address=currentPCB.getstartAddressRAM()+newDisplacement; 
                
                break;
            
            default:
                System.out.println("Invalid Address Type");
                break;
        }
        TypeAdd=-1;
        return Address;
    }

    //Indirect Adrressing
    public int getINDAdd(int newTypeAdd, int newindexReg, int newDisplacement, PCB currentPCB)
    {
        TypeAdd=newTypeAdd;
        switch (TypeAdd) {
            case 0:     //Code for LOGICAL address as an input. To be implemented on Phase 2.
                
                break;
                
            case 1:
                
                Address=currentPCB.getstartAddressRAM()+ Integer.parseInt((currentPCB.getRegister(newindexReg)), 16) +newDisplacement; //Verify objectPCB name
                
                break;
            
            default:
                System.out.println("Invalid Address Type");
                break;
        }
        TypeAdd=-1;
        return Address;
    }
        
    
}
