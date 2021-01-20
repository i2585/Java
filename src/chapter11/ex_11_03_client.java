package chapter11;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ex_11_03_client extends Frame implements ActionListener{
	private Panel panel;
	private TextField field;
	private boolean status = true;
	private Button[] button;
	private String[] data = {"7","8","9"," * ","4","5","6"," - ","1","2","3"," + ","C","0","="," / "};
	DatagramSocket ds;
	DatagramPacket SendPacket;
	byte buffer[];
	public ex_11_03_client() {
		super("°è»ê±â");
		buffer = new byte[1000];
		field = new TextField(20);
		panel = new Panel();
		field.setText("");
		field.setEnabled(false);
		panel.setLayout(new GridLayout(4,4));
		button = new Button[16];
		
		int x = 0;
		
		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				button[x] = new Button(data[x]);
				button[x].addActionListener(this);
				panel.add(button[x]);
				x++;
			}
		}
		add(field, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		setSize(400,300);
		setVisible(true);
		addWindowListener(new WinListener());
	}
	public void actionPerformed(ActionEvent e) {
		String val = e.getActionCommand();
		
		try
		{
			ds = new DatagramSocket(); 
			if(val.equals("C"))
			{
				field.setText("");
			}
			else if (val.charAt(0) >= '0' && val.charAt(0) <= '9')
			{
					if(status)
						field.setText(val);
					else
						field.setText(field.getText() + val);
					status=false;
			}
			else if (val.equals("="))
			{
				String result = "0.0";
				buffer = field.getText().getBytes();
				try
				{
					SendPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 4000);
					ds.send(SendPacket);
					buffer = new byte[1000]; 
		            DatagramPacket ReceivePacket = new DatagramPacket(buffer, buffer.length); 
		            ds.receive(ReceivePacket);
		            result = new String(buffer,0,buffer.length);
				}catch (IOException ae){
					System.out.println(ae);
				 }	
				field.setText(result + "");
			}
			else
				field.setText(field.getText() + val);
		}catch (Exception ae){
			System.out.println(ae);
		 }	
	}
	public static void main(String args[]) {
		ex_11_03_client s = new ex_11_03_client();
	}
	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
}
