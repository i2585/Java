package chapter11;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
public class ex_11_02_server extends Frame implements ActionListener{
	private TextField enter;
	private TextArea display;
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket socket;
	public ex_11_02_server() {
		super("서버");
		enter = new TextField("메시지를 입력하세요");
		enter.addActionListener(this);
		add(enter, BorderLayout.NORTH);
		display = new TextArea();
		add(display, BorderLayout.CENTER);
		addWindowListener(new WinListener());
		setSize(400,300);
		setVisible(true);
		try {
			socket = new DatagramSocket(5000);
		}catch(SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}
	public void waitForPackets() {
		while(true) {
			try {
				byte data[] = new byte[100];
				receivePacket = new DatagramPacket(data, data.length);
				socket.receive(receivePacket);
				display.append("\n수신 메시지 : " + new String(receivePacket.getData()));
			}catch(IOException io) {
				display.append(io.toString() + "\n");
				io.printStackTrace();
			}
		}
	}
	public void actionPerformed(ActionEvent e) {
		try {
			display.append("\n송신 메시지: " + e.getActionCommand());
			String s = e.getActionCommand();
			byte data[] = s.getBytes();
			sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 4000);
			socket.send(sendPacket);
		}catch ( IOException exception )
		  {
		     display.append( exception.toString() + "\n" );
		     exception.printStackTrace();
		  }
	}
	public static void main(String args[]) {
		ex_11_02_server s = new ex_11_02_server();
		s.waitForPackets();
	}
	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
}
