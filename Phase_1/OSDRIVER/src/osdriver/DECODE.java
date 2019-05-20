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
public class DECODE {
    
    private String InstHex;
    private String Function;
    private String OPCODE;
    private String Param1;
    private String Param2;
    private String Param3;
    
    
    public void DECODE()
    {
        InstHex = null;
        Function = null;
        OPCODE = null;
        Param1 = null;
        Param2 =null;
        Param3 = null;
    }
    
    public void resetDECODE()
    {
        InstHex = null;
        Function = null;
        OPCODE = null;
        Param1 = null;
        Param2 =null;
        Param3 = null;
    }
    
    //Use this method to send the instruction as a string of "Hexs" characters to Decode.
    public void DecodeInstruction(String newInstHex)
    {
        String TempParameters;
                
        //Method to convert String Hex characters into String bit characters
        HexToBinaryString(newInstHex);
        
        //Removing the null
        InstHex = InstHex.substring(4);
        
        //Getting the fucntion
        Function = InstHex.substring(0,2);
                
        //Getting the OPCODE
        OPCODE = InstHex.substring(2,8);
                
        TempParameters = InstHex.substring(8);
        
        //Switch fuction to get the parameters according to the Function Type.
        switch (Function) {
    
        case "00": 
            Param1 = TempParameters.substring(0,4);
            Param2 = TempParameters.substring(4,8);
            Param3 = TempParameters.substring(8,12);
            break;

        case "01":
            Param1 = TempParameters.substring(0,4);
            Param2 = TempParameters.substring(4,8);
            Param3 = TempParameters.substring(8);
            break;

        case "10":
            Param1 = TempParameters;
            Param2 = null;
            Param3 = null;
            break;

        case "11":
            Param1 = TempParameters.substring(0,4);
            Param2 = TempParameters.substring(4,8);
            Param3 = TempParameters.substring(8);
            break;

        default:
            System.out.println("Invalid Function");
            break;
        }
      
    }
    
    //Method to convert String Hex characters into String bit characters
    private void HexToBinaryString(String Hex)
    {
        
        for (int i=0; i<Hex.length(); i++)
        {
            switch (Hex.charAt(i)){
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
    }
    
    //Get Methods
    
    public String getFunction()
    {
        return Function;
    }
    
    public String getOPCODE()
    {
        return OPCODE;
    }
    
    public String getParam1()
    {
        return Param1;
    }
    
    public String getParam2()
    {
        return Param2;
    }
    
    public String getParam3()
    {
        return Param3;
    }
    
}
