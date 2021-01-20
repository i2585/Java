package chapter8;

import java.io.*;
import java.net.*;
import java.util.*;
public class ex_08_02_client {
	public static void main(String args[]) {
		Socket theSocket=null;
		String host;
		BufferedWriter writer;
		BufferedReader reader, userInput;
		String theLine, Message;
		if(args.length>0) {
			host=args[0];
		}
		else
			host="localhost";
		try {
			theSocket = new Socket(host, 9999);
			InputStream is = theSocket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is));
			userInput = new BufferedReader(new InputStreamReader(System.in));
			OutputStream os = theSocket.getOutputStream();
			writer = new BufferedWriter(new OutputStreamWriter(os));
				while(true) {
					System.out.print("������>> ");
					Message=userInput.readLine();
					writer.write(Message+"\n");
					writer.flush();
					if(Message.equals("bye"))
						break;
					if((theLine=reader.readLine())!=null) {
						System.out.println("����: " + theLine);
					}
			}
		}catch(UnknownHostException e) {
			System.err.println(args[0]+" ȣ��Ʈ�� ã�� �� �����ϴ�.");
		}catch(IOException e) {
			System.out.println(e);
		}finally {
			try {
				theSocket.close();
			}catch(IOException e) {
				System.out.println(e);
			}
		}
	}
}
