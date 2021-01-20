package chapter10;

import java.io.*;
import java.util.*;
import java.net.*;

public class ClientThread extends Thread {

	private ChatClient ct_client; // ChatClient ��ü
	private Socket ct_sock; // Ŭ���̾�Ʈ ����
	private DataInputStream ct_in; // �Է� ��Ʈ��
	private DataOutputStream ct_out; // ��� ��Ʈ��
	private StringBuffer ct_buffer; // ����
	private Thread thisThread;
	private DisplayRoom room;

	private String myLogonID = null;
	private static final String SEPARATOR = "|";
	private static final String DELIMETER = "`";

	// �޽��� ��Ŷ �ڵ� �� ������ ����

	// ������ �����ϴ� �޽��� �ڵ�
	private static final int REQ_LOGON = 1001;
	private static final int REQ_ENTERROOM = 1011;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_LOGOUT = 1031;
	private static final int REQ_QUITROOM = 1041;
	private static final int REQ_WHISPER = 1051;

	// �����κ��� ���۵Ǵ� �޽��� �ڵ�
	private static final int YES_LOGON = 2001;
	private static final int NO_LOGON = 2002;
	private static final int YES_ENTERROOM = 2011;
	private static final int NO_ENTERROOM = 2012;
	private static final int MDY_USERIDS = 2013;
	private static final int MDY_LOGONIDS = 2014;
	private static final int YES_SENDWORDS = 2021;
	private static final int NO_SENDWORDS = 2022;
	private static final int YES_WHISPER = 2023;
	private static final int YES_LOGOUT = 2031;
	private static final int NO_LOGOUT = 2032;
	private static final int YES_QUITROOM = 2041;

	// ���� �޽��� �ڵ�
	private static final int MSG_ALREADYUSER = 3001;
	private static final int MSG_SERVERFULL = 3002;
	private static final int MSG_CANNOTOPEN = 3011;

	private static MessageBox msgBox, logonbox;

	/*
	 * ����ȣ��Ʈ�� ������ ���� ������ ���� : java ChatClient ȣ��Ʈ�̸� ��Ʈ��ȣ To DO .....
	 */

	// ����ȣ��Ʈ���� ����ϱ� ���Ͽ� ���� ������
	// ������ Ŭ���̾�Ʈ�� ���� �ý����� ����Ѵ�.
	public ClientThread(ChatClient client) {
		try {
			ct_sock = new Socket(InetAddress.getLocalHost(), 2777);
			ct_in = new DataInputStream(ct_sock.getInputStream());
			ct_out = new DataOutputStream(ct_sock.getOutputStream());
			ct_buffer = new StringBuffer(4096);
			thisThread = this;
			ct_client = client; // ��ü������ �Ҵ�
		} catch (IOException e) {
			MessageBoxLess msgout = new MessageBoxLess(client, "���ῡ��", "������ ������ �� �����ϴ�.");
			msgout.setVisible(true);
		}
	}

