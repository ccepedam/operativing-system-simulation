/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osdriver;

import java.util.LinkedList;

/**
 *
 * @author CARLOSANDRES
 */
public class DMA {
    
    int newTypeAdd = 1; //Type Physical Address
        
    int breg1=-1;
    int dreg2=-1;
    int adda=-1;

    int inbreg1=-1;
    int indreg2=-1;
    
    String IRDMA;
    int temPC; 
    int temJobDAM;
    
    public void DMA()
    {
        newTypeAdd = 1; //Type Physical Address
        breg1=-1;
        dreg2=-1;
        adda=-1;
        inbreg1=-1;
        indreg2=-1;
    }
    
    
    public void runDMA(CPU CPU1, FETCH FT, EFFADD EFF, MEMORY M1, DECODE DC, LinkedList<PCB> pcbList, LinkedList<JOBLIST> ReadyQueue, LinkedList<JOBLIST> WaitingQueue)
    {
        newTypeAdd = 1; //Type Physical Address
        
        temJobDAM=WaitingQueue.getFirst().getjobID();
        
        temPC=pcbList.get(temJobDAM-1).getprogramCounter();
        IRDMA=FT.fetcthInfoDMA(temPC, pcbList.get(temJobDAM-1), EFF, M1, newTypeAdd);
        DC.DecodeInstruction(IRDMA);
        
        String OPCODE = DC.getOPCODE();
        String Param1 = DC.getParam1();
        String Param2 = DC.getParam2();
        String Param3 = DC.getParam3();
        
        //code to convert from string to integer
        breg1=Integer.parseInt(Param1, 2);
        dreg2=Integer.parseInt(Param2, 2);
        adda=Integer.parseInt(Param3, 2);
              
        inbreg1 = Integer.parseInt((pcbList.get((temJobDAM)-1).getRegister(breg1)), 2);
        indreg2 = Integer.parseInt((pcbList.get((temJobDAM)-1).getRegister(dreg2)), 2);
        
        
        while(OPCODE.equals("000000") || OPCODE.equals("000001"))
        {
            
            switch (OPCODE)
            {
                case "000000": 
                    if(dreg2==0 && adda!=0)
                    {
                        pcbList.get((temJobDAM)-1).setRegister(breg1, (Integer.toBinaryString(Integer.parseInt((FT.fetcthInfoDMA(adda/4, pcbList.get((temJobDAM)-1), EFF, M1, newTypeAdd)), 16))));
                    }
                    if(dreg2!=0 && adda==0)
                    {
                        pcbList.get((temJobDAM)-1).setRegister(breg1, (Integer.toBinaryString(Integer.parseInt((FT.fetcthInfoDMA(indreg2/4, pcbList.get((temJobDAM)-1), EFF, M1, newTypeAdd)), 16))));
                    }
                    break;
                case "000001": // if 03, Loads the content of an address into a reg.
                    if(dreg2==0 && adda!=0)
                    {
                        M1.writeMEMORYDMA(pcbList.get((temJobDAM)-1), 1, (EFF.getDIRAdd(newTypeAdd, adda/4, pcbList.get((temJobDAM)-1))), Integer.toHexString(inbreg1));
                    }
                    if(dreg2!=0 && adda==0)
                    {
                        M1.writeMEMORYDMA(pcbList.get((temJobDAM)-1), 1, (EFF.getDIRAdd(newTypeAdd, indreg2/4, pcbList.get((temJobDAM)-1))), Integer.toHexString(inbreg1));
                    }
                    break;
                default:
                    System.out.println("Invalid opcode in IO DMA" + OPCODE);
                    break;
            }
            
            temPC=temPC+1;
            
            IRDMA=FT.fetcthInfoDMA(temPC, pcbList.get(temJobDAM-1), EFF, M1, newTypeAdd);
            DC.DecodeInstruction(IRDMA);

            OPCODE = DC.getOPCODE();
            Param1 = DC.getParam1();
            Param2 = DC.getParam2();
            Param3 = DC.getParam3();

            breg1=Integer.parseInt(Param1, 2);
            dreg2=Integer.parseInt(Param2, 2);
            adda=Integer.parseInt(Param3, 2);

            inbreg1 = Integer.parseInt((pcbList.get((temJobDAM)-1).getRegister(breg1)), 2);
            indreg2 = Integer.parseInt((pcbList.get((temJobDAM)-1).getRegister(dreg2)), 2);

        }
        
        pcbList.get((temJobDAM)-1).setStatus("Ready");
        pcbList.get((temJobDAM)-1).setprogramCounter(temPC);
        ReadyQueue.addFirst(WaitingQueue.removeFirst());
        
        System.out.println("End of IO Request of Job# " + temJobDAM);
        System.out.println("PC new for ex waiting " + pcbList.get(temJobDAM-1).getprogramCounter());
        DC.resetDECODE();
        
    }
    
    
}
