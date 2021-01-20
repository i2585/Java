package chapter13;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class MulticastChatC extends Frame implements Runnable, WindowListener, ActionListener, KeyListener {

	TextArea display;
	TextField wtext, ltext;
	Label mlbl, wlbl, loglbl;
	Button logout;
	BufferedWriter output;
	BufferedReader input;
	Socket client;
	MulticastSocket socket;
	StringBuffer clientdata;
	String serverdata;
	String ID;
	Panel plabel, ptotal;
	int port;
	InetAddress group;
	String address, p;
	DatagramPacket incoming;
	Thread CT;
	private static final String SEPARATOR = "|";
	private static final int REQ_LOGON = 1001;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_LOGOUT = 1031;

	public MulticastChatC() {
		super("클라이언트");

		mlbl = new Label("채팅 상태를 보여줍니다.");
		add(mlbl, BorderLayout.NORTH);

		display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
		display.setEditable(false);
		add(display, BorderLayout.CENTER);

		ptotal = new Panel(new BorderLayout());

		Panel pword = new Panel(new BorderLayout());
		wlbl = new Label("대화말");
		wtext = new TextField(30);
		wtext.addKeyListener(this);
		pword.add(wlbl, BorderLayout.WEST);
		pword.add(wtext, BorderLayout.EAST);
		ptotal.add(pword, BorderLayout.CENTER);

		plabel = new Panel(new BorderLayout());
		loglbl = new Label("로그온");
		ltext = new TextField(30);
		ltext.addActionListener(this);
		plabel.add(loglbl, BorderLayout.WEST);
		plabel.add(ltext, BorderLayout.CENTER);
		ptotal.add(plabel, BorderLayout.SOUTH);
		add(ptotal, BorderLayout.SOUTH);
		setSize(300, 250);
		setVisible(true);
		addWindowListener(this);
	}

	public void runClient() {
		try {
			client = new Socket(InetAddress.getLocalHost(), 5000);
			mlbl.setText("연결된 서버이름 : " + client.getInetAddress().getHostName());
			input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			clientdata = new StringBuffer(2048);
			mlbl.setText("멀티캐스트 채팅 서버에 가입 요청합니다!");
			while (true) {
				serverdata = input.readLine();
				display.append(serverdata + "\r\n");
				incoming = new DatagramPacket(new byte[65508], 65508);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void stop() throws IOException {
		if (CT != null) {
			CT.interrupt();
			CT = null;
			try {
				socket.leaveGroup(group);
			} finally {
				socket.close();
				System.exit(0);
			}
		} else
			System.exit(0);
	}

	public void windowClosing(WindowEvent we) {
		try {
			stop();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void windowOpened(WindowEvent we) {
	}

	public void windowClosed(WindowEvent we) {
	}

	public void windowIconified(WindowEvent we) {
	}

	public void windowDeiconified(WindowEvent we) {
	}

	public void windowActivated(WindowEvent we) {
	}

	public void windowDeactivated(WindowEvent we) {
	}

	public void actionPerformed(ActionEvent ae) {
		ID = ltext.getText();
		if (ID.equals("") != true) {
			if (ae.getSource() == logout) {
				mlbl.setText("사용자 " + ID + " 가 로그아웃 하였습니다.\n");
				try {
					clientdata.setLength(0);
					clientdata.append(REQ_LOGOUT);
					clientdata.append(SEPARATOR);
					clientdata.append(ID);
					output.write(clientdata.toString() + "\r\n");
					output.flush();
					ID = null;
					plabel.removeAll();
					socket.leaveGroup(group);
					loglbl = new Label("로그온");
					ltext = new TextField(30);
					plabel.add(loglbl, BorderLayout.WEST);
					plabel.add(ltext, BorderLayout.CENTER);
					ptotal.add(plabel, BorderLayout.SOUTH);
					plabel.validate();
					ltext.addActionListener(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				mlbl.setText(ID + "(으)로 로그인 하였습니다.");
				try {
					clientdata.setLength(0);
					clientdata.append(REQ_LOGON);
					clientdata.append(SEPARATOR);
					clientdata.append(ID);
					output.write(clientdata.toString() + "\r\n");
					output.flush();
					plabel.removeAll();
					logout = new Button("로그아웃");
					plabel.add(logout, 0);
					plabel.validate();
					logout.addActionListener(this);
					address = serverdata.substring(serverdata.lastIndexOf(" ") + 1, serverdata.indexOf(":"));
					group = InetAddress.getByName(address);
					p = serverdata.substring(serverdata.lastIndexOf(":") + 1, serverdata.indexOf("입"));
					port = Integer.parseInt(p);
					socket = new MulticastSocket(port);
					socket.joinGroup(group);
					CT = new Thread(this);
					CT.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			mlbl.setText("로그인 후 사용하세요!!!");
		}
	}

	protected synchronized void handleIOException(IOException e) {
		try {
			stop();
		} catch (IOException ie) {
			System.out.println(ie);
		}
	}

	public void run() {
		try {
			while (!Thread.interrupted()) {
				incoming.setLength(incoming.getData().length);
				socket.receive(incoming);
				String receive = new String(incoming.getData(), 0, incoming.getLength());
				display.append(receive);
			}
		} catch (IOException e) {
			handleIOException(e);
		}
	}

	public static void main(String args[]) {
		MulticastChatC c = new MulticastChatC();
		c.runClient();
	}

	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
			String message = new String();
			message = wtext.getText();
			if (ID == null) {
				mlbl.setText("로그인 후 사용하세요!!!");
				wtext.setText("");
			} else if (message.equals("") == true) {
				mlbl.setText("메세지를 입력하세요!!!");
			} else {
				try {
					clientdata.setLength(0);
					clientdata.append(REQ_SENDWORDS);
					clientdata.append(SEPARATOR);
					clientdata.append(ID);
					clientdata.append(SEPARATOR);
					clientdata.append(message);
					output.write(clientdata.toString() + "\r\n");
					output.flush();
					wtext.setText("");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void keyReleased(KeyEvent ke) {
	}

	public void keyTyped(KeyEvent ke) {
	}
}