	public void run() {

		try {
			Thread currThread = Thread.currentThread();
			while (currThread == thisThread) { // ����� LOG_OFF���� thisThread=null;�� ���Ͽ�
				String recvData = ct_in.readUTF();
				StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
				int command = Integer.parseInt(st.nextToken());
				switch (command) {

				// �α׿� ���� �޽��� PACKET : YES_LOGON|�����ð�|ID1`ID2`ID3...
				case YES_LOGON: {
					logonbox.dispose();
					myLogonID = ct_client.cc_tfLogon.getText();
					ct_client.cc_tfDate.setText("");
					ct_client.cc_tfStatus.setText("�α׿��� �����߽��ϴ�.");
					ct_client.cc_tfLogon.setEditable(false);
					String date = st.nextToken(); // ��ȭ�� �����ð�
					ct_client.cc_tfDate.setText(date);
					String ids = st.nextToken(); // ��ȭ�� ������ ����Ʈ
					StringTokenizer users = new StringTokenizer(ids, DELIMETER);
					ct_client.cc_lstMember.removeAll();
					while (users.hasMoreTokens()) {
						ct_client.cc_lstMember.add(users.nextToken());
					}
					break;
				}

				// �α׿� ���� �Ǵ� �α׿��ϰ� ��ȭ���� �������� ���� ����
				// PACKET : NO_LOGON|errCode
				case NO_LOGON: {
					int errcode = Integer.parseInt(st.nextToken());
					if (errcode == MSG_ALREADYUSER) {
						logonbox.dispose();
						msgBox = new MessageBox(ct_client, "�α׿�", "�̹� �ٸ� ����ڰ� �ֽ��ϴ�.");
						msgBox.setVisible(true);
					} else if (errcode == MSG_SERVERFULL) {
						logonbox.dispose();
						msgBox = new MessageBox(ct_client, "�α׿�", "��ȭ���� �����Դϴ�.");
						msgBox.setVisible(true);
					}
					break;
				}

				// ��ȭ�� ���� �� ���� ���� �޽��� PACKET : YES_ENTERROOM
				case YES_ENTERROOM: {
					ct_client.dispose(); // �α׿� â�� �����.
					String id = st.nextToken();
					room = new DisplayRoom(this, id + "�� ��ȭ��");
					room.pack();
					room.setVisible(true); // ��ȭ�� â�� ����Ѵ�.
					break;
				}

				// ��ȭ�� ���� �� ���� ���� �޽��� PACKET : NO_ENTERROOM|errCode
				case NO_ENTERROOM: {
					int roomerrcode = Integer.parseInt(st.nextToken());
					if (roomerrcode == MSG_CANNOTOPEN) {
						msgBox = new MessageBox(ct_client, "��ȭ������", "�α׿µ� ����ڰ� �ƴմϴ�.");
						msgBox.setVisible(true);
					}
					break;
				}

				// ��ȭ�濡 ������ ����� ����Ʈ�� ���׷��̵� �Ѵ�.
				// PACKET : MDY_USERIDS|id1'id2'id3.....
				case MDY_USERIDS: {
					room.dr_lstMember.removeAll(); // ��� ID�� �����Ѵ�.
					String ids = st.nextToken(); // ��ȭ�� ������ ����Ʈ
					if (!ids.equals("")) {
						StringTokenizer roomusers = new StringTokenizer(ids, DELIMETER);
						while (roomusers.hasMoreTokens()) {
							room.dr_lstMember.add(roomusers.nextToken());
						}
					}
					break;
				}

				// �α׿� ����� ����Ʈ�� ������Ʈ �Ѵ�.
				// PACKET : MDY_LOGONIDS|id1'id2'id3.....
				case MDY_LOGONIDS: {
					ct_client.cc_lstMember.removeAll(); // ��� ID�� �����Ѵ�.
					String ids = st.nextToken(); // ��ȭ�� ������ ����Ʈ
					StringTokenizer logonusers = new StringTokenizer(ids, DELIMETER);
					while (logonusers.hasMoreTokens()) {
						ct_client.cc_lstMember.add(logonusers.nextToken());
					}
					break;
				}

				// ���� �޽��� ��� PACKET : YES_SENDWORDS|ID|��ȭ��
				case YES_SENDWORDS: {
					String id = st.nextToken(); // ��ȭ�� �������� ID�� ���Ѵ�.
					try {
						String data = st.nextToken();
						room.dr_taContents.append(id + " : " + data + "\n");
					} catch (NoSuchElementException e) {
					}
					room.dr_tfInput.setText(""); // ��ȭ�� �Է� �ʵ带 �����.
					break;
				}

				case YES_WHISPER: {
					String from = st.nextToken();// ��ȭ�� �������� ID�� ���Ѵ�.
					String to = st.nextToken();
					String data = st.nextToken();

					room.dr_taContents.append(from + " -> " + to + " : " + data + "\n");
					room.dr_tfInput.setText(""); // ��ȭ�� �Է� �ʵ带 �����.
					break;
				}

				// LOGOUT �޽��� ó��
				// PACKET : YES_LOGOUT|Ż����id|Ż���� ���� id1, id2,....
				case YES_LOGOUT: {
					ct_client.cc_tfStatus.setText("�α׾ƿ��� �����߽��ϴ�.");
					ct_client.cc_lstMember.removeAll();
					ct_client.cc_tfLogon.setEditable(true);
					ct_client.cc_tfDate.setText("�α׿� �ð��� ǥ�õ˴ϴ�.");
					break;
				}
				// ��� �޽���(YES_QUITROOM) ó�� PACKET : YES_QUITROOM
				case YES_QUITROOM: {
					room.dispose();
					ct_client.show();
					break;
				}

				} // switch ����

				Thread.sleep(200);

			} // while ����(������ ����)

		} catch (InterruptedException e) {
			System.out.println(e);
			release();

		} catch (IOException e) {
			System.out.println(e);
			release();
		}
	}

	// ��Ʈ��ũ �ڿ��� �����Ѵ�.
	public void release() {
	};

	// Logon ��Ŷ(REQ_LOGON|ID)�� �����ϰ� �����Ѵ�.
	public void requestLogon(String id) {
		try {
			logonbox = new MessageBox(ct_client, "�α׿�", "������ �α׿� ���Դϴ�.");
			logonbox.setVisible(true);
			ct_buffer.setLength(0); // Logon ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_LOGON);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			send(ct_buffer.toString()); // Logon ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// EnterRoom ��Ŷ(REQ_ENTERROOM|ID)�� �����ϰ� �����Ѵ�.
	public void requestEnterRoom(String id) {
		try {
			ct_buffer.setLength(0); // EnterRoom ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_ENTERROOM);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			send(ct_buffer.toString()); // EnterRoom ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void requestLogout() {
		if (myLogonID != null) {
			try {
				ct_buffer.setLength(0); // EnterRoom ��Ŷ�� �����Ѵ�.
				ct_buffer.append(REQ_LOGOUT);
				ct_buffer.append(SEPARATOR);
				ct_buffer.append(myLogonID);
				send(ct_buffer.toString()); // EnterRoom ��Ŷ�� �����Ѵ�.
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}

	public void requestQuitRoom() {
		try {
			ct_buffer.setLength(0); // EnterRoom ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_QUITROOM);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(myLogonID);
			send(ct_buffer.toString()); // EnterRoom ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// SendWords ��Ŷ(REQ_SENDWORDS|ID|��ȭ��)�� �����ϰ� �����Ѵ�.
	public void requestSendWords(String words) {
		try {
			ct_buffer.setLength(0); // SendWords ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_SENDWORDS);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(ct_client.msg_logon);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(words);
			send(ct_buffer.toString()); // SendWords ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void requsetWhisper(String words, String WID) {
		try {
			ct_buffer.setLength(0);
			ct_buffer.append(REQ_WHISPER);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(ct_client.msg_logon);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(WID);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(words);
			send(ct_buffer.toString());
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// Ŭ���̾�Ʈ���� �޽����� �����Ѵ�.
	private void send(String sendData) throws IOException {
		ct_out.writeUTF(sendData);
		ct_out.flush();
	}
}