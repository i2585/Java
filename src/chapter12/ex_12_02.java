package chapter12;

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class ex_12_02 extends Frame implements ActionListener{
	TextField enter;
	TextArea content;
	public ex_12_02() {
		super("호스트 파일 읽기");
		setLayout(new BorderLayout());
		enter = new TextField("URL를 입력하세요!");
		enter.addActionListener(this);
		add(enter,BorderLayout.NORTH);
		
		content = new TextArea();
		add(content,BorderLayout.CENTER);
		addWindowListener(new WinListener());
		setSize(450,350);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		URL u;
		URLConnection uc;
		int i=1;
		InputStream is;
		BufferedReader input;
		String line;
		StringBuffer buffer = new StringBuffer();
		String location = e.getActionCommand();
		HttpURLConnection response;
		try {
			u = new URL(location);
			uc = u.openConnection();
			is=uc.getInputStream();
			input = new BufferedReader(new InputStreamReader(is));
			response = (HttpURLConnection) u.openConnection();
			buffer.append("응답코드: " + response.getResponseCode() + "\n");
			buffer.append("응답메시지: " + response.getResponseMessage() + "\n");
			while(true) {
				line = uc.getHeaderFieldKey(i) + ": " +uc.getHeaderField(i) + "\n";
				buffer.append(line);
				i++;
				if(uc.getHeaderField(i)==null)
					break;
			}
			while((line=input.readLine())!=null)
				buffer.append(line).append("\n");
			content.setText(buffer.toString());
		}catch(MalformedURLException mal) {
			content.setText("URL 형식이 잘못되었습니다.");
		}catch(IOException io) {
			content.setText(io.toString());
		}
	}
	public static void main(String args[]) {
		ex_12_02 read = new ex_12_02();
	}
	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}
