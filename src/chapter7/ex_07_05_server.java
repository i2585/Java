package chapter7;

import java.io.*;
import java.net.*;
import java.util.*;
public class ex_07_05_server {
	public static void main(String args[]) {
		int port;
		String encoding = "ASCII";
		try {
			port = Integer.parseInt(args[0]);
			if(port<1 || port>65535)
				port=80;
		}catch(Exception e) {
			port = 80;
		}
		try {
			ServerSocket server = new ServerSocket(port);
			while(true) {
				Socket connection = null;
				FileDownload client = null;
				try {
					connection = server.accept();
					client = new FileDownload(connection, encoding, port);
					client.start();
				}catch(IOException e) {
					System.out.println(e);
				}
			}
		}catch(Exception e) {
			System.out.println(e);
		}
	}
}
class FileDownload extends Thread{
	private byte[] content;
	private byte[] header;
	private byte[] data;
	private int port;
	Socket connection;
	BufferedOutputStream out;
	BufferedInputStream in;
	FileInputStream inf;
	ByteArrayOutputStream bout;
	String filename;
	String contenttype;
	int b;
	public FileDownload(Socket connection, String encoding, int port) throws UnsupportedEncodingException{
		this.connection = connection;
		this.port = port;
	}
	public void run() {
		try {
			out = new BufferedOutputStream(connection.getOutputStream());
			in = new BufferedInputStream(connection.getInputStream());
			StringBuffer request = new StringBuffer(80);
			while(true) {
				int c = in.read();
				if(c=='\r' || c=='\n' || c==-1)
					break;
				request.append((char)c);
			}
			filename=request.substring(request.indexOf("/")+1,request.lastIndexOf(" "));
			if(filename.endsWith(".html") || filename.endsWith(".htm")) {
				contenttype = "text/plain";
			}
			inf = new FileInputStream(filename);
			bout = new ByteArrayOutputStream();
			while((b=inf.read())!=-1)
				bout.write(b);
			data = bout.toByteArray();
			this.content = data;
			System.out.println(request.toString());
			System.out.println(filename);
			String header = "HTTP 1.0 200 OK\r\n" + "Server: OneFile 1.0\r\n" + "Content-length:" + this.content.length+"\r\n"+"Content-type: " + contenttype+"\r\n\r\n";
			this.header = header.getBytes("ASCII");
			if(request.toString().indexOf("HTTP/") != -1) {
				out.write(this.header);
			}
			out.write(this.content);
			out.flush();
		}catch(IOException e) {
		}finally {
			try {
				if(connection != null)
					connection.close();
			}catch(IOException e) {
				System.out.println(e);
			}
		}
	}
}