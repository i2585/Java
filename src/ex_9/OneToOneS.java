package ex_9;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
public class OneToOneS extends Frame implements ActionListener {
   TextArea display;
   TextField text;
   Label lword;
   Socket connection;
   BufferedWriter output;
   BufferedReader input;
   String clientdata = "";
   String serverdata = "";

   public OneToOneS() {
      super("����");
      display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
      display.setEditable(false);
      add(display, BorderLayout.CENTER);

      Panel pword = new Panel(new BorderLayout());
      lword = new Label("��ȭ��");
      text = new TextField(30); //������ �����͸� �Է��ϴ� �ʵ�
      text.addActionListener(this); //�Էµ� �����͸� �۽��ϱ� ���� �̺�Ʈ ����
      pword.add(lword, BorderLayout.WEST);
      pword.add(text, BorderLayout.EAST);
      add(pword, BorderLayout.SOUTH);

      addWindowListener(new WinListener());
      setSize(300,200);
      setVisible(true);
   }
   
   public void runServer() {
      ServerSocket server;
      One2One othread;
      try {
         server = new ServerSocket(5000, 100);
         while(true) {
        	 connection = server.accept();
        	 display.setText("Ŭ���̾�Ʈ " + connection.getInetAddress().getHostAddress() + "�� ����Ǿ����ϴ�.");
        	 othread = new One2One(connection, display);
        	 othread.start();
         }
      } catch(IOException e) {
         e.printStackTrace();
      }
   }
	
   public void actionPerformed(ActionEvent ae){
	  if(connection == null) {
		  display.append("\n����� Ŭ���̾�Ʈ�� �����ϴ�.");
		  return;
	  }
      serverdata = text.getText();
      try{
         display.append("\n���� : " + serverdata);
         output.write(serverdata+"\r\n");
         output.flush();
         text.setText("");
         if(serverdata.equals("quit")){
            connection.close();
            connection=null;
         }
      } catch(IOException e){
         e.printStackTrace();
      }
   }
		
   public static void main(String args[]) {
      OneToOneS s = new OneToOneS();
      s.runServer();
   }
		
   class WinListener extends WindowAdapter {
      public void windowClosing(WindowEvent e) {
         System.exit(0);
      }
   }
   
  class One2One extends Thread{
	  Socket client;
	  TextArea display;
	  public One2One(Socket connection, TextArea display) {
		  client = connection;
		  this.display = display;
	  }
	  public void run() {
		  try {
			  InputStream is = connection.getInputStream();
			  InputStreamReader isr = new InputStreamReader(is);
			  input = new BufferedReader(isr); // ������ ������ ��ȭ���� ����
			  OutputStream os = connection.getOutputStream();
			  OutputStreamWriter osw = new OutputStreamWriter(os);
			  output = new BufferedWriter(osw); // Ŭ���̾�Ʈ�� ��ȭ���� ����
			  while(true) {
				  String clientdata = input.readLine();
				  if(clientdata.equals("quit")) {
					  display.append("\nŬ���̾�Ʈ���� ������ �ߴܵǾ����ϴ�");
					  output.flush();
					  break;
				  } else {
					  display.append("\nŬ���̾�Ʈ �޽��� : " + clientdata);
					  output.flush();
				  }
			  }
			  connection.close();
			  connection=null;
		  }catch(IOException e) {
		         e.printStackTrace();
		  }
	  }
  }
}