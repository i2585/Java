package chapter3;

import java.io.*;
import java.util.Scanner;

public class ex_03_04 {
	public static void main(String args[]) throws IOException{
		Scanner sc = new Scanner(System.in);
		FileWriter fw = new FileWriter("example4_1.txt");
		System.out.println("���� ������ �Է��ϼ���.");
		String text = sc.nextLine();
		fw.write(text,0,text.length());
		fw.flush();
		int numberRead;
		char[] buffer = new char[80];
		FileReader fr = new FileReader("example4_1.txt");
		System.out.print("���� ����: ");
		while((numberRead=fr.read(buffer)) >- 1)
			System.out.println(buffer);
		fw.close();		
		fr.close();
	}
}
