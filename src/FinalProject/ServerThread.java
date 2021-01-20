package FinalProject;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JOptionPane;

import chapter10.ChatClient;
import chapter10.ClientThread;

public class ServerThread extends Thread {
	private Socket st_sock;
	private DataInputStream st_in;
	private DataOutputStream st_out;
	private StringBuffer st_buffer;

	/* �α׿µ� ����� ���� */
	private static Hashtable<String, ServerThread> logonHash;
	private static Vector<String> logonVector;
	/* ���� ������ ���� */
	private static Hashtable<String, ServerThread> roomHash;
	private static Vector<String> roomVector;

	private static final String SEPARATOR = "|"; // �޽����� ������
	private static final String DELIMETER = "`"; // �Ҹ޽����� ������

	public String st_ID; // ID ����
	public static ServerThread player1 = null;
	public static ServerThread player2 = null;
	public static String result1 = null;
	public static String result2 = null;

	// �޽��� ��Ŷ �ڵ� �� ������ ����

	// Ŭ���̾�Ʈ�κ��� ���޵Ǵ� �޽��� �ڵ�
	private static final int REQ_LOGON = 1001;
	private static final int REQ_PLAYGAME = 1011;
	private static final int REQ_QUITROOM = 1021;
	private static final int REQ_LOGOUT = 1031;
	private static final int REQ_RESULT = 1041;

	// Ŭ���̾�Ʈ�� �����ϴ� �޽��� �ڵ�
	private static final int YES_LOGON = 2001;
	private static final int NO_LOGON = 2002;
	private static final int MDY_USERIDS = 2013;
	private static final int MDY_LOGONIDS = 2014;
	private static final int YES_LOGOUT = 2031;
	private static final int YES_PLAYGAME = 2011;
	private static final int NO_PLAYGAME = 2012;
	private static final int YES_QUITROOM = 2022;
	private static final int YES_RESULT = 2041;

	// ���� �޽��� �ڵ�
	private static final int MSG_ALREADYUSER = 3001;
	private static final int MSG_SERVERFULL = 3002;
	private static final int MSG_CANNOTOPEN = 3011;
	private static final int MSG_ALREADYPLAYER = 3012;
	private static final int MSG_REJECT = 3013;

