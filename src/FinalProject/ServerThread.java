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

	/* 로그온된 사용자 저장 */
	private static Hashtable<String, ServerThread> logonHash;
	private static Vector<String> logonVector;
	/* 게임 참여자 저장 */
	private static Hashtable<String, ServerThread> roomHash;
	private static Vector<String> roomVector;

	private static final String SEPARATOR = "|"; // 메시지간 구분자
	private static final String DELIMETER = "`"; // 소메시지간 구분자

	public String st_ID; // ID 저장
	public static ServerThread player1 = null;
	public static ServerThread player2 = null;
	public static String result1 = null;
	public static String result2 = null;

	// 메시지 패킷 코드 및 데이터 정의

	// 클라이언트로부터 전달되는 메시지 코드
	private static final int REQ_LOGON = 1001;
	private static final int REQ_PLAYGAME = 1011;
	private static final int REQ_QUITROOM = 1021;
	private static final int REQ_LOGOUT = 1031;
	private static final int REQ_RESULT = 1041;

	// 클라이언트에 전송하는 메시지 코드
	private static final int YES_LOGON = 2001;
	private static final int NO_LOGON = 2002;
	private static final int MDY_USERIDS = 2013;
	private static final int MDY_LOGONIDS = 2014;
	private static final int YES_LOGOUT = 2031;
	private static final int YES_PLAYGAME = 2011;
	private static final int NO_PLAYGAME = 2012;
	private static final int YES_QUITROOM = 2022;
	private static final int YES_RESULT = 2041;

	// 에러 메시지 코드
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

				// 로그온 시도 메시지 PACKET : REQ_LOGON|ID
				case REQ_LOGON: {
					int result;
					String id = st.nextToken(); // 클라이언트의 ID를 얻는다.
					result = addUser(id, this);
					st_buffer.setLength(0);
					if (result == 0) { // 접속을 허용한 상태
						st_buffer.append(YES_LOGON);
						// YES_LOGON|개설시각|ID1`ID2`..
						st_buffer.append(SEPARATOR);
						String userIDs = getUsers(); // 대화방 참여 사용자ID를 구한다
						st_buffer.append(userIDs);
						send(st_buffer.toString());
						modifyLogonUsers(userIDs);
					} else { // 접속불가 상태
						st_buffer.append(NO_LOGON); // NO_LOGON|errCode
						st_buffer.append(SEPARATOR);
						st_buffer.append(result); // 접속불가 원인코드 전송
						send(st_buffer.toString());
					}
					break;
				}

				// LOGOUT 전송 시도 메시지
				// PACKET : YES_LOGOUT|탈퇴자ID|탈퇴자 이외의 ids
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
						RoomIDs = "상대방이 나갔습니다.";
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
						if (result1.equals("가위") && result2.equals("가위") || result1.equals("바위") && result2.equals("바위")
								|| result1.equals("보") && result2.equals("보")) {
							
							st_buffer.append("상대방과 비겼습니다!!");
							player1.send(st_buffer.toString());
							player2.send(st_buffer.toString());
							result1 = result2 = null;
							player1 = player2 = null;
							break;
						}
						
						if (result1.equals("가위") && result2.equals("보") || result1.equals("바위") && result2.equals("가위")
								|| result1.equals("보") && result2.equals("바위")) {
							
							st_buffer.append("게임에서 이겼습니다!!");
							player1.send(st_buffer.toString());
							
							st_buffer.setLength(0);
							st_buffer.append(YES_RESULT);
							st_buffer.append(SEPARATOR);
							st_buffer.append("게임에서 졌습니다!!");
							player2.send(st_buffer.toString());
							result1 = result2 = null;
							player1 = player2 = null;
							break;
						}
						
						if (result1.equals("보") && result2.equals("가위") || result1.equals("가위") && result2.equals("바위")
								|| result1.equals("바위") && result2.equals("보")) {
							
							st_buffer.append("게임에서 졌습니다!!");
							player1.send(st_buffer.toString());
							
							st_buffer.setLength(0);
							st_buffer.append(YES_RESULT);
							st_buffer.append(SEPARATOR);
							st_buffer.append("게임에서 이겼습니다!!");
							player2.send(st_buffer.toString());
							result1 = result2 = null;
							player1 = player2 = null;
							break;
						}
					}
					break;
				}
				} // switch 종료

				Thread.sleep(100);
			} // while 종료

		} catch (NullPointerException e) { // 로그아웃시 st_in이 이 예외를 발생하므로
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
	// 자원을 해제한다.

	public void release() {
	}

	private static synchronized void addRoomUser(String id, String targetID, ServerThread client) {
		roomVector.addElement(id); // 사용자 ID 추가
		roomHash.put(id, client); // 사용자 ID 및 클라이언트와 통신할 스레드를 저장한다.
		roomVector.addElement(targetID); // 사용자 targetID 추가
		roomHash.put(targetID, client); // 사용자 targetID 및 클라이언트와 통신할 스레드를 저장한다.
	}

	private static synchronized int addUser(String id, ServerThread client) {
		if (checkUserID(id) != null) {
			return MSG_ALREADYUSER;
		}
		if (logonHash.size() >= GameServer.cs_maxclient) {
			return MSG_SERVERFULL;
		}
		logonVector.addElement(id); // 사용자 ID 추가
		logonHash.put(id, client); // 사용자 ID 및 클라이언트와 통신할 스레드를 저장한다.
		client.st_ID = id;
		return 0; // 클라이언트와 성공적으로 접속하고, 대화방이 이미 개설된 상태.
	}

	private static synchronized void delLogUser(String id, ServerThread client) {
		logonVector.removeElement(id); // 사용자 ID 삭제
		logonHash.remove(id, client);
	}

	private static synchronized void delRoomUser(String id, ServerThread client) {
		roomVector.removeElement(id);
		roomHash.remove(id, client);
	}

	/*
	 * 접속을 요청한 사용자의 ID와 일치하는 ID가 이미 사용되는 지를 조사한다. 반환값이 null이라면 요구한 ID로 대화방 입장이 가능함.
	 */
	private static ServerThread checkUserID(String id) {
		ServerThread alreadyClient = null;
		alreadyClient = (ServerThread) logonHash.get(id);
		return alreadyClient;
	}

	// 로그온에 참여한 사용자 ID를 구한다.
	private String getUsers() {
		StringBuffer id = new StringBuffer();
		String ids;
		Enumeration<String> enu = logonVector.elements();
		while (enu.hasMoreElements()) {
			id.append(enu.nextElement());
			id.append(DELIMETER);
		}
		try {
			ids = new String(id); // 문자열로 변환한다.
			ids = ids.substring(0, ids.length() - 1); // 마지막 "`"를 삭제한다.
		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}
		return ids;
	}

	// 대화방에 참여한 사용자 ID를 구한다.

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
			ids = ids.substring(0, ids.length() - 1); // 마지막 "`"를 삭제한다.
		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}
		return ids;
	}

	// 대화방에 참여한 모든 사용자(브로드케스팅)에게 데이터를 전송한다.
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

	// 데이터를 전송한다.
	public void send(String sendData) throws IOException {
		synchronized (st_out) {
			st_out.writeUTF(sendData);
			st_out.flush();
		}
	}
}
