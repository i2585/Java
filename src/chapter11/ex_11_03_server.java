package chapter11;

import java.io.*;
import java.net.*;
import java.util.*;

public class ex_11_03_server
{
	private char op;
	
	public static void main(String[] args) throws IOException
	{
		DatagramSocket ds = new DatagramSocket(4000); 
        byte[] buffer; 
        DatagramPacket ReceivePacket; 
        DatagramPacket SendPacket;
        
        while (true) 
        { 
        	buffer = new byte[1000]; 
        	Double result = 0.0;
        	int number;
        	int count = 0;
            ReceivePacket = new DatagramPacket(buffer, buffer.length); 
            ds.receive(ReceivePacket); 
  
            String data = new String(buffer, 0, buffer.length);
            System.out.println(data);
            
            StringTokenizer st = new StringTokenizer(data, " \0");
            while(st.hasMoreTokens() ){
            	String s = st.nextToken();
            	if (s.charAt(0) >= '0' && s.charAt(0) <= '9')
            		result = Double.parseDouble(s);
            	if (s.equals("+")) 
            		result = (double)(result) + (double)(number = Integer.parseInt(st.nextToken()));

            	else if (s.equals("-")) 
            		result = (double)(result) - (double)(number = Integer.parseInt(st.nextToken())); 

            	else if (s.equals("*")) 
            		result = (double)(result) * (double)(number = Integer.parseInt(st.nextToken())); 

            	else if (s.equals("/"))
            		result = (double)(result) / (double)(number = Integer.parseInt(st.nextToken()));
            }
            String res = Double.toString(result);
            System.out.println(res);
            buffer = res.getBytes(); 
  
            SendPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), ReceivePacket.getPort()); 
            ds.send(SendPacket);
        }
	}
}
