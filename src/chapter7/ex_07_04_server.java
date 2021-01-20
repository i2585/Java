package chapter7;

import java.io.*;
import java.net.*;

public class ex_07_04_server {
	public final static int port=10;
	public static void main(String args[]) {
		ServerSocket theServer;
		Socket theSocket = null;
		try {
			theServer = new ServerSocket(port);
			theSocket = theServer.accept();
		}catch(IOException e) {
			System.out.println(e);
		}
	}
}
