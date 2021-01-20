package chapter3;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;

public class ex_03_01 extends Frame implements ActionListener{
	private TextField enter;
	private TextArea output,output2;
	public ex_03_01(){
		super("File Ŭ���� �׽�Ʈ");
		enter = new TextField("���� �� ���丮���� �Է��ϼ���");
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
		SimpleDateFormat date = new SimpleDateFormat("yyyy�� MM�� dd�� (w����)hh�� mm��");
		long lastModified = name.lastModified();
		if(name.exists()) {
			output.setText(name.getName() + "�� �����Ѵ�.\n" + 
					(name.isFile() ? "�����̴�.\n" : "������ �ƴϴ�.\n") +
					(name.isDirectory() ? "���丮�̴�.\n" : "���丮�� �ƴϴ�.\n") +
					(name.isAbsolute() ? "�������̴�.\n" : "�����ΰ� �ƴϴ�.\n") +
					"������ ������¥�� : " + date.format(lastModified) +
					"\n������ ���̴� : " + name.length() );
			try {
			output2.setText("�����δ� : " + name.getAbsolutePath() +
					"\n���� ���丮�� : " + name.getParent() +
					"\n��¥��δ� : " + name.getCanonicalPath());
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
				output2.append("���丮�� ������ :\n");
				for(int i=0; i < directory.length; i++ )
					output2.append(directory[i]+"\n");
			}
		}
		else {
			output.setText(e.getActionCommand() + "�� �������� �ʴ´�.\n");
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
