package FinalProject;

import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.JOptionPane;

public class ClientThread<roomusers> extends Thread {

	private GameClient ct_client; // ChatClient 객체
	private Socket ct_sock; // 클라이언트 소켓
	private DataInputStream ct_in; // 입력 스트림
	private DataOutputStream ct_out; // 출력 스트림
	private StringBuffer ct_buffer; // 버퍼
	private Thread thisThread;
	private GameRoom room;

	String myLogonID = null;
	private static final String SEPARATOR = "|";
	private static final String DELIMETER = "`";

	// 메시지 패킷 코드 및 데이터 정의

	// 서버에 전송하는 메시지 코드
	private static final int REQ_LOGON = 1001;
	private static final int REQ_PLAYGAME = 1011;
	private static final int REQ_QUITROOM = 1021;
	private static final int REQ_LOGOUT = 1031;
	private static final int REQ_RESULT = 1041;
	
	// 서버로부터 전송되는 메시지 코드
	private static final int YES_LOGON = 2001;
	private static final int NO_LOGON = 2002;
	private static final int YES_PLAYGAME = 2011;
	private static final int NO_PLAYGAME = 2012;
	private static final int MDY_USERIDS = 2013;
	private static final int MDY_LOGONIDS = 2014;
	private static final int YES_LOGOUT = 2031;
	private static final int YES_QUITROOM = 2022;
	private static final int YES_RESULT = 2041;
	
	// 에러 메시지 코드
	private static final int MSG_ALREADYUSER = 3001;
	private static final int MSG_SERVERFULL = 3002;
	private static final int MSG_CANNOTOPEN = 3011;
	private static final int MSG_ALREADYPLAYER = 3012;
	private static final int MSG_REJECT = 3013;

	private static MessageBox msgBox, logonbox;

	/*
	 * 원격호스트와 연결을 위한 생성자 실행 : java ChatClient 호스트이름 포트번호 To DO .....
	 */

	// 로컬호스트에서 사용하기 위하여 만든 생성자
	// 서버와 클라이언트가 같은 시스템을 사용한다.
	public ClientThread(GameClient client) {
		try {
			ct_sock = new Socket(InetAddress.getLocalHost(), 2777);
			ct_in = new DataInputStream(ct_sock.getInputStream());
			ct_out = new DataOutputStream(ct_sock.getOutputStream());
			ct_buffer = new StringBuffer(4096);
			thisThread = this;
			ct_client = client; // 객체변수에 할당
		} catch (IOException e) {
			MessageBoxLess msgout = new MessageBoxLess(client, "연결에러", "서버에 접속할 수 없습니다.");
			msgout.setVisible(true);
		}
	}

