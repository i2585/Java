package chapter11;

import java.net.*;
import java.io.*;
import java.util.*;
public class ex_11_01_server extends Thread{
	public static final int port = 7;
	public static final int BUFFER_SIZE = 8192;
	protected DatagramSocket ds;
	public ex_11_01_server() throws SocketException{
		ds = new DatagramSocket(port);
	}
	public void run() {
		byte[] buffer = new byte[BUFFER_SIZE];
		while(true) {
			DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
			try {
				ds.receive(incoming);
				String recData = new String(incoming.getData(), 0, incoming.getLength());
				System.out.println(recData);
				Date now = new Date();
				buffer=now.toString().getBytes();
				DatagramPacket outgoing = new DatagramPacket(buffer, buffer.length, incoming.getAddress(),incoming.getPort());
				ds.send(outgoing);
				incoming.setLength(buffer.length);
			}catch(IOException e) {
				System.out.println(e);
			}
		}
	}
	public static void main(String args[]){
		try {
			ex_11_01_server server = new ex_11_01_server();
			server.start();
		}catch(SocketException se) {
			System.out.println(se);
		}
	}
}
