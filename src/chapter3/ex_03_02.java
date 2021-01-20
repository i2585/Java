package chapter3;

import java.io.*;
import  java.nio.channels.FileChannel;

public class ex_03_02
{
	public static void main(String[] args) throws IOException
	{
		long fsize;
		int input,count=0;
		double per;
		byte[] date = new byte[1024];
		double persent;
		RandomAccessFile raf, raf1;
		raf = new RandomAccessFile("ex_02.jpg", "r");
		raf1 = new RandomAccessFile("copy.jpg","rw");

		while((input=raf.read(date))!=-1) {
			raf1.write(date, 0, input);
			count += input;
			persent = ((double) count/raf.length())*100;
			if((int)persent%10 == 0 && (int)persent!=0) {
				System.out.println((int)persent + "% *");
			}
		}
		raf.close();
		raf1.close();
	}
}