package ex_9;

import java.io.*;
import java.net.*;

import ex_9.OneToOneC.Reconnect;

import java.awt.*;
import java.awt.event.*;

public class MultipleChatC extends Frame implements ActionListener {
	
   TextArea display;
   TextField text, uname;
   Label lword, ulabel;
   BufferedWriter output;
   BufferedReader input;
   Socket client;
   String user_name;
   String clientdata = "";
   String serverdata = "";
   public MultipleChatC() {
      super("클라이언트");
      Panel user = new Panel(new BorderLayout());
      ulabel = new Label("사용자이름");
      uname = new TextField(25);
      user.add(ulabel, BorderLayout.WEST);
      user.add(uname, BorderLayout.EAST);
      add(user, BorderLayout.NORTH);
      display=new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
      display.setEditable(false);
      add(display, BorderLayout.CENTER);

      Panel pword = new Panel(new BorderLayout());
      lword = new Label("대화말");
      text = new TextField(30); //전송할 데이터를 입력하는 필드
      text.addActionListener(this); //입력된 데이터를 송신하기 위한 이벤트 연결
      pword.add(lword, BorderLayout.WEST);
      pword.add(text, BorderLayout.EAST);
      add(pword, BorderLayout.SOUTH);

      addWindowListener(new WinListener());
      setSize(300, 150);
      setVisible(true);
   }
	
   public void runClient() {
      try {
         client = new Socket(InetAddress.getLocalHost(), 5000);
         input = new BufferedReader(new InputStreamReader(client.getInputStream()));
         output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
         while(true) {
            String serverdata = input.readLine();
            display.append("\r\n" + serverdata);
         }

      } catch(IOException e ) {
         e.printStackTrace();
      }
      try{
         client.close();
      }catch(IOException e){
         e.printStackTrace();
      }
   }
		
   public void actionPerformed(ActionEvent ae){
      user_name = uname.getText();
      if (!user_name.equals("")) {
    	  clientdata = text.getText();
    	  if(clientdata.equals("quit")){
    		  try {
    			  client.close();
    			  display.append("종료합니다");
    		  }catch(IOException e){
    	             e.printStackTrace();
              } 
    	  }
    	  else {
    		  try{
    			  output.write(user_name + ":" +clientdata+"\r\n");
    			  output.flush();
    			  text.setText("");
    		  } catch(IOException e){
    			  e.printStackTrace();
    		  } 
    	  }
      }
      else {
    	  display.append("\n사용자 이름을 입력 후 사용하세요!");
      }
	  
   }
		
   public static void main(String args[]) {
      MultipleChatC c = new MultipleChatC();
      c.runClient();
   }
		
   class WinListener extends WindowAdapter {
      public void windowClosing(WindowEvent e){
         System.exit(0);
      }
   }			
}