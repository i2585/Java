package chapter9;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class ChatWhisperC extends Frame implements ActionListener, KeyListener {
	
   TextArea display;
   TextField wtext, ltext;
   Label mlbl, wlbl, loglbl, logout;
   Button bt;
   BufferedWriter output;
   BufferedReader input;
   Socket client;
   StringBuffer clientdata;
   String serverdata;
   String ID;
   Panel plabel;
   private static final String SEPARATOR = "|";
   private static final int REQ_LOGON = 1001;
   private static final int REQ_SENDWORDS = 1021;
   private static final int REQ_WISPERSEND = 1022;
   private static final int REQ_LOGOUT = 1031;
   
   public ChatWhisperC() {
      super("Ŭ���̾�Ʈ");

      mlbl = new Label("ä�� ���¸� �����ݴϴ�.");
      add(mlbl, BorderLayout.NORTH);

      display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
      display.setEditable(false);
      add(display, BorderLayout.CENTER);

      Panel ptotal = new Panel(new BorderLayout());
 
      Panel pword = new Panel(new BorderLayout());
      wlbl = new Label("��ȭ��");
      wtext = new TextField(30); //������ �����͸� �Է��ϴ� �ʵ�
      wtext.addKeyListener(this); //�Էµ� �����͸� �۽��ϱ� ���� �̺�Ʈ ����
      pword.add(wlbl, BorderLayout.WEST);
      pword.add(wtext, BorderLayout.EAST);
      ptotal.add(pword, BorderLayout.CENTER);

      plabel = new Panel(new BorderLayout());
      loglbl = new Label("�α׿�");
      ltext = new TextField(30); //������ �����͸� �Է��ϴ� �ʵ�
      ltext.addActionListener(this); //�Էµ� �����͸� �۽��ϱ� ���� �̺�Ʈ ����
      bt = new Button("ON");
      bt.addActionListener(this);
      plabel.add(loglbl, BorderLayout.WEST);
      plabel.add(ltext, BorderLayout.CENTER);
      plabel.add(bt, BorderLayout.EAST);
      ptotal.add(plabel, BorderLayout.SOUTH);

      add(ptotal, BorderLayout.SOUTH);

      addWindowListener(new WinListener());
      setSize(300,250);
      setVisible(true);
   }
	
   public void runClient() {
      try {
         client = new Socket(InetAddress.getLocalHost(), 5000);
         mlbl.setText("����� �����̸� : " + client.getInetAddress().getHostName());
         input = new BufferedReader(new InputStreamReader(client.getInputStream()));
         output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
         clientdata = new StringBuffer(2048);
         mlbl.setText("���� �Ϸ� ����� ���̵� �Է��ϼ���.");
         while(true) {
            serverdata = input.readLine();
            display.append(serverdata+"\r\n");
            output.flush();
         }
      } catch(IOException e) {
         e.printStackTrace();
      }
   }
		
   public void actionPerformed(ActionEvent ae){
	   ID = ltext.getText();
	   if(ae.getSource() == bt && ID.equals("") != true) {
		   if(bt.getLabel().equals("ON")) {
		         try {
		            clientdata.setLength(0);
		            clientdata.append(REQ_LOGON);
		            clientdata.append(SEPARATOR);
		            clientdata.append(ID);
		            output.write(clientdata.toString()+"\r\n");
		            output.flush();
		            Thread.sleep(10);
		            if(serverdata.equals(ID + "�� �̹� �α��� �Ǿ��ֽ��ϴ�.")){
		            	mlbl.setText("�ߺ��� ID �Դϴ�. �ٽ� �Է����ּ���.");
		            	ltext.setText("");
		            }
		            else {
		            	mlbl.setText(ID + "(��)�� �α��� �Ͽ����ϴ�.");
		            	ltext.setVisible(false);
		            	loglbl.setText("�α׾ƿ�");
		            	bt.setLabel("OFF");
		            	plabel.validate();
		            }
		            
		         } catch(Exception e) {
		            e.printStackTrace();
		          }   
		   }
		   else {
			   mlbl.setText("����� " + ID + " �� �α׾ƿ� �Ͽ����ϴ�.\n");
				  try {
					clientdata.setLength(0);
					clientdata.append(REQ_LOGOUT);
		            clientdata.append(SEPARATOR);
		            clientdata.append(ID);
		            output.write(clientdata.toString()+"\r\n");
		            output.flush();
		            ID = "";
		            ltext.setVisible(true);
		            loglbl.setText("�α���");
		            bt.setLabel("ON");
		            ltext.setText("");
		            plabel.validate();

				  } catch(Exception e) {
			            e.printStackTrace();
			      }
		   }
	 }
     else {
    	 mlbl.setText("�α��� �� ����ϼ���!!!");
      }
   }
	
   public static void main(String args[]) {
      ChatWhisperC c = new ChatWhisperC();
      c.runClient();
   }
		
   class WinListener extends WindowAdapter {
      public void windowClosing(WindowEvent e){
         System.exit(0);
      }
   }

   public void keyPressed(KeyEvent ke) {
      if(ke.getKeyChar() == KeyEvent.VK_ENTER) {
         String message = wtext.getText();
         StringTokenizer st = new StringTokenizer(message, " ");
         if (ID == null) {
            mlbl.setText("�ٽ� �α��� �ϼ���!!!");
            wtext.setText("");
         } else {
            try {
               if(st.nextToken().equals("/w")) {
                  message = message.substring(3); // ��/w���� �����Ѵ�.
                  String WID = st.nextToken();
                  String Wmessage = st.nextToken();
                  while(st.hasMoreTokens()) { // ���鹮�� ������ ���� ��ȭ���߰�
                     Wmessage = Wmessage + " " + st.nextToken();
                  }
                  clientdata.setLength(0);
                  clientdata.append(REQ_WISPERSEND);
                  clientdata.append(SEPARATOR);
                  clientdata.append(ID);
                  clientdata.append(SEPARATOR);
                  clientdata.append(WID);
                  clientdata.append(SEPARATOR);
                  clientdata.append(Wmessage);
                  output.write(clientdata.toString()+"\r\n");
                  output.flush();
                  wtext.setText("");
               } else {
                  clientdata.setLength(0);
                  clientdata.append(REQ_SENDWORDS);
                  clientdata.append(SEPARATOR);
                  clientdata.append(ID);
                  clientdata.append(SEPARATOR);
                  clientdata.append(message);
                  output.write(clientdata.toString()+"\r\n");
                  output.flush();
                  wtext.setText("");
               }
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

   public void keyReleased(KeyEvent ke) {
   }

   public void keyTyped(KeyEvent ke) {
   }
}