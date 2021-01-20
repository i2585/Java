package chapter9;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

public class ChatWhisperS extends Frame {
   TextArea display;
   Label info;
   List<ServerThread> list;
   Hashtable hash;
   public ServerThread SThread;
	
   public ChatWhisperS() {
      super("����");
      info = new Label();
      add(info, BorderLayout.CENTER);
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
      ServerThread SThread;
      try {
         server = new ServerSocket(5000, 100);
         hash = new Hashtable();
         list = new ArrayList<ServerThread>();
         try {
            while(true) {
               sock = server.accept();
               SThread = new ServerThread(this, sock, display, info);
               SThread.start();
               info.setText(sock.getInetAddress().getHostName() + " ������ Ŭ���̾�Ʈ�� �����");
            }
         } catch(IOException ioe) {
            server.close();
            ioe.printStackTrace();
         }
      } catch(IOException ioe) {
         ioe.printStackTrace();
      }
			
   }

   public static void main(String args[]) {
      ChatWhisperS s = new ChatWhisperS();
      s.runServer();
   }
		
   class WinListener extends WindowAdapter {
      public void windowClosing(WindowEvent e) {
         System.exit(0);
      }
   }
}

class ServerThread extends Thread {
   Socket sock;
   BufferedWriter output;
   BufferedReader input;
   TextArea display;
   Label info;
   TextField text;
   String clientdata;
   String serverdata = "";
   ChatWhisperS cs;
	
   private static final String SEPARATOR = "|";
   private static final int REQ_LOGON = 1001;
   private static final int REQ_SENDWORDS = 1021;
   private static final int REQ_WISPERSEND = 1022;
   private static final int REQ_LOGOUT = 1031;
   
   public ServerThread(ChatWhisperS c, Socket s, TextArea ta, Label l) {
      sock = s;
      display = ta;
      info = l;
      cs = c;
      try {
         input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
         output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
      } catch(IOException ioe) {
         ioe.printStackTrace();
      }
   }
   public void run() {
      try {
         while((clientdata = input.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
            int command = Integer.parseInt(st.nextToken());
            int Lcnt = cs.list.size();
            switch(command) {
               case REQ_LOGON : {
                  String ID = st.nextToken();
                  if(cs.hash.containsKey(ID)) {
                	  display.append(ID + "�� �̹� �α��� �Ǿ��ֽ��ϴ�.\r\n");
                	  output.write(ID + "�� �̹� �α��� �Ǿ��ֽ��ϴ�.\r\n");
                	  output.flush();
                  }
                  else {
                	  cs.list.add(this);
                	  display.append("Ŭ���̾�Ʈ�� " + ID + "(��)�� �α��� �Ͽ����ϴ�.\r\n");  
                	  output.write("�α��� �Ǿ����ϴ�!!\r\n");
                	  output.flush();
                	  cs.hash.put(ID, this); // �ؽ� ���̺� ���̵�� �����带 �����Ѵ�
                  }
                  break;
               }
               case REQ_LOGOUT : {
            	   cs.list.remove(this);
            	   String ID = st.nextToken();
            	   cs.hash.remove(ID);
            	   display.append("Ŭ���̾�Ʈ " + ID + "�� �α׾ƿ� �Ͽ����ϴ�.\r\n");
            	   break;
               }
               case REQ_SENDWORDS : {
                  String ID = st.nextToken();
                  String message = st.nextToken();
                  display.append(ID + " : " + message + "\r\n");
                  for(int i=0; i<Lcnt; i++) {
                     ServerThread SThread = (ServerThread)cs.list.get(i);
                     SThread.output.write(ID + " : " + message + "\r\n");
                     SThread.output.flush();
                  }
                  break;
               }
               case REQ_WISPERSEND : {
                  String ID = st.nextToken();
                  String WID = st.nextToken();
                  String message = st.nextToken();
                  try {
                	  display.append(ID + " -> " + WID + " : " + message + "\r\n");
                	  ServerThread SThread = (ServerThread)cs.hash.get(ID);
                	  // �ؽ����̺��� �ӼӸ� �޽����� ������ Ŭ���̾�Ʈ�� �����带 ����
                	  SThread.output.write(ID + " -> " + WID + " : " + message + "\r\n");
                	  // �ӼӸ� �޽����� ������ Ŭ���̾�Ʈ�� ������
                	  SThread.output.flush();
                	  SThread = (ServerThread)cs.hash.get(WID);
                	  // �ؽ����̺��� �ӼӸ� �޽����� ������ Ŭ���̾�Ʈ�� �����带 ����
                	  SThread.output.write(ID + " : " + message + "\r\n");
                	  // �ӼӸ� �޽����� ������ Ŭ���̾�Ʈ�� ������
                	  SThread.output.flush();
                	  break;
                  }catch(Exception a) {
                	  display.append("����� " + WID + "�� �������� �ʽ��ϴ�.\r\n");
                	  output.write("����� " + WID + "�� �������� �ʽ��ϴ�.\r\n");
                	  output.flush();
                  }
               }
            }
         }
      } catch(IOException e) {
         e.printStackTrace();
      }
      cs.list.remove(this);
      try{
         sock.close();
      }catch(IOException ea){
         ea.printStackTrace();
      }
   }
}