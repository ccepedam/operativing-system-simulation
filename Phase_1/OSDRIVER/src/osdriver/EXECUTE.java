/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osdriver;

import static java.lang.Math.abs;
import java.util.LinkedList;

/**
 *
 * @author CARLOSANDRES
 */
public class EXECUTE {
    
    
    private String Function;
    private String OPCODE;
    private String Param1;
    private String Param2;
    private String Param3;
    
    
    public void EXECUTE()
    {
        
        Function = null;
        OPCODE = null;
        Param1 = null;
        Param2 =null;
        Param3 = null;
    }
    
    
    public void runEXECUTE(DISPATCHER DIS1, FETCH FT, EFFADD EFF, MEMORY M1, CPU CPU1, DECODE DC, LinkedList<PCB> pcbList, LinkedList<JOBLIST> RuningQueue, LinkedList<JOBLIST> WaitingQueue, LinkedList<JOBLIST> TerminatedQueue)
    {
        Function = DC.getFunction();
        OPCODE = DC.getOPCODE();
        Param1 = DC.getParam1();
        Param2 = DC.getParam2();
        Param3 = DC.getParam3();
        
        System.out.println(Function + " " + OPCODE + " " + Param1 + " " + Param2 + " " +Param3);
        
        
        switch (Function)
        {
            case"00":
                Arithmetic(OPCODE, Param1, Param2, Param3, pcbList, RuningQueue, CPU1);
                break;
            case"01":
                CondBranchImFormat(FT, EFF, M1, OPCODE, Param1, Param2, Param3, pcbList, RuningQueue, CPU1);
                break;
            case"10":
                UnconditionalJump(M1, OPCODE, Param1, pcbList, CPU1, RuningQueue, TerminatedQueue);
                break;
            case"11":
                System.out.println("Here in case 11 for IO, opcode = " + OPCODE);
                
                switch (OPCODE)
                {
                    case"000000": 
                        DIS1.SwitchingContext(M1, pcbList, CPU1, WaitingQueue, RuningQueue);
                        break;
                    case"000001": 
                        DIS1.SwitchingContext(M1, pcbList, CPU1, WaitingQueue, RuningQueue);
                        break;
                    case"001011": 
                        InputOutputFormat(Param2, Param3, pcbList, CPU1);
                        break;
                    default:
                        System.out.println("Invalid opcode in IO " + OPCODE);
                        break;
                }
                break;
            default:
                System.out.println("Invalid function");
                break;
        }
        
        DC.resetDECODE();
    }
    
    
    private void Arithmetic(String OPCODE, String Param1, String Param2, String Param3, LinkedList<PCB> pcbList, LinkedList<JOBLIST> RuningQueue, CPU CPU1)
    {
        //code to convert from string to integer
        int sreg1=Integer.parseInt(Param1, 2);
        int sreg2=Integer.parseInt(Param2, 2);
        int dreg=Integer.parseInt(Param3, 2);

        int temp;
        int tempA;
        int tempB;
        
        tempA = Integer.parseInt((pcbList.get((CPU1.getJobIDRuning())-1).getRegister(sreg1)), 2);
        tempB = Integer.parseInt((pcbList.get((CPU1.getJobIDRuning())-1).getRegister(sreg2)), 2);

        switch (OPCODE)
        {
            case"000100": // 4 in hex, Transfers the content of one register into another
                // test code
                pcbList.get((CPU1.getJobIDRuning())-1).setRegister(sreg1, (pcbList.get((CPU1.getJobIDRuning())-1).getRegister(sreg2)));
                //pcbList.get((CPU1.getJobIDRuning())-1).setRegister(sreg2, "0000"); //No en TAI code verificar
                break;
            case "000101":
                temp = tempA + tempB;
                pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg, Integer.toBinaryString(temp));
                break;
            case "000110":
                temp = abs(tempA - tempB); //ABS value maybe is required in TAI CODE
                pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg, Integer.toBinaryString(temp));
                break;
            case "000111":
                temp = tempA * tempB;
                pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg, Integer.toBinaryString(temp));
                break;
            case "001000": // 8 in hex
                temp = tempA / tempB; //According to the Job 3 instruction DIV it is correct.
                pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg, Integer.toBinaryString(temp));
                break;
            case "001001":
                temp = (tempA & tempB);
                pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg, Integer.toBinaryString(temp));
                break;
            case "001010":
                temp = (tempA | tempB);
                pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg, Integer.toBinaryString(temp));
                break;
            case "010000": // 10 in hex, Sets the D-reg to 1 if  first S-reg is less than second B-reg, and 0 otherwise
                if (tempA < tempB)
                    pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg, "0001");
                else
                    pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg, "0000");
                break;
            default:
                System.out.println("Invalid opcode in aritmetic " + OPCODE);
                break;
        }
    }

    private void CondBranchImFormat(FETCH FT, EFFADD EFF, MEMORY M1, String OPCODE, String Param1, String Param2, String Param3, LinkedList<PCB> pcbList, LinkedList<JOBLIST> RuningQueue, CPU CPU1)
    {
        int newTypeAdd = 1; //Type Physical Address
        
        //code to convert from string to integer
        int breg1=Integer.parseInt(Param1, 2);
        int dreg2=Integer.parseInt(Param2, 2);
        int adda=Integer.parseInt(Param3, 2);

        int inbreg1;
        int indreg2;
                
        inbreg1 = Integer.parseInt((pcbList.get((CPU1.getJobIDRuning())-1).getRegister(breg1)), 2);
        indreg2 = Integer.parseInt((pcbList.get((CPU1.getJobIDRuning())-1).getRegister(dreg2)), 2);
                
        switch (OPCODE)
        {
            case "000010":
                CPU1.setCacheElement(indreg2/4, Integer.toHexString(inbreg1));
                pcbList.get((CPU1.getJobIDRuning())-1).addDataChangedElement(indreg2/4);
                break;
            case "000011": // if 03, Loads the content of an address into a reg.
                pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg2, (Integer.toBinaryString(Integer.parseInt(CPU1.getCacheElement(inbreg1/4), 16))));
                break;
            case "001011": // if opcode is 0B (in hex), Transfers address/data directly into a register
                pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg2, Integer.toBinaryString(adda));
                break;
            case "001100": // 0C in hex,
                pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg2, Integer.toBinaryString(indreg2+(adda)));
                break;
            case "001101": // 0D in hex,
                pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg2, Integer.toBinaryString(indreg2*(adda))); //There are not examples on the jobs
                break;    
            case "001110": // 0E in hex,
                pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg2, Integer.toBinaryString(indreg2/(adda))); //There are not examples on the jobs
                break;      
            case "001111": // 0F in hex, Loads a data/address directly to the content of a register
                pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg2, Integer.toBinaryString(adda));
                break;
            case "010001": // 11 in hex
                if(inbreg1<adda)
                {
                    pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg2, "0001");
                }else{
                    pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg2, "0000");
                }
                break;
            case "010101": // 15 in hex, Branches to an address when content of B-reg = D-reg
                if(inbreg1==indreg2)
                {
                    CPU1.setPC((adda/4)-1); //less 1 due to PC increase by 1 after the execution
                }
                break;
            case "010110": // 16 in hex, Branches to an address when content of B-reg <> D-reg
                if (inbreg1!=indreg2)
                {
                    CPU1.setPC((adda/4)-1); //less 1 due to PC increase by 1 after the execution
                }
                break;
            case "010111": // 17 in hex
                if (inbreg1==0)
                {
                    CPU1.setPC((adda/4)-1); //less 1 due to PC increase by 1 after the execution
                }
                break;    
            case "011000": // 18 in hex
                if (inbreg1!=0)
                {
                    CPU1.setPC((adda/4)-1); //less 1 due to PC increase by 1 after the execution
                }
                break;
            case "011001": // 19 in hex
                if (inbreg1>0)
                {
                    CPU1.setPC((adda/4)-1); //less 1 due to PC increase by 1 after the execution
                }
                break;
            case "011010": // 1A in hex
                if (inbreg1<0)
                {
                    CPU1.setPC((adda/4)-1); //less 1 due to PC increase by 1 after the execution
                }
                break;    
            default:
                System.out.println("Invalid opcode in Conditional Branch " + OPCODE);
                break;
        }
    }

    public void UnconditionalJump(MEMORY M1, String OPCODE, String Param1, LinkedList<PCB> pcbList, CPU CPU1, LinkedList<JOBLIST> RuningQueue, LinkedList<JOBLIST> TerminatedQueue)
    {
        int adda=Integer.parseInt(Param1, 2);
        
        switch (OPCODE)
        {
            case "010010": //End of the Job
                pcbList.get((CPU1.getJobIDRuning())-1).setStatus("Finished");
                CPU1.setPC(-1);
                TerminatedQueue.add(RuningQueue.removeFirst());
                //TRANSFEREING CHANGED DATA IN CACHE TO RAM
                PCB currentPCB = pcbList.get((CPU1.getJobIDRuning())-1);
                for(int i=0; i<currentPCB.getSizeDataChanged(); i++)
                {
                    int addCACHE = currentPCB.getDataChangedElement(i);
                    int addRAM = currentPCB.getstartAddressRAM();
                    M1.writeMEMORY(CPU1, 1, addRAM+addCACHE, CPU1.getCacheElement(addCACHE));
                }
                CPU1.resetCACHE();
                               
                System.out.println("End of Job# " + CPU1.getJobIDRuning());
                break;
            case "010100": //Jump
                CPU1.setPC((adda/4)-1);
                break;
            default:
                System.out.println("Invalid opcode for unconditional jump " + OPCODE);
                break;
        }
    }
    
    private void InputOutputFormat(String Param2, String Param3, LinkedList<PCB> pcbList, CPU CPU1)
    {
        
        //code to convert from string to integer
        int dreg2=Integer.parseInt(Param2, 2);
        int adda=Integer.parseInt(Param3, 2);

        pcbList.get((CPU1.getJobIDRuning())-1).setRegister(dreg2, Integer.toBinaryString(adda));
        
    }


}
