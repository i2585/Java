package FinalProject;

import java.awt.*;
import java.awt.event.*;

public class GameClient extends Frame implements ActionListener, MouseListener
{
   
   public TextField cc_tfLogon; // 로그온 입력 텍스트 필드
   private Button cc_btLogon; // 로그온 실행 버튼
   private Button cc_btEnter; // 게임신청 및 입장 버튼
   private Button cc_btLogout; // 로그아웃 버튼

   public TextField cc_tfStatus; // 로그온 개설 안내
   public List cc_lstMember, cc_lstGame; // 로그온 참가자

   public static ClientThread cc_thread;

   public String msg_logon="";
   String targetID=null;
   public GameClient(String str){
      super(str);
      setLayout(new BorderLayout());

      Panel bt_panel = new Panel();
      bt_panel.setLayout(new FlowLayout());
      cc_btLogon = new Button("로그온실행");
      cc_btLogon.addActionListener(this);
      bt_panel.add(cc_btLogon);
      
      cc_tfLogon = new TextField(10);
      bt_panel.add(cc_tfLogon);
      
      cc_btEnter = new Button("게임신청");
      cc_btEnter.addActionListener(this);
      bt_panel.add(cc_btEnter);
      
      cc_btLogout = new Button("로그아웃");
      cc_btLogout.addActionListener(this);
      bt_panel.add(cc_btLogout);
      add("Center", bt_panel);

      // 4개의 Panel 객체를 사용하여 대화방 정보를 출력한다.
      Panel roompanel = new Panel(); // 3개의 패널을 담을 패널객체
      roompanel.setLayout(new BorderLayout());

      Panel northpanel = new Panel();
      northpanel.setLayout(new FlowLayout());
      cc_tfStatus = new TextField("하단의 텍스트 필드에  ID를 입력하십시오,",43); 
      													// 대화방의 개설상태 알림
      cc_tfStatus.setEditable(false);
      northpanel.add(cc_tfStatus);


      Panel southpanel = new Panel(); 
      southpanel.setLayout(new BorderLayout());
      Panel labelpanel = new Panel();
      labelpanel.add(new Label("로그온 목록"),BorderLayout.WEST) ;
      labelpanel.add(new Label("게임 참여자"),BorderLayout.EAST) ;
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

      // 로그온 텍스트 필드에 포커스를 맞추는 메소드 추가

      addWindowListener(new WinListener());
   }

   class WinListener extends WindowAdapter
   {
      public void windowClosing(WindowEvent we){

         System.exit(0); // 나중에 로그아웃루틴으로 변경
      }
   }

   // 로그온, 대화방 개설 및 입장 버튼 눌림 이벤트를 처리한다.
   public void actionPerformed(ActionEvent ae){
      Button b = (Button)ae.getSource();
      if(b.getLabel().equals("로그온실행")){

         // 로그온 처리 루틴
         msg_logon = cc_tfLogon.getText(); // 로그온 ID를 읽는다.
         if(!msg_logon.equals("")){
        	 msg_logon = cc_tfLogon.getText();
            cc_thread.requestLogon(msg_logon); // ClientThread의 메소드를 호출
         }else{
            MessageBox msgBox = new  MessageBox(this, "로그온", "로그온 id를 입력하세요.");
            msgBox.setVisible(true);
         }
      }else if(b.getLabel().equals("게임신청")){
         // 게임신청 처리 루틴
         msg_logon = cc_tfLogon.getText(); // 로그온 ID를 읽는다.
         if(!msg_logon.equals("")){
        	 if(msg_logon.equals(targetID)) {
        		 cc_tfStatus.setText("자신과는 게임을 할 수 없습니다.\n");
        	 }
        	 else if(targetID == null) {
        		 cc_tfStatus.setText("게임할 대상을 선택해주세요!!\n");
        	 }
        	 else {
        		 cc_thread.requestPlayGame(targetID); // ClientThread의 메소드를 호출
        	 }
         }else{
             MessageBox msgBox = new MessageBox(this, "로그온", "로그온을 먼저 하십시오.");
             msgBox.setVisible(true);
         }
      }else if(b.getLabel().equals("로그아웃")){ // 로그아웃 처리 루틴
    	  if(!msg_logon.equals("")){
    		  cc_thread.requestLogout(msg_logon);
    		  cc_tfLogon.setText("");
    	  }else{
              MessageBox msgBox = new MessageBox(this, "로그온", "로그온을 먼저 하십시오.");
              msgBox.setVisible(true);
           }
      }
   }

   public static void main(String args[]){
      GameClient client = new GameClient("가위바위보 게임");
      client.setSize(350, 400);
      client.setVisible(true);
       cc_thread = new ClientThread(client); // 로컬 호스트용 생성자
       cc_thread.start(); // 클라이언트의 스레드를 시작한다.
      
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
