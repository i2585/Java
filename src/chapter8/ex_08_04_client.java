package chapter8;

import java.io.*;
import java.net.*;
public class ex_08_04_client {
	public static void main(String[] args) {
		Socket theSocket;
		try {
		theSocket = new Socket("localhost",10);
		theSocket.setSendBufferSize(100);
		theSocket.setReceiveBufferSize(120);
		theSocket.setKeepAlive(true);
		theSocket.setTcpNoDelay(true);
		System.out.println("toString: " + theSocket.toString());
		System.out.println("SendBufferSize: " + theSocket.getSendBufferSize());
		System.out.println("ReceiveBufferSize: " + theSocket.getReceiveBufferSize());
		System.out.println("KeepAlive: " + theSocket.getKeepAlive());
		System.out.println("TcpNoDelay: " + theSocket.getTcpNoDelay());
		System.out.println("ReuseAddress: " + theSocket.getReuseAddress());
		}catch(IOException e) {
			System.out.println(e);
		}
	}
}