	public void run() {

		try {
			Thread currThread = Thread.currentThread();
			while (currThread == thisThread) { // 종료는 LOG_OFF에서 thisThread=null;에 의하여
				String recvData = ct_in.readUTF();
				StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
				int command = Integer.parseInt(st.nextToken());
				switch (command) {

				// 로그온 성공 메시지 PACKET : YES_LOGON|개설시각|ID1`ID2`ID3...
				case YES_LOGON: {
					logonbox.dispose();
					ct_client.cc_tfStatus.setText("로그온이 성공했습니다.");
					ct_client.cc_tfLogon.setEditable(false);
					String ids = st.nextToken(); // 대화방 참여자 리스트
					StringTokenizer users = new StringTokenizer(ids, DELIMETER);
					ct_client.cc_lstMember.removeAll();
					while (users.hasMoreTokens()) {
						ct_client.cc_lstMember.add(users.nextToken());
					}
					break;
				}

				// 로그온 실패 또는 로그온하고 대화방이 개설되지 않은 상태
				// PACKET : NO_LOGON|errCode
				case NO_LOGON: {
					myLogonID="";
					int errcode = Integer.parseInt(st.nextToken());
					if (errcode == MSG_ALREADYUSER) {
						logonbox.dispose();
						msgBox = new MessageBox(ct_client, "로그온", "이미 다른 사용자가 있습니다.");
						msgBox.setVisible(true);
					} else if (errcode == MSG_SERVERFULL) {
						logonbox.dispose();
						msgBox = new MessageBox(ct_client, "로그온", "대화방이 만원입니다.");
						msgBox.setVisible(true);
					}
					break;
				}


				case MDY_USERIDS: {
					String ids = st.nextToken(); // 게임방 참여자 리스트
					if (ids.equals("상대방이 나갔습니다.")) {
						ct_client.cc_lstGame.removeAll(); // 모든 ID를 삭제한다.
					}
					else if (!ids.equals("")) {
						ct_client.cc_lstGame.removeAll(); // 모든 ID를 삭제한다.
						StringTokenizer roomusers = new StringTokenizer(ids, DELIMETER);
						while (roomusers.hasMoreTokens()) {
							ct_client.cc_lstGame.add(roomusers.nextToken());
						}
					}
					break;
				}

				// 로그온 사용자 리스트를 업데이트 한다.
				// PACKET : MDY_LOGONIDS|id1'id2'id3.....
				case MDY_LOGONIDS: {
					ct_client.cc_lstMember.removeAll(); // 모든 ID를 삭제한다.
					String ids = st.nextToken(); // 대화방 참여자 리스트
					StringTokenizer logonusers = new StringTokenizer(ids, DELIMETER);
					while (logonusers.hasMoreTokens()) {
						ct_client.cc_lstMember.add(logonusers.nextToken());
					}
					break;
				}

				// LOGOUT 메시지 처리
				// PACKET : YES_LOGOUT|탈퇴자id|탈퇴자 제외 id1, id2,....
				case YES_LOGOUT: {
					ct_client.cc_tfStatus.setText("로그아웃이 성공했습니다.");
					ct_client.cc_lstMember.removeAll();
					ct_client.cc_lstGame.clear();
					ct_client.cc_tfLogon.setEditable(true);
					break;
				}
				
				case REQ_PLAYGAME: {
					String id = st.nextToken();
					String targetID = st.nextToken();
					int result = JOptionPane.showConfirmDialog(null, id + "로 부터 게임 요청을 수락하시겠습니까?", "게임요청", JOptionPane.YES_NO_OPTION);
					
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
						room = new GameRoom(this, "게임방", id, targetID);
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
					room = new GameRoom(this, "게임방", myLogonID, targetID);
					room.pack();
					room.show();
					break;
				}
				
				case NO_PLAYGAME: {
					int code = Integer.parseInt(st.nextToken());
					String id = st.nextToken();
					if(code == MSG_ALREADYPLAYER) {
						MessageBox msgout = new MessageBox(ct_client, "상태창", "상대방이 게임에 참여 중 입니다.");
						msgout.setVisible(true);
						break;
					}
					else if(code == MSG_REJECT) {
						MessageBox msgout = new MessageBox(ct_client, "상태창", "상대방이 게임을 거절하였습니다.");
						msgout.setVisible(true);
						break;
					}
				}
				
				case YES_RESULT: {
					String result = st.nextToken();
					JOptionPane.showMessageDialog(null, result, myLogonID + " 상태창", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				
				case YES_QUITROOM: {
					room.dispose();
					ct_client.show();
					break;
				}
				} // switch 종료

				Thread.sleep(200);

			} // while 종료(스레드 종료)

		} catch (InterruptedException e) {
			System.out.println(e);
			release();

		} catch (IOException e) {
			System.out.println(e);
			release();
		}
	}

	// 네트워크 자원을 해제한다.
	public void release() {
	};

	// Logon 패킷(REQ_LOGON|ID)을 생성하고 전송한다.
	public void requestLogon(String id) {
		try {
			myLogonID = id;
			logonbox = new MessageBox(ct_client, "로그온", "서버에 로그온 중입니다.");
			logonbox.setVisible(true);
			ct_buffer.setLength(0); // Logon 패킷을 생성한다.
			ct_buffer.append(REQ_LOGON);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			send(ct_buffer.toString()); // Logon 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	public void requestPlayGame(String targetID) {
		try {
			ct_buffer.setLength(0); // 패킷을 생성한다.
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
			ct_buffer.setLength(0); // 패킷을 생성한다.
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
				ct_buffer.setLength(0); // EnterRoom 패킷을 생성한다.
				ct_buffer.append(REQ_LOGOUT);
				ct_buffer.append(SEPARATOR);
				ct_buffer.append(id);
				send(ct_buffer.toString()); // EnterRoom 패킷을 전송한다.
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
	public void requestSendResult(String input) {
		try {
			ct_buffer.setLength(0); // EnterRoom 패킷을 생성한다.
			ct_buffer.append(REQ_RESULT);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(myLogonID);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(input);
			send(ct_buffer.toString()); // EnterRoom 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// 클라이언트에서 메시지를 전송한다.
	private void send(String sendData) throws IOException {
		ct_out.writeUTF(sendData);
		ct_out.flush();
	}
}