package chapter10;

import java.awt.*;
import java.awt.event.*;

public class ChatClient extends Frame implements ActionListener {

	public TextField cc_tfLogon; // �α׿� �Է� �ؽ�Ʈ �ʵ�
	private Button cc_btLogon; // �α׿� ���� ��ư
	private Button cc_btEnter; // ��ȭ�� ���� �� ���� ��ư
	private Button cc_btLogout; // �α׾ƿ� ��ư

	public TextField cc_tfStatus; // �α׿� ���� �ȳ�
	public TextField cc_tfDate; // �����ð�
	public List cc_lstMember; // ��ȭ�� ������
	public static List cc_lstGame;

	public static ClientThread cc_thread;

	public String msg_logon = "";

	public ChatClient(String str) {
		super(str);
		setLayout(new BorderLayout());

		// �α׿�, ��ȭ�� ���� �� ���� ��ư�� �����Ѵ�.
		Panel bt_panel = new Panel();
		bt_panel.setLayout(new FlowLayout());
		cc_btLogon = new Button("�α׿½���");
		cc_btLogon.addActionListener(this);
		bt_panel.add(cc_btLogon);

		cc_tfLogon = new TextField(10);
		bt_panel.add(cc_tfLogon);

		cc_btEnter = new Button("��ȭ������");
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
		cc_tfStatus = new TextField("�ϴ��� �ؽ�Ʈ �ʵ忡  ID�� �Է��Ͻʽÿ�,", 43);
		// ��ȭ���� �������� �˸�
		cc_tfStatus.setEditable(false);
		northpanel.add(cc_tfStatus);

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout());
		centerpanel.add(new Label("�α׿� �ð�: "));
		cc_tfDate = new TextField("�α׿� �ð�", 31);
		cc_tfDate.setEditable(false);
		centerpanel.add(cc_tfDate);

		Panel southpanel = new Panel();
		southpanel.setLayout(new FlowLayout());
		southpanel.add(new Label("�α׿� �����"));
		cc_lstMember = new List(10);
		southpanel.add(cc_lstMember);

		roompanel.add("North", northpanel);
		roompanel.add("Center", centerpanel);
		roompanel.add("South", southpanel);
		add("North", roompanel);

		// �α׿� �ؽ�Ʈ �ʵ忡 ��Ŀ���� ���ߴ� �޼ҵ� �߰�

		addWindowListener(new WinListener());
	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {

			System.exit(0); // ���߿� �α׾ƿ���ƾ���� ����
		}
	}

	// �α׿�, ��ȭ�� ���� �� ���� ��ư ���� �̺�Ʈ�� ó���Ѵ�.
	public void actionPerformed(ActionEvent ae) {
		Button b = (Button) ae.getSource();
		if (b.getLabel().equals("�α׿½���")) {

			// �α׿� ó�� ��ƾ
			msg_logon = cc_tfLogon.getText(); // �α׿� ID�� �д´�.
			if (!msg_logon.equals("")) {
				cc_thread.requestLogon(msg_logon); // ClientThread�� �޼ҵ带 ȣ��
			} else {
				MessageBoxLess msgBox = new MessageBoxLess(this, "�α׿�", "�α׿� id�� �Է��ϼ���.");
				msgBox.setVisible(true);
			}
		} else if (b.getLabel().equals("��ȭ������")) {

			// ��ȭ�� ���� �� ���� ó�� ��ƾ
			msg_logon = cc_tfLogon.getText(); // �α׿� ID�� �д´�.
			if (!msg_logon.equals("")) {
				cc_thread.requestEnterRoom(msg_logon); // ClientThread�� �޼ҵ带 ȣ��
			} else {
				MessageBox msgBox = new MessageBox(this, "�α׿�", "�α׿��� ���� �Ͻʽÿ�.");
				msgBox.setVisible(true);
			}

		} else if (b.getLabel().equals("�α׾ƿ�")) {
			if (!msg_logon.equals("")) {
				cc_thread.requestLogout();
				cc_tfLogon.setText("");
			} else {
				MessageBox msgBox = new MessageBox(this, "�α׿�", "�α׿��� ���� �Ͻʽÿ�.");
				msgBox.setVisible(true);
			}
			// �α׾ƿ� ó�� ��ƾ

		}
	}

	public static void main(String args[]) {
		ChatClient client = new ChatClient("��ȭ�� ���� �� ����");
		client.setSize(350, 400);
		client.setVisible(true);

		// ������ �����ϰ� ������ ����� �����带 ȣ���Ѵ�.

		// ������ Ŭ���̾�Ʈ�� �ٸ� �ý������� ����ϴ� ���
		// ���� : java ChatClient [ȣ��Ʈ�̸��� ��Ʈ��ȣ�� �ʿ��ϴ�.]
		// To DO

		// ������ Ŭ���̾�Ʈ�� ���� �ý������� ����ϴ� ���
		// ���� : java ChatClient [ȣ��Ʈ�̸��� ��Ʈ��ȣ�� �ʿ����.]

		cc_thread = new ClientThread(client); // ���� ȣ��Ʈ�� ������
		cc_thread.start(); // Ŭ���̾�Ʈ�� �����带 �����Ѵ�.

	}
}
