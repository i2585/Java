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
		super("ȣ��Ʈ ���� �б�");
		setLayout(new BorderLayout());
		enter = new TextField("URL�� �Է��ϼ���!");
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
			line="����Ʈ ���� : " + uc.getContentType() + "\n";
			buffer.append(line);
			line="����Ʈ ���ڵ� : " + uc.getContentEncoding()+ "\n";
			buffer.append(line);
			line="�������۳�¥ : " + new Date(uc.getDate()) + "\n";
			buffer.append(line);
			line="����������¥ : " + new Date(uc.getLastModified()) + "\n";
			buffer.append(line);
			line="�������⳯¥ : " + new Date(uc.getExpiration()) + "\n";
			buffer.append(line);
			line="�������� : " + uc.getContentLength() + "\n";
			buffer.append(line);
			contents.setText(buffer.toString());
			input.close();
		}catch(MalformedURLException mal) {
			contents.setText("URL ������ �߸��Ǿ����ϴ�.");
		}catch(IOException io) {
			contents.setText(io.toString());
		}catch(Exception ex) {
			contents.setText("ȣ��Ʈ ��ǻ���� ���ϸ��� �� �� �ֽ��ϴ�.");
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
			System.out.println("�Էµ� URL�� �߸��� URL �Դϴ�.");
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
