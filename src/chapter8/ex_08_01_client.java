package chapter8;

import java.io.*;
import java.net.*;
public class ex_08_01_client {
	public static void main(String args[]) {
		Socket theSocket=null;
		String host;
		InputStream is;
		BufferedReader reader, userInput;
		OutputStream os;
		BufferedWriter writer;
		String theLine;
		if(args.length>0) {
			host=args[0];
		}
		else {
			host="localhost";
		}
		try {
			theSocket = new Socket(host,7);
			is = theSocket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is));
			userInput = new BufferedReader(new InputStreamReader(System.in));
			os = theSocket.getOutputStream();
			writer = new BufferedWriter(new OutputStreamWriter(os));
			System.out.println("전송할 단어를 입력하십시오.");
			while((theLine=userInput.readLine())!=null) {
				writer.write(theLine+'\r'+'\n');
				writer.flush();
				System.out.println(reader.readLine());
			}
		}catch(UnknownHostException e) {
			System.err.println(args[0]+"호스트를 찾을 수 없습니다.");
		}catch(IOException e) {
			System.err.println(e);
		}finally {
			if(theSocket != null) {
				try {
					theSocket.close();
					System.out.println("프로그램이 종료되었습니다.");
				}catch(IOException e) {
					System.err.println(e);
				}
			}
		}
	}
}
