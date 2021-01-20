package FinalProject;

import java.awt.*;
import java.awt.event.*;

public class GameClient extends Frame implements ActionListener, MouseListener
{
   
   public TextField cc_tfLogon; // �α׿� �Է� �ؽ�Ʈ �ʵ�
   private Button cc_btLogon; // �α׿� ���� ��ư
   private Button cc_btEnter; // ���ӽ�û �� ���� ��ư
   private Button cc_btLogout; // �α׾ƿ� ��ư

   public TextField cc_tfStatus; // �α׿� ���� �ȳ�
   public List cc_lstMember, cc_lstGame; // �α׿� ������

   public static ClientThread cc_thread;

   public String msg_logon="";
   String targetID=null;
   public GameClient(String str){
      super(str);
      setLayout(new BorderLayout());

      Panel bt_panel = new Panel();
      bt_panel.setLayout(new FlowLayout());
      cc_btLogon = new Button("�α׿½���");
      cc_btLogon.addActionListener(this);
      bt_panel.add(cc_btLogon);
      
      cc_tfLogon = new TextField(10);
      bt_panel.add(cc_tfLogon);
      
      cc_btEnter = new Button("���ӽ�û");
      cc_btEnter.addActionListener(this);
      bt_panel.add(cc_btEnter);
      
      cc_btLogout = new Button("�α׾ƿ�");
      cc_btLogout.addActionListener(this);
      bt_panel.add(cc_btLogout);
      add("Center", bt_panel);

      // 4���� Panel ��ü�� ����Ͽ� ��ȭ�� ������ ����Ѵ�.
      Panel roompanel = new Panel(); // 3���� �г��� ���� �гΰ�ü
      roompanel.setLayout(new BorderLayout());

      Panel northpanel = new Panel();
      northpanel.setLayout(new FlowLayout());
      cc_tfStatus = new TextField("�ϴ��� �ؽ�Ʈ �ʵ忡  ID�� �Է��Ͻʽÿ�,",43); 
      													// ��ȭ���� �������� �˸�
      cc_tfStatus.setEditable(false);
      northpanel.add(cc_tfStatus);


      Panel southpanel = new Panel(); 
      southpanel.setLayout(new BorderLayout());
      Panel labelpanel = new Panel();
      labelpanel.add(new Label("�α׿� ���"),BorderLayout.WEST) ;
      labelpanel.add(new Label("���� ������"),BorderLayout.EAST) ;
      southpanel.add(labelpanel,BorderLayout.NORTH);
      
      Panel boardpanel = new Panel();
      cc_lstMember = new List(10);  	      
      boardpanel.add(cc_lstMember,BorderLayout.WEST);
      cc_lstGame = new List(10);
      cc_lstMember.addMouseListener(this);
      boardpanel.add(cc_lstGame,BorderLayout.EAST);
      southpanel.add(boardpanel,BorderLayout.SOUTH);
      
      roompanel.add("North", northpanel);
      roompanel.add("South", southpanel);
      add("North", roompanel);

      // �α׿� �ؽ�Ʈ �ʵ忡 ��Ŀ���� ���ߴ� �޼ҵ� �߰�

      addWindowListener(new WinListener());
   }

   class WinListener extends WindowAdapter
   {
      public void windowClosing(WindowEvent we){

         System.exit(0); // ���߿� �α׾ƿ���ƾ���� ����
      }
   }

   // �α׿�, ��ȭ�� ���� �� ���� ��ư ���� �̺�Ʈ�� ó���Ѵ�.
   public void actionPerformed(ActionEvent ae){
      Button b = (Button)ae.getSource();
      if(b.getLabel().equals("�α׿½���")){

         // �α׿� ó�� ��ƾ
         msg_logon = cc_tfLogon.getText(); // �α׿� ID�� �д´�.
         if(!msg_logon.equals("")){
        	 msg_logon = cc_tfLogon.getText();
            cc_thread.requestLogon(msg_logon); // ClientThread�� �޼ҵ带 ȣ��
         }else{
            MessageBox msgBox = new  MessageBox(this, "�α׿�", "�α׿� id�� �Է��ϼ���.");
            msgBox.setVisible(true);
         }
      }else if(b.getLabel().equals("���ӽ�û")){
         // ���ӽ�û ó�� ��ƾ
         msg_logon = cc_tfLogon.getText(); // �α׿� ID�� �д´�.
         if(!msg_logon.equals("")){
        	 if(msg_logon.equals(targetID)) {
        		 cc_tfStatus.setText("�ڽŰ��� ������ �� �� �����ϴ�.\n");
        	 }
        	 else if(targetID == null) {
        		 cc_tfStatus.setText("������ ����� �������ּ���!!\n");
        	 }
        	 else {
        		 cc_thread.requestPlayGame(targetID); // ClientThread�� �޼ҵ带 ȣ��
        	 }
         }else{
             MessageBox msgBox = new MessageBox(this, "�α׿�", "�α׿��� ���� �Ͻʽÿ�.");
             msgBox.setVisible(true);
         }
      }else if(b.getLabel().equals("�α׾ƿ�")){ // �α׾ƿ� ó�� ��ƾ
    	  if(!msg_logon.equals("")){
    		  cc_thread.requestLogout(msg_logon);
    		  cc_tfLogon.setText("");
    	  }else{
              MessageBox msgBox = new MessageBox(this, "�α׿�", "�α׿��� ���� �Ͻʽÿ�.");
              msgBox.setVisible(true);
           }
      }
   }

   public static void main(String args[]){
      GameClient client = new GameClient("���������� ����");
      client.setSize(350, 400);
      client.setVisible(true);
       cc_thread = new ClientThread(client); // ���� ȣ��Ʈ�� ������
       cc_thread.start(); // Ŭ���̾�Ʈ�� �����带 �����Ѵ�.
      
   }

@Override
public void mouseClicked(MouseEvent e) {
	if(!msg_logon.equals(""))
		targetID = cc_lstMember.getSelectedItem().toString();
}

@Override
public void mousePressed(MouseEvent e) {
	// TODO Auto-generated method stub
	
}

@Override
public void mouseReleased(MouseEvent e) {
	// TODO Auto-generated method stub
	
}

@Override
public void mouseEntered(MouseEvent e) {
	// TODO Auto-generated method stub
	
}

@Override
public void mouseExited(MouseEvent e) {
	// TODO Auto-generated method stub
	
}
}
