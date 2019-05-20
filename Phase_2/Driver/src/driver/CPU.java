/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;

import static driver.Driver.dequeFinished;
import static driver.Driver.dequeReady;
import static driver.Driver.dequeRunning;
import static driver.Driver.dequeWaiting;
import static driver.Driver.mem;
import static driver.Driver.unit;
import java.io.IOException;
import java.math.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author CARLOSANDRES
 */
public class CPU implements Runnable
{
    static Driver dr;
    PCB cur; // Current Job
   
    public String IR; // instruction register

    public LinkedList<Integer> ChangedCFrames = new LinkedList<>();
    public boolean PFault=false;
    public String TypeFault="";
    
    private String current;
    private PageTable currentPT;
    private FrameTable currentFT;
    private CacheFrameTable Cache;
    
  
    public CPU(Driver obj)
    {
        dr = obj;   //new Driver();
    }
    
    public CPU(Driver obj, PCB c, PageTable PT, FrameTable FT) throws InterruptedException
    {
        dr = obj;//new Driver();
        cur = c;
        currentPT=PT;
        currentFT=FT;
        Cache = new CacheFrameTable();
        
        ShortTermLoader(cur, PT, FT, mem, Cache);
    }
    
    
    private void ShortTermLoader(PCB current, PageTable PT, FrameTable FT, MEMORY mem, CacheFrameTable newCache) throws InterruptedException
    {
        int NumPages = current.getJobPageSize();
                
        for(int i=0; i<NumPages; i++)
        {
            if(current.getJobID()==PT.getPage(current.getJobPage(i)).getJobID())
            {
                if(PT.getPage(current.getJobPage(i)).getinRam()) //If current Page is in Ram, load to Cache
                {
                    if(PT.getPage(current.getJobPage(i)).getType()=="Ins")
                    {
                        CacheFrame CurrentCFrame = newCache.useFrameIns();
                        CurrentCFrame.setJobID(current.getJobID());
                        CurrentCFrame.setequFrameRam(PT.getPage(current.getJobPage(i)).getequFrame());
                        FT.getFrame(PT.getPage(current.getJobPage(i)).getequFrame()).setequCache(CurrentCFrame.getcframeNumber());
                        
                        for(int j=0; j<4; j++)
                        {
                            String line = mem.ReadRam(FT.getFrame(PT.getPage(current.getJobPage(i)).getequFrame()), j);
                            CurrentCFrame.addData(j, line);
                        }
                    }else if(PT.getPage(current.getJobPage(i)).getType()=="InpB")
                    {
                        CacheFrame CurrentCFrame = newCache.useFrameInpB();
                        CurrentCFrame.setJobID(current.getJobID());
                        CurrentCFrame.setequFrameRam(PT.getPage(current.getJobPage(i)).getequFrame());
                        FT.getFrame(PT.getPage(current.getJobPage(i)).getequFrame()).setequCache(CurrentCFrame.getcframeNumber());
                        
                        for(int j=0; j<4; j++)
                        {
                            String line = mem.ReadRam(FT.getFrame(PT.getPage(current.getJobPage(i)).getequFrame()), j);
                            CurrentCFrame.addData(j, line);
                        }
                    }else if(PT.getPage(current.getJobPage(i)).getType()=="OutB")
                    {
                        CacheFrame CurrentCFrame = newCache.useFrameOutB();
                        CurrentCFrame.setJobID(current.getJobID());
                        CurrentCFrame.setequFrameRam(PT.getPage(current.getJobPage(i)).getequFrame());
                        FT.getFrame(PT.getPage(current.getJobPage(i)).getequFrame()).setequCache(CurrentCFrame.getcframeNumber());
                        
                        for(int j=0; j<4; j++)
                        {
                            String line = mem.ReadRam(FT.getFrame(PT.getPage(current.getJobPage(i)).getequFrame()), j);
                            CurrentCFrame.addData(j, line);
                        }
                    }else if(PT.getPage(current.getJobPage(i)).getType()=="TemB")
                    {
                        CacheFrame CurrentCFrame = newCache.useFrameTempB();
                        CurrentCFrame.setJobID(current.getJobID());
                        CurrentCFrame.setequFrameRam(PT.getPage(current.getJobPage(i)).getequFrame());
                        FT.getFrame(PT.getPage(current.getJobPage(i)).getequFrame()).setequCache(CurrentCFrame.getcframeNumber());
                        
                        for(int j=0; j<4; j++)
                        {
                            String line = mem.ReadRam(FT.getFrame(PT.getPage(current.getJobPage(i)).getequFrame()), j);
                            CurrentCFrame.addData(j, line);
                        }
                    }else
                    {
                        System.err.println("Invalid Type of Cache Frame");
                    }
                }
            }else
            {
                System.err.println("Invalid Page in ShortTermLoader");
                System.exit(0);
            }
        }
    }
       
