package chapter10;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {
	private Socket st_sock;
	private DataInputStream st_in;
	private DataOutputStream st_out;
	private StringBuffer st_buffer;
	/* �α׿µ� ����� ���� */
	private static Hashtable<String, ServerThread> logonHash;
	private static Vector<String> logonVector;
	/* ��ȭ�� ������ ���� */
	private static Hashtable<String, ServerThread> roomHash;
	private static Vector<String> roomVector;

	private static int isOpenRoom = 0; // ��ȭ���� �����ȵ�(�ʱⰪ)

	private static final String SEPARATOR = "|"; // �޽����� ������
	private static final String DELIMETER = "`"; // �Ҹ޽����� ������
	private static Date starttime; // �α׿� �ð�

	public String st_ID; // ID ����

	// �޽��� ��Ŷ �ڵ� �� ������ ����

	// Ŭ���̾�Ʈ�κ��� ���޵Ǵ� �޽��� �ڵ�
	private static final int REQ_LOGON = 1001;
	private static final int REQ_ENTERROOM = 1011;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_LOGOUT = 1031;
	private static final int REQ_QUITROOM = 1041;
	private static final int REQ_WHISPER = 1051;
	
	// Ŭ���̾�Ʈ�� �����ϴ� �޽��� �ڵ�
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

	static {
		logonHash = new Hashtable<String, ServerThread>(ChatServer.cs_maxclient);
		logonVector = new Vector<String>(ChatServer.cs_maxclient);
		roomHash = new Hashtable<String, ServerThread>(ChatServer.cs_maxclient);
		roomVector = new Vector<String>(ChatServer.cs_maxclient);
	}

	public ServerThread(Socket sock) {
		try {
			st_sock = sock;
			st_in = new DataInputStream(sock.getInputStream());
			st_out = new DataOutputStream(sock.getOutputStream());
			st_buffer = new StringBuffer(2048);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void run() {
		try {
			while (true) {
				String recvData = st_in.readUTF();
				StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
				int command = Integer.parseInt(st.nextToken());
				switch (command) {

				// �α׿� �õ� �޽��� PACKET : REQ_LOGON|ID
				case REQ_LOGON: {
					int result;
					String id = st.nextToken(); // Ŭ���̾�Ʈ�� ID�� ��´�.
					result = addUser(id, this);
					starttime = new Date();
					st_buffer.setLength(0);
					if (result == 0) { // ������ ����� ����
						st_buffer.append(YES_LOGON);
						// YES_LOGON|�����ð�|ID1`ID2`..
						st_buffer.append(SEPARATOR);
						st_buffer.append(starttime);
						st_buffer.append(SEPARATOR);
						String userIDs = getUsers(); // ��ȭ�� ���� �����ID�� ���Ѵ�
						st_buffer.append(userIDs);
						send(st_buffer.toString());
						
						modifyLogonUsers(userIDs);
					} else { // ���ӺҰ� ����
						st_buffer.append(NO_LOGON); // NO_LOGON|errCode
						st_buffer.append(SEPARATOR);
						st_buffer.append(result); // ���ӺҰ� �����ڵ� ����
						send(st_buffer.toString());
					}
					break;
				}

				// ��ȭ�� ���� �õ� �޽��� PACKET : REQ_ENTERROOM|ID
				case REQ_ENTERROOM: {
					st_buffer.setLength(0);
					String id = st.nextToken(); // Ŭ���̾�Ʈ�� ID�� ��´�.
					if (checkUserID(id) == null) {

						// NO_ENTERROOM PACKET : NO_ENTERROOM|errCode
						st_buffer.append(NO_ENTERROOM);
						st_buffer.append(SEPARATOR);
						st_buffer.append(MSG_CANNOTOPEN);
						send(st_buffer.toString()); // NO_ENTERROOM ��Ŷ�� �����Ѵ�.
						break;
					}

					roomVector.addElement(id); // ����� ID �߰�
					roomHash.put(id, this); // ����� ID �� Ŭ���̾�Ʈ�� ����� ������ ����

					if (isOpenRoom == 0) { // ��ȭ�� �����ð� ����
						isOpenRoom = 1;
						starttime = new Date();
					}

					// YES_ENTERROOM PACKET : YES_ENTERROOM
					st_buffer.append(YES_ENTERROOM);
					st_buffer.append(SEPARATOR);
					st_buffer.append(id);
					send(st_buffer.toString()); // YES_ENTERROOM ��Ŷ�� �����Ѵ�.

					// MDY_USERIDS PACKET : MDY_USERIDS|id1'id2' ....
					st_buffer.setLength(0);
					st_buffer.append(MDY_USERIDS);
					st_buffer.append(SEPARATOR);
					String userIDs = getRoomUsers(); // ��ȭ�� ���� ����� ID�� ���Ѵ�
					st_buffer.append(userIDs);
					broadcastRoom(st_buffer.toString()); // MDY_USERIDS ��Ŷ�� �����Ѵ�.
					break;
				}

				// ��ȭ�� ���� �õ� �޽��� PACKET : REQ_SENDWORDS|ID|��ȭ��
				case REQ_SENDWORDS: {
					st_buffer.setLength(0);
					st_buffer.append(YES_SENDWORDS);
					st_buffer.append(SEPARATOR);
					String id = st.nextToken(); // ������ ������� ID�� ���Ѵ�.
					st_buffer.append(id);
					st_buffer.append(SEPARATOR);
					try {
						String data = st.nextToken(); // ��ȭ���� ���Ѵ�.
						st_buffer.append(data);
					} catch (NoSuchElementException e) {
					}
					broadcastRoom(st_buffer.toString()); // YES_SENDWORDS ��Ŷ ����
					break;
				}

				// LOGOUT ���� �õ� �޽���
				// PACKET : YES_LOGOUT|Ż����ID|Ż���� �̿��� ids
				case REQ_LOGOUT: {
					String id = st.nextToken();
					RoomDelUser(id);
					st_buffer.setLength(0);
					st_buffer.append(YES_LOGOUT);
					st_buffer.append(SEPARATOR);
					st_buffer.append(id);
					send(st_buffer.toString());
					String logonIDs = getUsers();
					modifyLogonUsers(logonIDs);
					break;
				}

				// �� �������� LOGOUT ���� �õ� �޽��� PACKET : YES_QUITROOM
				case REQ_QUITROOM: {
					String id = st.nextToken();
					RoomDelUser(id);
					st_buffer.setLength(0);
					st_buffer.append(YES_QUITROOM);
					st_buffer.append(SEPARATOR);
					send(st_buffer.toString());
					String logonIDs = getRoomUsers();
					modifyRoomUsers(logonIDs);
					break;
				}

				// �ӼӸ�
				case REQ_WHISPER: {
					String id = st.nextToken(); // ������ ������� ID�� ���Ѵ�.
					String wid = st.nextToken();
					String words = st.nextToken();
					st_buffer.setLength(0);
					st_buffer.append(YES_SENDWORDS);
					st_buffer.append(SEPARATOR);
					st_buffer.append(id);
					st_buffer.append(SEPARATOR);
					st_buffer.append(wid);
					st_buffer.append(SEPARATOR);
					st_buffer.append(words);
					send(st_buffer.toString());
					
					ServerThread sTHread = roomHash.get(wid);
					sTHread.send(st_buffer.toString());
					break;
				}
				} // switch ����

				Thread.sleep(100);
			} // while ����

		} catch (NullPointerException e) { // �α׾ƿ��� st_in�� �� ���ܸ� �߻��ϹǷ�
		} catch (InterruptedException e) {
		} catch (IOException e) {
		}
	}

	private void modifyLogonUsers(String ids) throws IOException{
		st_buffer.setLength(0);
		st_buffer.append(MDY_LOGONIDS);
		st_buffer.append(SEPARATOR);
		st_buffer.append(ids);
		broadcastLogon(st_buffer.toString());
	}
	
	private void modifyRoomUsers(String ids) throws IOException{
		st_buffer.setLength(0);
		st_buffer.append(MDY_USERIDS);
		st_buffer.append(SEPARATOR);
		st_buffer.append(ids);
		broadcastLogon(st_buffer.toString());
	}
	// �ڿ��� �����Ѵ�.

	public void release() {
	}

	/*
	 * �ؽ� ���̺� ������ ��û�� Ŭ���̾�Ʈ�� ID �� ������ ����ϴ� �����带 ���. ��, �ؽ� ���̺��� ��ȭ�� �ϴ� Ŭ���̾�Ʈ�� ����Ʈ��
	 * ����.
	 */
	private static synchronized int addUser(String id, ServerThread client) {
		if (checkUserID(id) != null) {
			return MSG_ALREADYUSER;
		}
		if (logonHash.size() >= ChatServer.cs_maxclient) {
			return MSG_SERVERFULL;
		}
		logonVector.addElement(id); // ����� ID �߰�
		logonHash.put(id, client); // ����� ID �� Ŭ���̾�Ʈ�� ����� �����带 �����Ѵ�.
		client.st_ID = id;
		return 0; // Ŭ���̾�Ʈ�� ���������� �����ϰ�, ��ȭ���� �̹� ������ ����.
	}

	private static synchronized void delUser(String id) {
		logonVector.removeElement(id); // ����� ID ����
		logonHash.remove(id);
		roomVector.removeElement(id);
		roomHash.remove(id);
	}
	private static synchronized void RoomDelUser (String id) {
		roomVector.removeElement(id);
		roomHash.remove(id);
	}
	/*
	 * ������ ��û�� ������� ID�� ��ġ�ϴ� ID�� �̹� ���Ǵ� ���� �����Ѵ�. ��ȯ���� null�̶�� �䱸�� ID�� ��ȭ�� ������ ������.
	 */
	private static ServerThread checkUserID(String id) {
		ServerThread alreadyClient = null;
		alreadyClient = (ServerThread) logonHash.get(id);
		return alreadyClient;
	}

	// �α׿¿� ������ ����� ID�� ���Ѵ�.
	private String getUsers() {
		StringBuffer id = new StringBuffer();
		String ids;
		Enumeration<String> enu = logonVector.elements();
		while (enu.hasMoreElements()) {
			id.append(enu.nextElement());
			id.append(DELIMETER);
		}
		try {
			ids = new String(id); // ���ڿ��� ��ȯ�Ѵ�.
			ids = ids.substring(0, ids.length() - 1); // ������ "`"�� �����Ѵ�.
		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}
		return ids;
	}

	// ��ȭ�濡 ������ ����� ID�� ���Ѵ�.

	private String getRoomUsers() {
		StringBuffer id = new StringBuffer();
		String ids;
		Enumeration<String> enu = roomVector.elements();
		while (enu.hasMoreElements()) {
			id.append(enu.nextElement());
			id.append(DELIMETER);
		}
		try {
			ids = new String(id);
			ids = ids.substring(0, ids.length() - 1); // ������ "`"�� �����Ѵ�.
		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}
		return ids;
	}

	// ��ȭ�濡 ������ ��� �����(��ε��ɽ���)���� �����͸� �����Ѵ�.
	public synchronized void broadcastRoom(String sendData) throws IOException {
		ServerThread client;
		Enumeration<String> enu = roomVector.elements();
		while (enu.hasMoreElements()) {
			client = (ServerThread) roomHash.get(enu.nextElement());
			client.send(sendData);
		}
	}

	public synchronized void broadcastLogon(String sendData) throws IOException {
		ServerThread client;
		Enumeration<String> enu = logonVector.elements();
		while (enu.hasMoreElements()) {
			client = (ServerThread) logonHash.get(enu.nextElement());
			client.send(sendData);
		}
	}
	// �����͸� �����Ѵ�.
	public void send(String sendData) throws IOException {
		synchronized (st_out) {
			st_out.writeUTF(sendData);
			st_out.flush();
		}
	}
}
