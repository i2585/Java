package chapter12;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
public class ex_12_01 extends Frame implements ActionListener{
	private TextField enter;
	private TextArea contents;
	public ex_12_01() {
		super("호스트 파일 읽기");
		setLayout(new BorderLayout());
		enter = new TextField("URL를 입력하세요!");
		enter.addActionListener(this);
		add(enter,BorderLayout.NORTH);
		contents=new TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		add(contents,BorderLayout.CENTER);
		addWindowListener(new WinListener());
		setSize(450,350);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		URL url;
		InputStream is;
		BufferedReader input;
		String line;
		StringBuffer buffer = new StringBuffer();
		String location = e.getActionCommand();
		URLConnection uc;
		try {
			url=new URL(location);
			uc = url.openConnection();
			is = uc.getInputStream();
			input=new BufferedReader(new InputStreamReader(is));
			line="컨텐트 유형 : " + uc.getContentType() + "\n";
			buffer.append(line);
			line="컨텐트 인코딩 : " + uc.getContentEncoding()+ "\n";
			buffer.append(line);
			line="문서전송날짜 : " + new Date(uc.getDate()) + "\n";
			buffer.append(line);
			line="최종수정날짜 : " + new Date(uc.getLastModified()) + "\n";
			buffer.append(line);
			line="문서만기날짜 : " + new Date(uc.getExpiration()) + "\n";
			buffer.append(line);
			line="문서길이 : " + uc.getContentLength() + "\n";
			buffer.append(line);
			contents.setText(buffer.toString());
			input.close();
		}catch(MalformedURLException mal) {
			contents.setText("URL 형식이 잘못되었습니다.");
		}catch(IOException io) {
			contents.setText(io.toString());
		}catch(Exception ex) {
			contents.setText("호스트 컴퓨터의 파일만을 열 수 있습니다.");
		}
		try {
			url=new URL(location);
			uc = url.openConnection();
			is = uc.getInputStream();
			int cl = uc.getContentLength();
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer1= new byte[cl];
			int bytesread=0;
			int offset=0;
			while(offset<cl) {
				bytesread = bis.read(buffer1, offset, buffer1.length-offset);
				if(bytesread==-1) break;
				offset=offset+bytesread;
			}
			bis.close();
			String path="D://eclipse-workspace/java/download12";
			File Folder=new File(path);
			String filename = url.getFile();
			filename = filename.substring(filename.lastIndexOf('/')+1);
			if(!Folder.exists()) {
				Folder.mkdir();
			}
			FileOutputStream fout = new FileOutputStream(Folder + "/" +filename);
			fout.write(buffer1);
			fout.flush();
			fout.close();
		}catch(MalformedURLException ec) {
			System.out.println("입력된 URL은 잘못된 URL 입니다.");
		}catch(IOException ex) {
			System.out.println(ex);
		}
	}
	public static void main(String args[]) {
		ex_12_01 read = new ex_12_01();
	}
	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}
