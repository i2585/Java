package FinalProject;

import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.JOptionPane;

public class ClientThread<roomusers> extends Thread {

	private GameClient ct_client; // ChatClient ��ü
	private Socket ct_sock; // Ŭ���̾�Ʈ ����
	private DataInputStream ct_in; // �Է� ��Ʈ��
	private DataOutputStream ct_out; // ��� ��Ʈ��
	private StringBuffer ct_buffer; // ����
	private Thread thisThread;
	private GameRoom room;

	String myLogonID = null;
	private static final String SEPARATOR = "|";
	private static final String DELIMETER = "`";

	// �޽��� ��Ŷ �ڵ� �� ������ ����

	// ������ �����ϴ� �޽��� �ڵ�
	private static final int REQ_LOGON = 1001;
	private static final int REQ_PLAYGAME = 1011;
	private static final int REQ_QUITROOM = 1021;
	private static final int REQ_LOGOUT = 1031;
	private static final int REQ_RESULT = 1041;
	
	// �����κ��� ���۵Ǵ� �޽��� �ڵ�
	private static final int YES_LOGON = 2001;
	private static final int NO_LOGON = 2002;
	private static final int YES_PLAYGAME = 2011;
	private static final int NO_PLAYGAME = 2012;
	private static final int MDY_USERIDS = 2013;
	private static final int MDY_LOGONIDS = 2014;
	private static final int YES_LOGOUT = 2031;
	private static final int YES_QUITROOM = 2022;
	private static final int YES_RESULT = 2041;
	
	// ���� �޽��� �ڵ�
	private static final int MSG_ALREADYUSER = 3001;
	private static final int MSG_SERVERFULL = 3002;
	private static final int MSG_CANNOTOPEN = 3011;
	private static final int MSG_ALREADYPLAYER = 3012;
	private static final int MSG_REJECT = 3013;

	private static MessageBox msgBox, logonbox;

	/*
	 * ����ȣ��Ʈ�� ������ ���� ������ ���� : java ChatClient ȣ��Ʈ�̸� ��Ʈ��ȣ To DO .....
	 */

	// ����ȣ��Ʈ���� ����ϱ� ���Ͽ� ���� ������
	// ������ Ŭ���̾�Ʈ�� ���� �ý����� ����Ѵ�.
	public ClientThread(GameClient client) {
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
					ct_client.cc_tfStatus.setText("�α׿��� �����߽��ϴ�.");
					ct_client.cc_tfLogon.setEditable(false);
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
					myLogonID="";
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


				case MDY_USERIDS: {
					String ids = st.nextToken(); // ���ӹ� ������ ����Ʈ
					if (ids.equals("������ �������ϴ�.")) {
						ct_client.cc_lstGame.removeAll(); // ��� ID�� �����Ѵ�.
					}
					else if (!ids.equals("")) {
						ct_client.cc_lstGame.removeAll(); // ��� ID�� �����Ѵ�.
						StringTokenizer roomusers = new StringTokenizer(ids, DELIMETER);
						while (roomusers.hasMoreTokens()) {
							ct_client.cc_lstGame.add(roomusers.nextToken());
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

				// LOGOUT �޽��� ó��
				// PACKET : YES_LOGOUT|Ż����id|Ż���� ���� id1, id2,....
				case YES_LOGOUT: {
					ct_client.cc_tfStatus.setText("�α׾ƿ��� �����߽��ϴ�.");
					ct_client.cc_lstMember.removeAll();
					ct_client.cc_lstGame.clear();
					ct_client.cc_tfLogon.setEditable(true);
					break;
				}
				
				case REQ_PLAYGAME: {
					String id = st.nextToken();
					String targetID = st.nextToken();
					int result = JOptionPane.showConfirmDialog(null, id + "�� ���� ���� ��û�� �����Ͻðڽ��ϱ�?", "���ӿ�û", JOptionPane.YES_NO_OPTION);
					
					if(result == JOptionPane.CLOSED_OPTION) {
						;
					} else if(result == JOptionPane.YES_OPTION) {
						try{
							ct_buffer.setLength(0);
							ct_buffer.append(YES_PLAYGAME);
							ct_buffer.append(SEPARATOR);
							ct_buffer.append(id);
							ct_buffer.append(SEPARATOR);
							ct_buffer.append(targetID);
							send(ct_buffer.toString());
						}catch(IOException e) {
	            			   System.out.println(e);
	            		}
						ct_client.dispose();
						room = new GameRoom(this, "���ӹ�", id, targetID);
						room.pack();
						room.show();
					} else {
						try{
							ct_buffer.setLength(0);
							ct_buffer.append(NO_PLAYGAME);
							ct_buffer.append(SEPARATOR);
							ct_buffer.append(id);
							send(ct_buffer.toString());
						}catch(IOException e) {
	            			   System.out.println(e);
	            		}
					}
					break;
				}
				
				case YES_PLAYGAME: {
					ct_client.dispose();
					String targetID = st.nextToken();
					room = new GameRoom(this, "���ӹ�", myLogonID, targetID);
					room.pack();
					room.show();
					break;
				}
				
				case NO_PLAYGAME: {
					int code = Integer.parseInt(st.nextToken());
					String id = st.nextToken();
					if(code == MSG_ALREADYPLAYER) {
						MessageBox msgout = new MessageBox(ct_client, "����â", "������ ���ӿ� ���� �� �Դϴ�.");
						msgout.setVisible(true);
						break;
					}
					else if(code == MSG_REJECT) {
						MessageBox msgout = new MessageBox(ct_client, "����â", "������ ������ �����Ͽ����ϴ�.");
						msgout.setVisible(true);
						break;
					}
				}
				
				case YES_RESULT: {
					String result = st.nextToken();
					JOptionPane.showMessageDialog(null, result, myLogonID + " ����â", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				
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
			myLogonID = id;
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
	public void requestPlayGame(String targetID) {
		try {
			ct_buffer.setLength(0); // ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_PLAYGAME);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(myLogonID);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(targetID);
			send(ct_buffer.toString());
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	public void requestQuitRoom(String id) {
		try {
			ct_buffer.setLength(0); // ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_QUITROOM);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			send(ct_buffer.toString());
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	public void requestLogout(String id) {
		if (myLogonID != null) {
			try {
				myLogonID = "";
				ct_buffer.setLength(0); // EnterRoom ��Ŷ�� �����Ѵ�.
				ct_buffer.append(REQ_LOGOUT);
				ct_buffer.append(SEPARATOR);
				ct_buffer.append(id);
				send(ct_buffer.toString()); // EnterRoom ��Ŷ�� �����Ѵ�.
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
	public void requestSendResult(String input) {
		try {
			ct_buffer.setLength(0); // EnterRoom ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_RESULT);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(myLogonID);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(input);
			send(ct_buffer.toString()); // EnterRoom ��Ŷ�� �����Ѵ�.
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