package chapter1;
import java.io.*;
import java.util.*;
 
public class ex_01_01
{
    public static void main(String[] args) throws IOException
    { 
        FileInputStream fin = null;
        FileInputStream fin1 = null;
        try
        {
        	System.out.println("파일 이름 : " + args[0]);
        	System.out.print("파일 내용 : ");
        	int byteRead;
        	byte[] buffer = new byte[256];
        	fin = new FileInputStream(args[0]);
        	while((byteRead = fin.read(buffer)) >= 0)
        		System.out.write(buffer,0,byteRead);
        	System.out.println();
        	System.out.println();
        	
        	System.out.println("파일 이름 : " + args[1]);
        	System.out.print("파일 내용 : ");
        	fin1 = new FileInputStream(args[1]);
        	while((byteRead = fin1.read(buffer)) >= 0)
        		System.out.write(buffer,0,byteRead);
            System.out.println();
        }
        catch(IOException e)
        {
        	System.out.println(e.toString());
        }
        finally
        {
        	try {
            	if(fin != null)  fin.close();
            	if(fin1 != null)  fin1.close();
        	}catch(IOException e) {}
        }
     }
}