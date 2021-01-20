package FinalProject;

import java.awt.*;
import java.awt.event.*;

public class GameRoom extends Frame implements KeyListener {


	public TextArea dr_taContents; // 대화말 내용 리스트창
	public List dr_lstMember; // 게임 참가자

	public TextField dr_tfInput, dr_tfUsers; // 대화말 입력필드

	public static ClientThread dr_thread;

	private boolean isSelected;
	private String WID;

	public GameRoom(ClientThread client, String title, String id, String targetID) {
		super(title);
		dr_thread = client;// ClientThread 클래스와 연결한다.
		setLayout(new BorderLayout());
		
		Panel northpanel = new Panel();
		northpanel.setLayout(new BorderLayout());
		Panel roomnamepanel = new Panel();
		Label roomlabel = new Label("가위바위보 게임");
		roomnamepanel.add(roomlabel,BorderLayout.WEST);
		dr_tfUsers = new TextField(15);
		dr_tfUsers.setText(" "+ id +" vs "+ targetID);
		dr_tfUsers.setEnabled(false);
		roomnamepanel.add(dr_tfUsers,BorderLayout.EAST);
		northpanel.add(roomnamepanel,BorderLayout.NORTH);

		Label lb_1 = new Label("user: " + dr_thread.myLogonID);
		northpanel.add(lb_1, BorderLayout.WEST);
		
		dr_tfInput = new TextField(15);
		northpanel.add(dr_tfInput, BorderLayout.EAST);
		dr_tfInput.addKeyListener(this);
		add("North", northpanel);
		addWindowListener(new WinListener());

	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			dr_thread.requestQuitRoom(dr_thread.myLogonID); // 로그아웃 루틴으로 바꾼다.
		}
	}

	// 가위, 바위, 보를 입력하여 서버에 전송한다.
	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
			String words = dr_tfInput.getText(); // 입력값을 구한다. 
			dr_thread.requestSendResult(words); // 입력값을 참여한 사용자에 전송한다.
		}
	}

	public void keyReleased(KeyEvent ke) {
	}

	public void keyTyped(KeyEvent ke) {
	}

}
