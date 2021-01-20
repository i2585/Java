package FinalProject;

import java.awt.*;
import java.awt.event.*;

public class GameRoom extends Frame implements KeyListener {


	public TextArea dr_taContents; // ��ȭ�� ���� ����Ʈâ
	public List dr_lstMember; // ���� ������

	public TextField dr_tfInput, dr_tfUsers; // ��ȭ�� �Է��ʵ�

	public static ClientThread dr_thread;

	private boolean isSelected;
	private String WID;

	public GameRoom(ClientThread client, String title, String id, String targetID) {
		super(title);
		dr_thread = client;// ClientThread Ŭ������ �����Ѵ�.
		setLayout(new BorderLayout());
		
		Panel northpanel = new Panel();
		northpanel.setLayout(new BorderLayout());
		Panel roomnamepanel = new Panel();
		Label roomlabel = new Label("���������� ����");
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
			dr_thread.requestQuitRoom(dr_thread.myLogonID); // �α׾ƿ� ��ƾ���� �ٲ۴�.
		}
	}

	// ����, ����, ���� �Է��Ͽ� ������ �����Ѵ�.
	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
			String words = dr_tfInput.getText(); // �Է°��� ���Ѵ�. 
			dr_thread.requestSendResult(words); // �Է°��� ������ ����ڿ� �����Ѵ�.
		}
	}

	public void keyReleased(KeyEvent ke) {
	}

	public void keyTyped(KeyEvent ke) {
	}

}
