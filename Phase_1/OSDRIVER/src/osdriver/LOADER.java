/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osdriver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author CARLOSANDRES
 */
public class LOADER {
    
    private int jobID = -1;
    private int jobSize = -1;
    private int jobPriority = -1;
    private int inputBufferSize=-1;
    private int outputBufferSize=-1;
    private int tempBufferSize=-1;
    private int TempStartingAddress=0;
    private int TempEndAddress=0;
    private int TotalSize=0;

    
    public void runLOADER(MEMORY M1, String addressFile, LinkedList<PCB> pcbList, LinkedList<JOBLIST> jobListDisk)
    {
        
        try 
        {
            BufferedReader br = new BufferedReader(new FileReader(addressFile));
            String line; 
            while ((line = br.readLine()) != null)
            {
                if (line.contains("// JOB")) // If JOB, store in disk
                {
                    // Logic to convert job ID from hexadecimal to decimal.
                    int nextSpace = line.indexOf(' ', 7);
                    String tempID = line.substring(7, nextSpace);
                    jobID = Integer.parseInt(tempID,16);
                    line = line.substring(nextSpace + 1);

                    // Converts and store job size as decimal value
                    nextSpace = line.indexOf(' ');
                    String tempSize = line.substring(0,nextSpace);
                    jobSize = Integer.parseInt(tempSize,16);
                    line = line.substring(nextSpace +1);

                    // Converts and stores job priority as decimal value
                    jobPriority = Integer.parseInt(line,16);

                    // Read next line after the control card
                    for (int i = 0; i < jobSize; i++)
                    {
                        // Go to next line
                        line = br.readLine().substring(2);

                        // Store instructions in disk as 8-char HEX values
                        M1.wMEMORY(0, line);
                    }
                }

                // If DATA control card, do this
                if (line.contains("// Data"))
                {
                    // Stores & convert data for Input Buffer
                    int nextSpace = line.indexOf(' ', 8);
                    String tempInputBuffer = line.substring(8,nextSpace);
                    inputBufferSize = Integer.parseInt(tempInputBuffer, 16); // Converted to HEX
                    line = line.substring(nextSpace + 1);

                    // Stores & convert data for Output Buffer Size
                    nextSpace = line.indexOf(' ');
                    String tempOutputBufferSize = line.substring(0, nextSpace);
                    outputBufferSize = Integer.parseInt(tempOutputBufferSize, 16);
                    line = line.substring(nextSpace +1);

                    // Stores & convert size of temp buffer
                    tempBufferSize = Integer.parseInt(line, 16);

                    // Read next line after the control card
                    for (int i = 0; i < (inputBufferSize + outputBufferSize + tempBufferSize); i++)
                    {
                        // Go to next line
                        line = br.readLine().substring(2);

                        // Store instructions in disk as 8-char HEX values
                        M1.wMEMORY(0, line);
                    }
                    
                    // LOGIC TO ADD DATA PROGRAM ATTRIBUTES TO PCB GOES HERE
                    TotalSize=jobSize+inputBufferSize+outputBufferSize+tempBufferSize;
                    TempEndAddress=TempStartingAddress+TotalSize-1;

                    PCB currentPCB = new PCB();
                    JOBLIST currentJOBLIST = new JOBLIST();
                    pcbList.add(currentPCB);
                    jobListDisk.add(currentJOBLIST);
                    pcbList.getLast().loaderPCB (jobID, jobSize, jobPriority, inputBufferSize, outputBufferSize, tempBufferSize, TempStartingAddress, TempEndAddress);
                    jobListDisk.getLast().loaderJOBLIST (jobID, jobPriority, TotalSize);
                    
                    TempStartingAddress=TempStartingAddress+TotalSize;
                }
            }
            br.close();
        }
        catch (IOException ex)
        {
            System.out.println("File Not Found!");
        }

    }
}