    public void run()
    {
        //Thread Stamp
        System.out.println(Thread.currentThread().getName()+ " Starting Job ID = " +cur.getJobID());
        
        //Accounting time
        long startTime = System.nanoTime();
        cur.setabsStartTime(System.currentTimeMillis());
        cur.setStatus("running");
        do {
            try {
                Fetch();
            } catch (InterruptedException ex) {
                Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                DMAChannel();
            } catch (InterruptedException ex) {
                Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while(cur.IR != "HLT" && !cur.PagFault);
        
        //Accounting time
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        cur.setTimeT(elapsedTime/1000);
        cur.setabsEndTime(System.currentTimeMillis());
        
    }

    
    public void DMAChannel() throws InterruptedException
    {
        String bin = HexToBinaryString(current);
        String value = bin.substring(0,2);
        String opc = bin.substring(2,8);
        
        switch (Integer.parseInt(value)) // input / output
        {
            case 11:
                long startTime = System.nanoTime();
                String temp = bin.substring(8);
                String reg1 = temp.substring(0, 4);
                String reg2 = temp.substring(4, 8);
                String address = temp.substring(8);

                //convert opcode to decimal
                int dopc = Integer.parseInt(opc, 2);

                int dreg1 = Integer.parseInt(reg1, 2); // convert to decimal
                int dreg2 = Integer.parseInt(reg2, 2); // convert to decimal
                int physAddress = convertAddress(address); // converts address to decimal and divides by 4

                if (dopc == 00) // 000000 = 00 in hex, Reads content of I/P buffer into accumulator
                {
                    cur.setNIO(1);
                    if (dreg2 != 0) // if reg2 is nonzero, read from reg2 to reg1
                    {
                        String contents = cur.regArr[dreg2];
                        int addr = Integer.parseInt(contents, 2) / 4;

                        String data = Fetch(addr);
                        if(!cur.PagFault)
                        {
                            cur.regArr[dreg1] = Integer.toBinaryString(Integer.parseInt(data, 16));
                            //System.out.println("IO 00 take addr from reg2, fetch and store on reg1. R2 () valueC R1 ()" + dreg2 + " " + contents + " " + data +" " + dreg1 + " " +cur.regArr[dreg1]);
                        }else
                        {
                            TypeFault="IO";
                            cur.TFault=TypeFault;
                            break;
                        }
                    }
                    else // if address is nonzero, read from address to reg1
                    {
                        String hex = Fetch(physAddress); // must convert the data in memory from hex to binary
                        if(!cur.PagFault)
                        {
                            String binData = new BigInteger(hex, 16).toString(2);
                            cur.regArr[dreg1] = binData; // stored as binary
                            //System.out.println("IO Read addrr to reg. Adrre, value, r1 () " + physAddress + " Value" + hex + " Reg "+ dreg1 + "Contents "+ cur.regArr[dreg1]);
                        }else
                        {
                            TypeFault="IO";
                            cur.TFault=TypeFault;
                            break;
                        }
                    }
                }
                else if (dopc == 01) // 00001 = 01 in hex, Writes the content of accumulator into O/P buffer
                {
                    cur.setNIO(1);
                    String tmp = "";
                    tmp = cur.regArr[dreg1];
                    String tmp2 = Integer.toHexString(Integer.parseInt(tmp, 2));
                    
                    if (dreg2 != 0) {
                        String contents = cur.regArr[dreg2];
                        int addr = Integer.parseInt(contents, 2) / 4;
                        SetCache(addr, tmp2);
                        if(!cur.PagFault)
                        {
                            ChangedCFrames.add(addr);                 
                            //System.out.println("IO wCache addr, data " + addr + " " + tmp2);
                        }else
                        {
                            TypeFault="IO";
                            cur.TFault=TypeFault;
                            break;
                        }
                    }
                    else
                    {
                        SetCache(physAddress, tmp2);
                        if(!cur.PagFault)
                        {
                            ChangedCFrames.add(physAddress);                 
                            //System.out.println("IO wCache addr, data " + physAddress + " " + tmp2);
                        }else
                        {
                            TypeFault="IO";
                            cur.TFault=TypeFault;
                            break;
                        }
                        
                    }
                }
                long endTime = System.nanoTime();
                long elapsedTime = endTime - startTime;
                cur.setTimeW(elapsedTime);
                break;
            
            default: 
                ComputeOnly();
                break;
        }
    }
    
    
    public int[][] EffectiveAddress(int address)
    {
        int[][] TempValues = new int[2][1];
        int InsNumber;
        int pageNumber=0;
        int offset=0;
        
        InsNumber=address;
        
        if((InsNumber+1)<=cur.getCodeSize())
        {
            pageNumber=(InsNumber/4);
            offset=InsNumber%4;

        }else if((InsNumber+1)>cur.getCodeSize() && (InsNumber+1)<=(cur.getCodeSize()+cur.getInputBufferSize()))
        {
            if(cur.getCodeSize()%4!=0)
            {
                InsNumber=InsNumber+(4-(cur.getCodeSize()%4));
            }
            pageNumber=(InsNumber/4);
            offset=InsNumber%4;

        }else if((InsNumber+1)>(cur.getCodeSize()+cur.getInputBufferSize()) && (InsNumber+1)<=(cur.getCodeSize()+cur.getInputBufferSize()+cur.getOutputBufferSize()))
        {
            if(cur.getCodeSize()%4!=0)
            {
                InsNumber=InsNumber+(4-(cur.getCodeSize()%4));
            }
            if(cur.getInputBufferSize()%4!=0)
            {
                InsNumber=InsNumber+(4-(cur.getInputBufferSize()%4));
            }

            pageNumber=(InsNumber/4);
            offset=InsNumber%4;

        }else if((InsNumber+1)>(cur.getCodeSize()+cur.getInputBufferSize()+cur.getOutputBufferSize()) && (InsNumber+1)<=(cur.getTotalSize()))
        {
            if(cur.getCodeSize()%4!=0)
            {
                InsNumber=InsNumber+(4-(cur.getCodeSize()%4));
            }
            if(cur.getInputBufferSize()%4!=0)
            {
                InsNumber=InsNumber+(4-(cur.getInputBufferSize()%4));
            }
            if(cur.getOutputBufferSize()%4!=0)
            {
                InsNumber=InsNumber+(4-(cur.getOutputBufferSize()%4));
            }
            pageNumber=(InsNumber/4);
            offset=InsNumber%4;
        }
        
        TempValues[0][0]=pageNumber;
        TempValues[1][0]=offset;
        
        return TempValues;
    }
    
    public void Fetch() throws InterruptedException
    {
        int stop = cur.getCodeSize();
        int InsNumber=0;
        int pageNumber=0;
        int offset=0;
        int[][] values = new int[2][1];

        if (cur.PC != -1 && cur.PC < stop)
        {
            if (cur.PC  == 0) // do this only once per job, loads start address of current job into the PC
            {
                InsNumber=cur.PC;
                offset=InsNumber%4;
                
                if(currentPT.getPage(cur.getJobPage(pageNumber)).getinRam())
                {
                    Page tempPage=currentPT.getPage(cur.getJobPage(pageNumber));
                    Frame tempFrame=currentFT.getFrame(tempPage.getequFrame());
                    int tempNEC=tempFrame.getequCache();
                    
                    current=Cache.getCFrameIns(tempNEC).getData(offset);
                    System.out.println("In Fetch, cur.PC:" + cur.PC + "Current: " + current);
                    cur.PC++;
                }else
                {
                    PageFault(InsNumber, pageNumber);
                    TypeFault="Fault";
                    cur.TFault=TypeFault;
                }
            }
            else
            {
                InsNumber=cur.PC;
                
                values=EffectiveAddress(InsNumber);
                pageNumber=values[0][0];
                offset=values[1][0];
                
                if(currentPT.getPage(cur.getJobPage(pageNumber)).getinRam())
                {
                    if((cur.PC+1)<=cur.getCodeSize())
                    {
                        current=Cache.getCFrameIns(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache()).getData(offset);
                    }else if((cur.PC+1)>cur.getCodeSize() && (cur.PC+1)<=(cur.getCodeSize()+cur.getInputBufferSize()))
                    {
                        current=Cache.getCFrameInpB(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache()).getData(offset);
                    }else if((cur.PC+1)>(cur.getCodeSize()+cur.getInputBufferSize()) && (cur.PC+1)<=(cur.getCodeSize()+cur.getInputBufferSize()+cur.getOutputBufferSize()))
                    {
                        current=Cache.getCFrameOutB(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache()).getData(offset);
                    }else if((cur.PC+1)>(cur.getCodeSize()+cur.getInputBufferSize()+cur.getOutputBufferSize()) && (cur.PC+1)<=(cur.getTotalSize()))
                    {
                        current=Cache.getCFrameTempB(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache()).getData(offset);
                    }
                    System.out.println("In Fetch, cur.PC:" + cur.PC + "Current: " + current);
                    cur.PC++;
                }else
                {
                    PageFault(InsNumber, pageNumber);
                    TypeFault="Fault";
                    cur.TFault=TypeFault;
                }
            }
        }else{
            System.out.println("The instruction is out of bounds on Fetch()");
        }
        
        
    }

    public String Fetch(int address) throws InterruptedException
    {
        int stop = cur.getTotalSize();
        int InsNumber=address;
        int pageNumber=0;
        int offset=0;
        String tempRead="";
        int[][] values = new int[2][1];
        
        if (InsNumber != -1 && InsNumber < stop)
        {
            values=EffectiveAddress(InsNumber);
            pageNumber=values[0][0];
            offset=values[1][0];

            if(currentPT.getPage(cur.getJobPage(pageNumber)).getinRam())
            {
                if((InsNumber+1)<=cur.getCodeSize())
                {
                    tempRead=Cache.getCFrameIns(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache()).getData(offset);
                }else if((InsNumber+1)>cur.getCodeSize() && (InsNumber+1)<=(cur.getCodeSize()+cur.getInputBufferSize()))
                {
                    tempRead=Cache.getCFrameInpB(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache()).getData(offset);
                }else if((InsNumber+1)>(cur.getCodeSize()+cur.getInputBufferSize()) && (InsNumber+1)<=(cur.getCodeSize()+cur.getInputBufferSize()+cur.getOutputBufferSize()))
                {
                    tempRead=Cache.getCFrameOutB(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache()).getData(offset);
                }else if((InsNumber+1)>(cur.getCodeSize()+cur.getInputBufferSize()+cur.getOutputBufferSize()) && (InsNumber+1)<=(cur.getTotalSize()))
                {
                    tempRead=Cache.getCFrameTempB(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache()).getData(offset);
                }
            }else
            {
                PageFault(InsNumber, pageNumber);
                cur.PC--;
            }
        }else{
            System.out.println("The instruction is out of bounds on Fetch(add)");
        }
        return tempRead;
    }
    
    public void SetCache(int address, String data) throws InterruptedException
    {
        int stop = cur.getTotalSize();
        int InsNumber=address;
        int pageNumber=0;
        int offset=0;
        String tempWrite=data;
        int[][] values = new int[2][1];

        if (InsNumber != -1 && InsNumber < stop)
        {
            values=EffectiveAddress(InsNumber);
            pageNumber=values[0][0];
            offset=values[1][0];

            if(currentPT.getPage(cur.getJobPage(pageNumber)).getinRam())
            {
                if((InsNumber+1)<=cur.getCodeSize())
                {
                    Cache.getCFrameIns(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache()).setData(offset, tempWrite);
                }else if((InsNumber+1)>cur.getCodeSize() && (InsNumber+1)<=(cur.getCodeSize()+cur.getInputBufferSize()))
                {
                    Cache.getCFrameInpB(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache()).setData(offset, tempWrite);
                }else if((InsNumber+1)>(cur.getCodeSize()+cur.getInputBufferSize()) && (InsNumber+1)<=(cur.getCodeSize()+cur.getInputBufferSize()+cur.getOutputBufferSize()))
                {
                    Cache.getCFrameOutB(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache()).setData(offset, tempWrite);
                }else if((InsNumber+1)>(cur.getCodeSize()+cur.getInputBufferSize()+cur.getOutputBufferSize()) && (InsNumber+1)<=(cur.getTotalSize()))
                {
                    Cache.getCFrameTempB(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache()).setData(offset, tempWrite);
                }
            }else
            {
                PageFault(InsNumber, pageNumber);
                cur.PC--;
            }
        }else{
            System.out.println("The instruction is out of bounds on setCache()");
        }
    }
    
    public void PageFault(int insNnumber, int pageNumber) throws InterruptedException
    {
        PFault=true;
        cur.PagFault=PFault;
        cur.CurrentPageFault=pageNumber;
        SWITCHING();
        Backup();
    }
    
    public void Backup()
    {
        
        /*try {
            unit.acquireLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error acquiring lock!");
        }*/
        
        int[][] values = new int[2][1];
        int pageNumber=0;
        int offset=0;
        int InsNumber=0;
        CacheFrame TempCache = null;
        Frame TempFrame;
        
        while(ChangedCFrames.size()>0)
        {
            InsNumber=ChangedCFrames.getFirst();
            values=EffectiveAddress(InsNumber);
            pageNumber=values[0][0];
            
            if((InsNumber+1)<=cur.getCodeSize())
            {
                TempCache=Cache.getCFrameIns(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache());

            }else if((InsNumber+1)>cur.getCodeSize() && (InsNumber+1)<=(cur.getCodeSize()+cur.getInputBufferSize()))
            {
                TempCache=Cache.getCFrameInpB(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache());

            }else if((InsNumber+1)>(cur.getCodeSize()+cur.getInputBufferSize()) && (InsNumber+1)<=(cur.getCodeSize()+cur.getInputBufferSize()+cur.getOutputBufferSize()))
            {
                TempCache=Cache.getCFrameOutB(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache());
            }else if((InsNumber+1)>(cur.getCodeSize()+cur.getInputBufferSize()+cur.getOutputBufferSize()) && (InsNumber+1)<=(cur.getTotalSize()))
            {
                TempCache=Cache.getCFrameTempB(currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame()).getequCache());
            }
            
            TempFrame=currentFT.getFrame(currentPT.getPage(cur.getJobPage(pageNumber)).getequFrame());
            mem.WriteCacheToFrame(TempCache, TempFrame);
            ChangedCFrames.removeFirst();
        }
        
        /*try {
            unit.releaseLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error releasing lock!");
        }*/
        
    }
    
    public void SWITCHING()
    {
        /*try {
            unit.acquireLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error acquiring lock!");
        }*/
        
        int identifier=0;
        if(dequeRunning.size()>0)
        {
            for(int i=0; i<dequeRunning.size(); i++)
            {
                if(dequeRunning.get(i).getJobID()==cur.getJobID())
                {
                    identifier=i;
                    break;
                }
            }
            cur.setStatus("waiting");
            dequeWaiting.addLast(cur);
            dequeRunning.remove(identifier);
        }
        
        /*try {
            unit.releaseLock();
        }
        catch (InterruptedException e)
        {
            System.out.println("Error releasing lock!");
        }*/
    }
    
    
    public void ComputeOnly() throws InterruptedException
    {
        String Function = "";
        String OPCODE = "";
        String Param1 = "";
        String Param2 = "";
        String Param3 = "";

        String TempParameters;

        //Method to convert String Hex characters into String bit characters
        String bin = HexToBinaryString(current);

        //Getting the fucntion
        Function = bin.substring(0,2);

        //Getting the OPCODE
        OPCODE = bin.substring(2,8);

        TempParameters = bin.substring(8);

        //Switch fuction to get the parameters according to the Function Type.
        switch (Function) {

            case "00": // arithmetic
                Param1 = TempParameters.substring(0,4);
                Param2 = TempParameters.substring(4,8);
                Param3 = TempParameters.substring(8,12);
                Arithmetic(OPCODE, Param1, Param2, Param3);
                break;

            case "01": // condition branch and immediate
                Param1 = TempParameters.substring(0,4); // base reg
                Param2 = TempParameters.substring(4,8); // dest reg
                Param3 = TempParameters.substring(8); // address
                CondBranchImFormat(Param1, Param2, Param3, OPCODE);
                break;

            case "10": // unconditional jump
                UnconditionalJump(OPCODE);
                break;

            default:
                System.out.println("Invalid Function");
                break;
        }
    }
    
    
    private void Arithmetic(String opcode,String s1, String s2, String d)
    {
        //code to convert from string to integer
        int sreg1=Integer.parseInt(s1, 2);
        int sreg2=Integer.parseInt(s2, 2);
        int dreg=Integer.parseInt(d, 2);

        int temp = -1;
        int tempA = -1;
        int tempB = -1;

        tempA = Integer.parseInt(cur.regArr[sreg1], 2);
        tempB = Integer.parseInt(cur.regArr[sreg2], 2);

        switch (opcode)
        {
            case"000100": // 4 in hex, Transfers the content of one register into another
                cur.regArr[sreg1] =  cur.regArr[sreg2];
                //System.out.println("arithmetic MOV Register " + sreg2 + " into Register " + sreg1+ " " + cur.regArr[sreg1] + " " + cur.regArr[sreg2]);
                break;
            case "000101":
                temp = tempA + tempB;
                cur.regArr[dreg] = Integer.toBinaryString(temp);
                //System.out.println("arithmetic ADD: Register " + sreg1 + " " + sreg2 + " " + dreg + " " + cur.regArr[dreg]);
                break;
            case "000110":
                temp = tempA - tempB;
                cur.regArr[dreg] = Integer.toBinaryString(temp);
                //System.out.println("arithmetic SUB: Register " + dreg + " " + cur.regArr[dreg]);
                break;
            case "000111":
                temp = tempA * tempB;
                cur.regArr[dreg] = Integer.toBinaryString(temp);
                //System.out.println("arithmetic MUL: Register " + dreg + " " + cur.regArr[dreg]);
                break;
            case "001000": // 8 in hex
                int num = tempA / tempB;
                cur.regArr[dreg] = Integer.toBinaryString(num);
                //System.out.println("DIV "+ cur.regArr[sreg1] + " " + cur.regArr[sreg2] + " " + cur.regArr[dreg]);
                break;
            case "001001":
                temp = (tempA & tempB);
                //System.out.println("AND: " + temp);
                break;
            case "001010":
                temp = (tempA | tempB);
                //System.out.println("OR: "+ temp);
                break;
            case "010000": // 10 in hex, Sets the D-reg to 1 if  first S-reg is less than second B-reg, and 0 otherwise
                if (tempA < tempB)
                    cur.regArr[dreg] = "1"; //regArr[Integer.parseInt(Param3, 2)] = "1";
                else
                    cur.regArr[dreg] = "0"; //regArr[Integer.parseInt(Param3, 2)] = "0";
                //System.out.println("SLT: Register " + dreg + " " + cur.regArr[dreg]);
                break;
            case "010001": // 11 in hex
                    System.out.println("SLTI PUT IT CODE HERE");
                break;
            default:
                System.out.println("Invalid opcode");
        }
    }
 
        
    private void CondBranchImFormat(String bregg, String dregg, String address, String OPCODE) throws InterruptedException
    {
        int daddress = convertAddress(address);

        int temp = Integer.parseInt(bregg, 2);
        String temp2 = cur.regArr[temp];

        int breg = Integer.parseInt(bregg, 2);
        int dreg = Integer.parseInt(dregg, 2);

        String bData = cur.regArr[breg];
        String dData = cur.regArr[dreg];

        int bInt = Integer.parseInt(bData, 2);
        int dInt = Integer.parseInt(dData, 2);

        switch (OPCODE)
        {
            case "000010": // if 02, Stores content of a reg.  into an address ???????????
                int addr = dInt/4;
                String data = Integer.toHexString(bInt);
                SetCache(addr, data);
                if(!cur.PagFault)
                {
                    ChangedCFrames.add(addr);                 
                    //System.out.println("ST: address " + addr + " " + cur.regArr[dreg]);
                }else
                {
                    TypeFault="Fault";
                    cur.TFault=TypeFault;
                    break;
                }
                break;
            case "000011": // if 03, Loads the content of an address into a reg.
                String contents = Integer.toBinaryString(Integer.parseInt(Fetch(bInt/4), 16));
                cur.regArr[dreg] = contents;
                //System.out.println("LW: Register " + dreg + " " + cur.regArr[dreg]);
                break;
            case "001011": // if opcode is 0B (in hex), Transfers address/data directly into a register
                cur.regArr[dreg] = address;
                //System.out.println("job " + cur.getJobID()  + " MOVI: Register " + dreg + " " + address);
                break;
            case "001100": // 0C in hex,
                int a = Integer.parseInt(cur.regArr[dreg], 2);
                int b = Integer.parseInt(address, 2);
                int c = a + b;
                cur.regArr[dreg] = Integer.toBinaryString(c);
                //System.out.println("job " + cur.getJobID() + " ADDI: Register " + dreg + " " + cur.regArr[dreg]);
                break;
            case "001111": // 0F in hex, Loads a data/address directly to the content of a register
                cur.regArr[dreg] = address;
                //System.out.println("LDI: Register " + dreg + " " + address);
                break;
            case "010101": // 15 in hex, Branches to an address when content of B-reg = D-reg
                if (bInt == dInt)
                {
                    cur.PC = daddress;
                    
                }
                //System.out.println("BEQ: Register " + breg + " " + dreg);
                break;
            case "010110": // 16 in hex, Branches to an address when content of B-reg <> D-reg
                if (bInt != dInt)
                {
                    cur.PC = daddress;
                    
                }
                //System.out.println("BNE: Register " + breg + " " + dreg + " data " + bInt + " " + dInt + " " + daddress);
                break;
            default:
                System.out.println("Invalid opcode");
        }
    }

    public void UnconditionalJump(String opc) throws InterruptedException
    {
        switch (opc)
        {
            case "010010":
                cur.IR = "HLT";
                //cur.PC = 0;
                atomicUpdate();
                System.out.println("End of Job# " + cur.getJobID());
                break;
            default:
                System.out.println("Invalid opcode for unconditional jump");
        }
    }

    //Method to convert String Hex characters into String bit characters
    private String HexToBinaryString(String Hex)
    {
        String Hex2 = Hex.replaceAll("\\s","");
        String InstHex = "";
        for (int i=0; i<Hex2.length(); i++)
        {
            switch (Hex2.charAt(i)){
                case '0':
                    InstHex=InstHex+"0000";
                    break;
                case '1':
                    InstHex=InstHex+"0001";
                    break;
                case '2':
                    InstHex=InstHex+"0010";
                    break;
                case '3':
                    InstHex=InstHex+"0011";
                    break;
                case '4':
                    InstHex=InstHex+"0100";
                    break;
                case '5':
                    InstHex=InstHex+"0101";
                    break;
                case '6':
                    InstHex=InstHex+"0110";
                    break;
                case '7':
                    InstHex=InstHex+"0111";
                    break;
                case '8':
                    InstHex=InstHex+"1000";
                    break;
                case '9':
                    InstHex=InstHex+"1001";
                    break;
                case 'A':
                    InstHex=InstHex+"1010";
                    break;
                case 'B':
                    InstHex=InstHex+"1011";
                    break;
                case 'C':
                    InstHex=InstHex+"1100";
                    break;
                case 'D':
                    InstHex=InstHex+"1101";
                    break;
                case 'E':
                    InstHex=InstHex+"1110";
                    break;
                case 'F':
                    InstHex=InstHex+"1111";
                    break;
                default:
                    System.out.println("Invalid Hex Data");
                    break;
            }
        }
        return InstHex;
    }

    private int convertAddress(String address)
    {
        return (Integer.parseInt(address, 2) / 4);
    }
    
    public void atomicUpdate() throws InterruptedException 
    {
        Page tempPage;
        Frame tempFrame;
        
        Backup();
        
        for(int i=0; i<cur.getJobPageSize(); i++)
        {
            tempPage=currentPT.getPage(cur.getJobPage(i));
            if(tempPage.getinRam())
            {
                tempFrame=currentFT.getFrame(currentPT.getPage(cur.getJobPage(i)).getequFrame());
                mem.WriteFrameToPage(tempFrame, tempPage);
                currentFT.releaseFrame(tempFrame);
            }
        }
        
        int identifier=0;
       
        for(int i=0; i<dequeRunning.size(); i++)
        {
            if(dequeRunning.get(i).getJobID()==cur.getJobID())
            {
                identifier=i;
                break;
            }
        }
        cur.setStatus("finished");
        dequeFinished.addLast(cur);
        dequeRunning.remove(identifier);
//      //(new Thread(new ServicingFault(dr))).start();
        ServicingFault SF = new ServicingFault(dr);
        SF.run();
    }

}
