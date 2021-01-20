package chapter7;

import java.io.*;
import java.net.*;

public class ex_07_01_sever {
	public static void main(String args[]) {
		ServerSocket theServer;
		Socket theSocket = null;
		InputStream is;
		BufferedReader reader;
		OutputStream os;
		BufferedWriter writer;
		String theLine, theresult;
		try {
			theServer = new ServerSocket(7);
			theSocket = theServer.accept();
			is = theSocket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is));
			os = theSocket.getOutputStream();
			writer = new BufferedWriter(new OutputStreamWriter(os));
			while((theLine = reader.readLine()) != null) {
				System.out.println(theLine);
				theresult = match(theLine);
				writer.write(theresult+'\r'+'\n');
				writer.flush();
			}
		}catch(UnknownHostException e) {
			System.err.println(e);
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
	public static String match(String s) {
		String data,result=null;
		String[] dictionary = new String[10]; 
		int i=0;
		try {
			FileReader reader = new FileReader("dictionary.txt");
			BufferedReader br = new BufferedReader(reader);
			while((data=br.readLine())!=null) {
				dictionary[i]=data;
				if(data.equals(s)) {
					result=dictionary[i-1];
					break;
				}	
				else {
					result="해당 단어가 없습니다.";
					i++;
				}
			}
		}catch(IOException e)
		{
			System.err.println(e);
		}
		return result;
	}
}
