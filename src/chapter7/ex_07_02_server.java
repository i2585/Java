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
			System.out.println("������ ��ٸ��� �ֽ��ϴ�....");
			while(true) {
				try {
					theSocket = theServer.accept();
					OutputStream os = theSocket.getOutputStream();
					writer = new BufferedWriter(new OutputStreamWriter(os));
					InputStream is = theSocket.getInputStream();
					reader = new BufferedReader(new InputStreamReader(is));
					userInput = new BufferedReader(new InputStreamReader(System.in));
					System.out.println("����Ǿ����ϴ�.");
					while((theLine=reader.readLine())!=null) {
						if(theLine.equals("bye")) {
							theSocket.close();
							System.out.println("Ŭ���̾�Ʈ���� bye�� ������ �����Ͽ���");
							break;
						}
						System.out.println("Ŭ���̾�Ʈ: " + theLine);
						System.out.print("������>> ");
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
