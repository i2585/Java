package chapter5;

import java.awt.event.*;
import java.awt.*;
import java.net.*;

public class ex_05_02 extends Frame implements ActionListener{
	TextField hostname;
	Button getinfor;
	TextArea display,display2;
	public static void main(String args[]) {
		ex_05_02 host = new ex_05_02("InetAddress 클래스");
		host.setVisible(true);
	}
	public ex_05_02(String str) {
		super(str);
		addWindowListener(new WinListener());
		setLayout(new BorderLayout());
		Panel inputpanel = new Panel();
		inputpanel.setLayout(new BorderLayout());
		inputpanel.add("North", new Label("호스트 이름:"));
		hostname = new TextField("",30);
		getinfor = new Button("호스트 정보 얻기");
		inputpanel.add("Center", hostname);
		inputpanel.add("South",getinfor);
		getinfor.addActionListener(this);
		add("North", inputpanel);
		Panel outputpanel = new Panel();
		outputpanel.setLayout(new BorderLayout());
		display = new TextArea("", 24, 40);
		display.setEditable(false);
		outputpanel.add("North", new Label("인터넷 주소"));
		outputpanel.add("Center", display);
		add("Center", outputpanel);
		Panel outputpanel2 = new Panel();
		outputpanel2.setLayout(new BorderLayout());
		display2 = new TextArea("", 10, 10);
		display2.setEditable(false);
		outputpanel2.add("North", new Label("Class & hashcode"));
		outputpanel2.add("Center", display2);
		add("South", outputpanel2);
		setSize(300, 450);
	}
	public void actionPerformed(ActionEvent e) {
		String name = hostname.getText();
		try {
			InetAddress inet = InetAddress.getByName(name);
			String ip = inet.getHostName()+"\n";
			display.append(ip);
			ip=inet.getHostAddress()+"\n";
			display.append(ip);
			display.append("모든주소\n");
			InetAddress[] addresses = InetAddress.getAllByName(name);
			for(int i=0; i<addresses.length; i++)
				display.append(addresses[i].toString()+"\n");
			ip=ipClass(inet.getAddress())+"\n";
			display2.append(ip);
			ip=inet.getHostAddress();
			display2.append(ip.hashCode()+"\n");
		}catch(UnknownHostException ue) {
			String ip = name+": 해당 호스트가 없습니다.\n";
			display.append(ip);
			display2.append(ip);
		}
	}
	static char ipClass(byte[] ip) {
		int highByte = 0xff & ip[0];
		return(highByte<128) ? 'A' : (highByte<192) ? 'B' : (highByte<224) ? 'C' : (highByte<240) ? 'D' : 'E';
	}
	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}
