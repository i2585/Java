package chapter7;

import java.io.*;
import java.net.*;
import java.util.*;
public class ex_07_02_server {
	public static void main(String args[]) {
		ServerSocket theServer;
		Socket theSocket = null;
		BufferedWriter writer;
		BufferedReader reader,  userInput;
		String theLine, Message;
		try {
			theServer = new ServerSocket(9999);
			System.out.println("연결을 기다리고 있습니다....");
			while(true) {
				try {
					theSocket = theServer.accept();
					OutputStream os = theSocket.getOutputStream();
					writer = new BufferedWriter(new OutputStreamWriter(os));
					InputStream is = theSocket.getInputStream();
					reader = new BufferedReader(new InputStreamReader(is));
					userInput = new BufferedReader(new InputStreamReader(System.in));
					System.out.println("연결되었습니다.");
					while((theLine=reader.readLine())!=null) {
						if(theLine.equals("bye")) {
							theSocket.close();
							System.out.println("클라이언트에서 bye로 연결을 종료하였음");
							break;
						}
						System.out.println("클라이언트: " + theLine);
						System.out.print("보내기>> ");
						Message=userInput.readLine();
						writer.write(Message+"\n");
						writer.flush();
					}
				}catch(IOException e) {
					System.out.println(e);
				}finally {
					try {
						if(theSocket!=null) theSocket.close();
					}catch(IOException e) {
						System.out.println(e);
					}
				}
			}
		}catch(IOException e) {
			System.out.println(e);
		}
	}
}
