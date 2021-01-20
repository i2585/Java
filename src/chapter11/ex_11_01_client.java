package chapter11;

import java.io.*;
import java.net.*;
import java.util.*;

public class ex_11_01_client {
	public static final int port = 7;
	public static void main(String args[]){
		String hostname = "localhost";
		if(args.length>0) {
			hostname=args[0];
		}
		try {
			InetAddress ia = InetAddress.getByName(hostname);
			DatagramSocket theSocket = new DatagramSocket();
			Sender send = new Sender(ia, port, theSocket);
			send.start();
			Receiver receive = new Receiver(theSocket);
			receive.start();
		}catch(UnknownHostException e) {
			System.out.println(e);
		}catch(SocketException se) {
			System.out.println(se);
		}
	}
}

class Sender extends Thread{
	InetAddress server;
	int port;
	DatagramSocket theSocket;
	public Sender(InetAddress ia, int port, DatagramSocket ds) {
		server=ia;
		this.port=port;
		theSocket=ds;
	}
	public void run() {
		BufferedReader input;
		String theLine;
		DatagramPacket outgoing;
		try {
			input = new BufferedReader(new InputStreamReader(System.in));
			while((theLine=input.readLine()) != null) {
				byte data[] = theLine.getBytes();
				outgoing = new DatagramPacket(data, data.length, server, port);
				theSocket.send(outgoing);
				Thread.yield();
			}
		}catch(IOException e) {
			System.out.println(e);
		}
	}
}

class Receiver extends Thread{
	protected DatagramPacket incoming;
	DatagramSocket theSocket;
	public Receiver(DatagramSocket ds) {
		theSocket = ds;
		byte[] buffer = new byte[65508];
		incoming = new DatagramPacket(buffer, buffer.length);
	}
	public void run() {
		while(true) {
			try {
				theSocket.receive(incoming);
				String recData = new String(incoming.getData(), 0, incoming.getLength());
				System.out.println(recData);
				Thread.yield();
			}catch(IOException e) {
				System.out.println(e);
			}
		}
	}
}