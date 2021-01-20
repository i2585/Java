package chapter7;

import java.io.*;
import java.net.*;

public class ex_07_03_server{
	public static void main(String args[]) {
		ServerSocket theServer=null;
		Socket theSocket = null;
		Threads t1 = null;
		InetSocketAddress Inet = null;
		try {
			theServer = new ServerSocket();
			Inet = new InetSocketAddress(7);
			theServer.setReuseAddress(true);
			theServer.bind(Inet);
			while(true) {
				try {
					theSocket = theServer.accept();
					t1 = new Threads(theSocket);
					t1.start();
				}catch(IOException e) {
					System.err.println(e);
				}
			}
		}catch(IOException e) {
			System.err.println(e);
		}
	}
}
class Threads extends Thread{
	Socket connection;
	InputStream is;
	BufferedReader reader;
	String theLine;
	public Threads(Socket theSocket) {
		connection=theSocket;
	}
	public void run() {
		try {
			System.out.println("client�� �����Ͽ����ϴ�.");
			is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is));
			while((theLine = reader.readLine()) != null) {
				System.out.println(theLine);
			}	
		}catch(UnknownHostException e) {
			System.err.println(e);
		}catch(IOException e) {
			System.err.println(e);
		}finally {
			if(connection != null) {
				try {
					connection.close();
					System.out.println("client���� ������ ����Ǿ����ϴ�.");
				}catch(IOException e) {
					System.err.println(e);
				}	
			}
		}
	}
}