	static {
		logonHash = new Hashtable<String, ServerThread>(GameServer.cs_maxclient);
		logonVector = new Vector<String>(GameServer.cs_maxclient);
		roomHash = new Hashtable<String, ServerThread>(GameServer.cs_maxclient);
		roomVector = new Vector<String>(GameServer.cs_maxclient);
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
					st_buffer.setLength(0);
					if (result == 0) { // ������ ����� ����
						st_buffer.append(YES_LOGON);
						// YES_LOGON|�����ð�|ID1`ID2`..
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

				// LOGOUT ���� �õ� �޽���
				// PACKET : YES_LOGOUT|Ż����ID|Ż���� �̿��� ids
				case REQ_LOGOUT: {
					String id = st.nextToken();
					delLogUser(id, this);
					st_buffer.setLength(0);
					st_buffer.append(YES_LOGOUT);
					send(st_buffer.toString());
					String logonIDs = getUsers();
					modifyLogonUsers(logonIDs);
					break;
				}

				case REQ_PLAYGAME: {
					String id = st.nextToken();
					String targetID = st.nextToken();

					ServerThread client = null;
					ServerThread comfirm = null;
					if ((client = (ServerThread) logonHash.get(targetID)) != null) {
						if ((comfirm = (ServerThread) roomHash.get(targetID)) != null) {
							st_buffer.setLength(0);
							st_buffer.append(NO_PLAYGAME);
							st_buffer.append(SEPARATOR);
							st_buffer.append(MSG_ALREADYPLAYER);
							st_buffer.append(SEPARATOR);
							st_buffer.append(targetID);
							client.send(st_buffer.toString());
							break;
						}
						st_buffer.setLength(0);
						st_buffer.append(REQ_PLAYGAME);
						st_buffer.append(SEPARATOR);
						st_buffer.append(id);
						st_buffer.append(SEPARATOR);
						st_buffer.append(targetID);
						client.send(st_buffer.toString());
						break;
					}
				}

				case REQ_QUITROOM: {
					st_buffer.setLength(0);
					String id = st.nextToken();
					delRoomUser(id, this);

					st_buffer.append(YES_QUITROOM);
					send(st_buffer.toString());
					String RoomIDs = getRoomUsers();
					if (RoomIDs.equals(""))
						RoomIDs = "������ �������ϴ�.";
					modifyRoomUsers(RoomIDs);
					break;
				}

				case YES_PLAYGAME: {
					String id = st.nextToken();
					String targetID = st.nextToken();
					addRoomUser(id, targetID, this);
					ServerThread client = (ServerThread) logonHash.get(id);
					st_buffer.setLength(0);
					st_buffer.append(YES_PLAYGAME);
					st_buffer.append(SEPARATOR);
					st_buffer.append(targetID);
					client.send(st_buffer.toString());

					String players = getRoomUsers();
					modifyRoomUsers(players);
					break;
				}

				case NO_PLAYGAME: {
					String id = st.nextToken();
					ServerThread client = (ServerThread) logonHash.get(id);
					st_buffer.setLength(0);
					st_buffer.append(NO_PLAYGAME);
					st_buffer.append(SEPARATOR);
					st_buffer.append(MSG_REJECT);
					st_buffer.append(SEPARATOR);
					st_buffer.append(id);
					client.send(st_buffer.toString());

					break;
				}

				case REQ_RESULT: {
					String id = st.nextToken();
					st_buffer.setLength(0);
					st_buffer.append(YES_RESULT);
					st_buffer.append(SEPARATOR);
					if (player1 == null)	player1 = (ServerThread) logonHash.get(id);
					else if(player2 == null)	player2 = (ServerThread) logonHash.get(id);

					
					if(player1.equals(player2)) {
						player1 = null;
						player2 = null;
						result1 = null;
						result2 = null;
						break;
					}
					
					if (result1 == null)	result1 = st.nextToken();
					else if(result2 == null)	result2 = st.nextToken();
					
					if (result1 != null && result2 != null) {
						if (result1.equals("����") && result2.equals("����") || result1.equals("����") && result2.equals("����")
								|| result1.equals("��") && result2.equals("��")) {
							
							st_buffer.append("����� �����ϴ�!!");
							player1.send(st_buffer.toString());
							player2.send(st_buffer.toString());
							result1 = result2 = null;
							player1 = player2 = null;
							break;
						}
						
						if (result1.equals("����") && result2.equals("��") || result1.equals("����") && result2.equals("����")
								|| result1.equals("��") && result2.equals("����")) {
							
							st_buffer.append("���ӿ��� �̰���ϴ�!!");
							player1.send(st_buffer.toString());
							
							st_buffer.setLength(0);
							st_buffer.append(YES_RESULT);
							st_buffer.append(SEPARATOR);
							st_buffer.append("���ӿ��� �����ϴ�!!");
							player2.send(st_buffer.toString());
							result1 = result2 = null;
							player1 = player2 = null;
							break;
						}
						
						if (result1.equals("��") && result2.equals("����") || result1.equals("����") && result2.equals("����")
								|| result1.equals("����") && result2.equals("��")) {
							
							st_buffer.append("���ӿ��� �����ϴ�!!");
							player1.send(st_buffer.toString());
							
							st_buffer.setLength(0);
							st_buffer.append(YES_RESULT);
							st_buffer.append(SEPARATOR);
							st_buffer.append("���ӿ��� �̰���ϴ�!!");
							player2.send(st_buffer.toString());
							result1 = result2 = null;
							player1 = player2 = null;
							break;
						}
					}
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

	private void modifyLogonUsers(String ids) throws IOException {
		st_buffer.setLength(0);
		st_buffer.append(MDY_LOGONIDS);
		st_buffer.append(SEPARATOR);
		st_buffer.append(ids);
		broadcastLogon(st_buffer.toString());
	}

	private void modifyRoomUsers(String ids) throws IOException {
		st_buffer.setLength(0);
		st_buffer.append(MDY_USERIDS);
		st_buffer.append(SEPARATOR);
		st_buffer.append(ids);
		broadcastLogon(st_buffer.toString());
	}
	// �ڿ��� �����Ѵ�.

	public void release() {
	}

	private static synchronized void addRoomUser(String id, String targetID, ServerThread client) {
		roomVector.addElement(id); // ����� ID �߰�
		roomHash.put(id, client); // ����� ID �� Ŭ���̾�Ʈ�� ����� �����带 �����Ѵ�.
		roomVector.addElement(targetID); // ����� targetID �߰�
		roomHash.put(targetID, client); // ����� targetID �� Ŭ���̾�Ʈ�� ����� �����带 �����Ѵ�.
	}

	private static synchronized int addUser(String id, ServerThread client) {
		if (checkUserID(id) != null) {
			return MSG_ALREADYUSER;
		}
		if (logonHash.size() >= GameServer.cs_maxclient) {
			return MSG_SERVERFULL;
		}
		logonVector.addElement(id); // ����� ID �߰�
		logonHash.put(id, client); // ����� ID �� Ŭ���̾�Ʈ�� ����� �����带 �����Ѵ�.
		client.st_ID = id;
		return 0; // Ŭ���̾�Ʈ�� ���������� �����ϰ�, ��ȭ���� �̹� ������ ����.
	}

	private static synchronized void delLogUser(String id, ServerThread client) {
		logonVector.removeElement(id); // ����� ID ����
		logonHash.remove(id, client);
	}

	private static synchronized void delRoomUser(String id, ServerThread client) {
		roomVector.removeElement(id);
		roomHash.remove(id, client);
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
