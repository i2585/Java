package chapter3;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;

public class ex_03_01 extends Frame implements ActionListener{
	private TextField enter;
	private TextArea output,output2;
	public ex_03_01(){
		super("File 클래스 테스트");
		enter = new TextField("파일 및 디렉토리명을 입력하세요");
		enter.addActionListener(this);
		output = new TextArea();
		output2 = new TextArea();
		add(enter, BorderLayout.NORTH);
		add(output, BorderLayout.CENTER);
		add(output2, BorderLayout.SOUTH);
		addWindowListener(new WinListener());
		setSize(400,400);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		File name = new File(e.getActionCommand());
		SimpleDateFormat date = new SimpleDateFormat("yyyy년 MM월 dd일 (w요일)hh시 mm분");
		long lastModified = name.lastModified();
		if(name.exists()) {
			output.setText(name.getName() + "이 존재한다.\n" + 
					(name.isFile() ? "파일이다.\n" : "파일이 아니다.\n") +
					(name.isDirectory() ? "디렉토리이다.\n" : "디렉토리가 아니다.\n") +
					(name.isAbsolute() ? "절대경로이다.\n" : "절대경로가 아니다.\n") +
					"마지막 수정날짜은 : " + date.format(lastModified) +
					"\n파일의 길이는 : " + name.length() );
			try {
			output2.setText("절대경로는 : " + name.getAbsolutePath() +
					"\n상위 디렉토리는 : " + name.getParent() +
					"\n진짜경로는 : " + name.getCanonicalPath());
			}catch(IOException e3) {
			}
			if(name.isFile()) {
				try {
					RandomAccessFile r = new RandomAccessFile(name, "r");
					StringBuffer buf = new StringBuffer();
					String text;
					output2.append("\n\n");
					while((text=r.readLine()) != null)
						buf.append(text + "\n");
					output2.append(buf.toString());
				}catch(IOException e2) {
				}
			}
			else if(name.isDirectory()) {
				String directory[] = name.list();
				output2.append("\n\n");
				output2.append("디렉토리의 내용은 :\n");
				for(int i=0; i < directory.length; i++ )
					output2.append(directory[i]+"\n");
			}
		}
		else {
			output.setText(e.getActionCommand() + "은 존재하지 않는다.\n");
		}
	}
	public static void main(String args[]) {
		ex_03_01 f = new ex_03_01();
	}
	class WinListener extends WindowAdapter{
	public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}
