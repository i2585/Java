package chapter13;

import java.net.*;
import java.util.*;
import java.util.List;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class MulticastChatS extends Frame implements WindowListener, ActionListener {
	protected InetAddress group;
	protected int port;
	protected Frame frame;
	protected TextArea display;
	protected Label info;
	protected ServerThread SThread;
	protected MulticastSocket socket;
	List<ServerThread> list;

	public MulticastChatS(InetAddress group, int port) {
		this.group = group;
		this.port = port;
		initAWT();
	}

	protected void initAWT() {
		frame = new Frame("서버");
		frame.addWindowListener(this);
		info = new Label("멀티캐스트 채팅 그룹 주소 : " + group.getHostAddress());
		display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
		display.setEditable(false);
		frame.setLayout(new BorderLayout());
		frame.add(info, "Center");
		frame.add(display, "South");
		frame.pack();
		frame.setVisible(true);
	}

	public synchronized void start() throws IOException {
		ServerSocket server;
		Socket sock;
		try {
			list = new ArrayList<ServerThread>();
			server = new ServerSocket(5000, 100);
			try {
				while (true) {
					initNet();
					sock = server.accept();
					SThread = new ServerThread(this, sock, socket, display, info, group, port);
					SThread.start();
				}
			} catch (IOException ioe) {
				server.close();
				ioe.printStackTrace();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	protected void initNet() throws IOException {
		socket = new MulticastSocket(port);
		socket.setTimeToLive(1);
		socket.joinGroup(group);
	}

	public void windowClosing(WindowEvent we) {
		try {
			socket.leaveGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
			System.exit(0);
		}
	}

	public void actionPerformed(ActionEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public static void main(String args[]) throws IOException {
		if ((args.length != 1) || (args[0].indexOf(":") < 0))
			throw new IllegalArgumentException("잘못된 멀티캐스트 주소입니다.");
		int idx = args[0].indexOf(":");
		InetAddress group = InetAddress.getByName(args[0].substring(0, idx));
		int port = Integer.parseInt(args[0].substring(idx + 1));
		MulticastChatS chat = new MulticastChatS(group, port);
		chat.start();
	}
}

class ServerThread extends Thread {
	Socket sock;
	MulticastSocket socket;
	BufferedWriter output;
	BufferedReader input;
	TextArea display;
	Label info;
	int port;
	InetAddress group;
	String clientdata;
	String serverdata = "";
	MulticastChatS ms;
	DatagramPacket outgoing;
	byte[] data = new byte[100];
	private static final String SEPARATOR = "|";
	private static final int REQ_LOGON = 1001;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_LOGOUT = 1031;

	public ServerThread(MulticastChatS m, Socket s, MulticastSocket socket, TextArea ta, Label l, InetAddress id,
			int p) {
		ms = m;
		sock = s;
		this.socket = socket;
		display = ta;
		info = l;
		group = id;
		port = p;
		try {
			input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			outgoing = new DatagramPacket(new byte[1], 1, group, port);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void run() {
		try {
			while ((clientdata = input.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
				int command = Integer.parseInt(st.nextToken());
				switch (command) {
				case REQ_LOGON: {
					ms.list.add(this);
					String ID = st.nextToken();
					display.append("클라이언트가 " + ID + "(으)로 로그인 하였습니다.\r\n");
					output.write("멀티캐스트 채팅 그룹 주소는 " + group.getHostAddress() + ":" + port + "입니다.\r\n");
					output.flush();
					break;
				}
				case REQ_LOGOUT: {
					ms.list.remove(this);
					String ID = st.nextToken();
					display.append("클라이언트 " + ID + "가 로그아웃 하였습니다.\r\n");
					break;
				}
				case REQ_SENDWORDS: {
					String ID = st.nextToken();
					String message = st.nextToken();
					String datas = ID + " : " + message + "\r\n";
					data = datas.getBytes();
					display.append(datas);
					outgoing.setData(data);
					socket.send(outgoing);
					break;
				}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			sock.close();
		} catch (IOException ea) {
			ea.printStackTrace();
		}
	}
}
