package chapter6;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.awt.image.*;

public class ex_06_01 extends Frame implements ActionListener{
	private TextField enter;
	private TextArea contents, con;
	public ex_06_01() {
		super("ȣ��Ʈ ���� �б�");
		setLayout(new BorderLayout());
		enter = new TextField("URL�� �Է��ϼ���!");
		enter.addActionListener(this);
		add(enter,BorderLayout.NORTH);
		contents=new TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		add(contents,BorderLayout.CENTER);
		con=new TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		add(con,BorderLayout.SOUTH);
		addWindowListener(new WinListener());
		setSize(450,350);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		URL url;
		InputStream is;
		BufferedReader input;
		String line,line1,type=null;
		StringBuffer buffer = new StringBuffer();
		String location = e.getActionCommand();
		try {
			url=new URL(location);
			Object o=url.getContent();
			is=url.openStream();
			URLConnection uc = url.openConnection();
			uc.connect();
			type = uc.getContentType();
			input=new BufferedReader(new InputStreamReader(is));
			contents.setText("������ �д� ���Դϴ�....\n");
			line = "Protocol: " + url.getProtocol() + "\n";
			contents.append(line);
			line = "Host Name: " + url.getHost() + "\n";
			contents.append(line);
			line = "Port No: " + url.getPort() + "\n";
			contents.append(line);
			line = "File Name: " + url.getFile() + "\n";
			contents.append(line);
			line = "Hash Code: " + url.hashCode() + "\n";
			contents.append(line);
			if (o instanceof ImageProducer)
				con.setText("image");
			else if (type.contains("audio"))
				con.setText("audio");
			else if (type.contains("video"))
				con.setText("video");
			else if(o instanceof InputStream)
			{
				while((line1 = input.readLine()) != null)
					buffer.append(line1).append('\n');
				con.setText(buffer.toString());
			}
			input.close();
		}catch(MalformedURLException mal) {
			contents.setText("URL ������ �߸��Ǿ����ϴ�.");
		}catch(IOException io) {
			contents.setText(io.toString());
		}catch(Exception ex) {
			contents.setText("ȣ��Ʈ ��ǻ���� ���ϸ��� �� �� �ֽ��ϴ�.");
		}
	}
	public static void main(String args[]) {
		ex_06_01 read = new ex_06_01();
	}
	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}
