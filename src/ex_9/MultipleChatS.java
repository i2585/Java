package ex_9;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

public class MultipleChatS extends Frame {
   TextArea display;
   Label info;
   String clientdata = "";
   String serverdata = "";
   List<ServerThread> list;
   public ServerThread SThread;
   
   public MultipleChatS() {
      super("����");
    //info = new Label();
    //add(info, BorderLayout.CENTER);
      display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
      display.setEditable(false);
      add(display, BorderLayout.SOUTH);
      addWindowListener(new WinListener());
      setSize(300,250);
      setVisible(true);
   }
	
   public void runServer() {
      ServerSocket server;
      Socket sock;
      try {
         list = new ArrayList<ServerThread>();
         server = new ServerSocket(5000, 100);
         try {
            while(true) {
               sock = server.accept();
               SThread = new ServerThread(this, sock, display, serverdata);
               SThread.start();
             //info.setText(sock.getInetAddress().getHostName() + " ������ Ŭ���̾�Ʈ�� �����");
              display.append("������ " + sock.getInetAddress().getHostName() + ":" + sock.getPort() + "Ŭ���̾�Ʈ�� �����\r\n");
              int cnt = list.size();
              for(int i=0; i<cnt; i++) { //��� Ŭ���̾�Ʈ�� �����͸� �����Ѵ�.
                 SThread = (ServerThread)list.get(i);
                 SThread.output.write("Ŭ���̾�Ʈ" + sock.getInetAddress().getHostName() + ":" + sock.getPort() + "�� �����\r\n");
                 SThread.output.flush();
              }
            }
         }catch(IOException oe) {
            server.close();
            oe.printStackTrace();
         }
      } catch(IOException ioe) {
         ioe.printStackTrace();
      }
   }
		
   public static void main(String args[]) {
      MultipleChatS s = new MultipleChatS();
      s.runServer();
   }
	
   class WinListener extends WindowAdapter {
      public void windowClosing(WindowEvent e) {
         System.exit(0);
      }
   }
   class ServerThread extends Thread {
	   Socket sock;
	   InputStream is;
	   InputStreamReader isr;
	   BufferedReader input;
	   OutputStream os;
	   OutputStreamWriter osw;
	   BufferedWriter output;
	   TextArea display;
	   Label info;
	   TextField text;
	   String serverdata = "";
	   MultipleChatS cs;
	
	   public ServerThread(MultipleChatS c, Socket s, TextArea ta, String data) {
		   sock = s;
		   display = ta;
		   //info = l;
		   serverdata = data;
		   cs = c;
		   try {
			   is = sock.getInputStream();
			   isr = new InputStreamReader(is);       
			   input = new BufferedReader(isr);
			   os = sock.getOutputStream();
			   osw = new OutputStreamWriter(os);
			   output = new BufferedWriter(osw);
		   } catch(IOException ioe) {
			   ioe.printStackTrace();
		   }
	   }
	   public void run() {
		   cs.list.add(this);
		   String clientdata;
		   try {
			   while((clientdata = input.readLine()) != null) {
				   display.append(clientdata + "\r\n");
				   int cnt = cs.list.size();
				   for(int i=0; i<cnt; i++) { //��� Ŭ���̾�Ʈ�� �����͸� �����Ѵ�.
					   ServerThread SThread = (ServerThread)cs.list.get(i);
					   SThread.output.write(clientdata + "\r\n");
					   SThread.output.flush();
				   }
			   }
			   String client_end = "Ŭ���̾�Ʈ" + sock.getInetAddress().getHostName()+":" + sock.getPort() + "���� ������ �����\r\n";
			   int cnt = cs.list.size();
			   for(int i=0; i<cnt; i++) { //��� Ŭ���̾�Ʈ�� �����͸� �����Ѵ�.
				   ServerThread SThread = (ServerThread)cs.list.get(i);
				   SThread.output.write(clientdata + "\r\n");
				   SThread.output.flush();
			   }
		   } catch(IOException e) {
			   e.printStackTrace();
		   }
		   cs.list.remove(this); //����Ʈ���� close�� Ŭ���̾�Ʈ�� �����.
		   try{
			   sock.close();   //���ϴݱ�
		   }catch(IOException ea){
			   ea.printStackTrace();
		   }
	   }
   }
}