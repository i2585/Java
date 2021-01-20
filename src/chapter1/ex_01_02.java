package chapter1;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ex_01_02 extends Frame implements ActionListener{
	Label ifile, ofile, tarea;
	TextField idata, odata;
	TextArea areadata;
	Button b1, b2;
	String file1, file2;
	public ex_01_02(String str) {
		super(str);
		setLayout(new FlowLayout());
		
		ifile = new Label("입력파일");
		add(ifile);
		idata = new TextField(20);
		add(idata);
		b1 = new Button("확인");
		b1.addActionListener(this);
		add(b1);
		
		ofile = new Label("출력파일");
		add(ofile);
		odata = new TextField(20);
		add(odata);
		b2 = new Button("확인");
		b2.addActionListener(this);
		add(b2);
		tarea = new Label("파일내용");
		add(tarea);
		areadata = new TextArea(20, 35);
		add(areadata);
		
		addWindowListener(new WinListener());
	}
	public static void main(String args[]) {
		ex_01_02 text = new ex_01_02("");
		text.setSize(300,400);
		text.setVisible(true);
	}
	public void actionPerformed(ActionEvent ae) {
		file1 = idata.getText(); 
		file2 = odata.getText();
		FileInputStream fin = null;
		FileOutputStream fout = null;
		try {
				int byteRead;
				byte[] buffer = new byte[256];
				fin = new FileInputStream(file1);
				if(ae.getSource() == b2) {
					fout = new FileOutputStream(file2);
					while((byteRead = fin.read(buffer)) >= 0)
						fout.write(buffer, 0, byteRead);
					fin = new FileInputStream(file2);
					fin.read(buffer);
					String data = new String(buffer);
					areadata.setText(data + "\n");
				}
		}
		catch(IOException e) {
			System.out.println(e.toString());
		}
		finally {
			try {
            	if(fin != null)  fin.close();
            	if(fout != null)  fout.close();
        	}catch(IOException e) {}
		}
	}
	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}