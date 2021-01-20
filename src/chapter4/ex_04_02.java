package chapter4;

import java.io.*;
import java.util.*;
public class ex_04_02 {
	public static void main(String args[]) {
		System.out.println("문자를 입력하세요");
	    BufferedReader br = null;
	    BufferedWriter pw = null;
	    FileReader rw=null;	
	    int num=1;
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			pw = new BufferedWriter(new FileWriter("chapter4-2.txt"));
			
			String text = null;
			while((text=br.readLine())!=null) {
				pw.write(num + ": " + text + "\n");
				num++;
			}
		}catch(IOException e) {
			System.out.println(e);
		}
		finally {
			try {
				pw.close();
			}catch(Exception e) {}
		}
		try {
			rw = new FileReader("chapter4-2.txt");
			br=new BufferedReader(rw);
			String read =null;
			while((read=br.readLine()) != null)
				System.out.println(read);	
		}catch(IOException e) {
			System.out.println(e);
		}
	}
}