package chapter10;

import java.awt.*;
import java.awt.event.*;

public class DisplayRoom extends Frame implements ActionListener, KeyListener, MouseListener {

	private Button dr_btClear; // ��ȭ�� â ȭ�� �����
	private Button dr_btLogout; // �α׾ƿ� ���� ��ư
	private Button dr_whisper;

	public TextArea dr_taContents; // ��ȭ�� ���� ����Ʈâ
	public List dr_lstMember; // ��ȭ�� ������

	public TextField dr_tfInput; // ��ȭ�� �Է��ʵ�

	public static ClientThread dr_thread;

	private boolean isSelected;
	private String WID;

	public DisplayRoom(ClientThread client, String title) {
		super(title);

		isSelected = false;
		setLayout(new BorderLayout());

		// ��ȭ�濡�� ����ϴ� ������Ʈ�� ��ġ�Ѵ�.
		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());
		dr_btClear = new Button("ȭ�������");
		dr_btClear.addActionListener(this);
		northpanel.add(dr_btClear);

		dr_btLogout = new Button("����ϱ�");
		dr_btLogout.addActionListener(this);
		northpanel.add(dr_btLogout);

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout());
		dr_taContents = new TextArea(10, 27);
		dr_taContents.setEditable(false);
		centerpanel.add(dr_taContents);

		dr_lstMember = new List(10);
		dr_lstMember.addMouseListener(this);
		centerpanel.add(dr_lstMember);

		Panel southpanel = new Panel();
		southpanel.setLayout(new FlowLayout());
		dr_tfInput = new TextField(41);
		dr_tfInput.addKeyListener(this);
		southpanel.add(dr_tfInput);

		dr_whisper = new Button("����������");
		dr_whisper.addActionListener(this);
		southpanel.add(dr_whisper);

		add("North", northpanel);
		add("Center", centerpanel);
		add("South", southpanel);

		dr_thread = client; // ClientThread Ŭ������ �����Ѵ�.

		// �Է� �ؽ�Ʈ �ʵ忡 ��Ŀ���� ���ߴ� �޼ҵ� �߰�

		addWindowListener(new WinListener());

	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			dr_thread.requestLogout(); // �α׾ƿ� ��ƾ���� �ٲ۴�.
		}
	}

	// ȭ�������, �α׾ƿ� �̺�Ʈ�� ó���Ѵ�.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("ȭ�������")) {
			dr_taContents.setText("");
			// ȭ������� ó�� ��ƾ

		} else if (b.getLabel().equals("����ϱ�")) {
			dr_thread.requestQuitRoom();
			// �α׾ƿ� ó�� ��ƾ
		} else if (b.getLabel().equals("����������")) {
			if (!isSelected) {
				MessageBox msgBox = new MessageBox(this, "����������", "���� ���� ������ �����ϼ���!");
				msgBox.setVisible(true);
			} else {
				String words = dr_tfInput.getText();
				if(!words.equals("")) {
					WID = dr_lstMember.getSelectedItem();
					dr_thread.requsetWhisper(words, WID);
				} else {
					MessageBox msgBox = new MessageBox(this, "����������", "���� �� �޽����� �Է��ϼ���!");
					msgBox.setVisible(true);
				}
			}
		}
	}

	// �Է��ʵ忡 �Է��� ��ȭ���� ������ �����Ѵ�.
	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
			String words = dr_tfInput.getText(); // ��ȭ���� ���Ѵ�.
			dr_thread.requestSendWords(words); // ��ȭ���� ������ ����ڿ� �����Ѵ�.
		}
	}

	public void keyReleased(KeyEvent ke) {
	}

	public void keyTyped(KeyEvent ke) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		isSelected = true;
		WID = ((List) e.getSource()).getSelectedItem();
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
