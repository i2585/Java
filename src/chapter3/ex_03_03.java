package chapter3;

import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class ex_03_03 extends Frame implements ActionListener
{
	private TextField accountField, nameField, balanceField;
	private Button enter, done;
	private RandomAccessFile output;
	private Record data;
	
	public ex_03_03()
	{
		super("���Ͼ���");
		data = new Record();
		
		try
		{
			output = new RandomAccessFile("customer.txt", "rw");
		}
		catch(IOException e)
		{
			System.err.println(e.toString());
			System.exit(1);
		}
		setSize(350, 150);
		setLayout(new GridLayout(4,2));
		add(new Label("���¹�ȣ"));
		accountField = new TextField();
		add(accountField);
		add(new Label("�̸�"));
		nameField = new TextField(20);
		add(nameField);
		add(new Label("�ܰ�"));
		balanceField = new TextField(20);
		add(balanceField);
		enter=new Button("�Է�");
		enter.addActionListener(this);
		add(enter);
		done=new Button("���");
		done.addActionListener(this);
		add(done);
		addWindowListener(new WinListener());
		setVisible(true);
	}
	
	public void addRecord()
	{
		int accountNo = 0;
		Double d;
		
		if(! accountField.getText().equals(""))
		{
			try
			{
				accountNo = Integer.parseInt(accountField.getText());
				
				if(accountNo > 0 && accountNo <= 100)
				{
					data.setAccount(accountNo);
					data.setName(nameField.getText());
					d = new Double(balanceField.getText());
					data.setBalance(d.doubleValue());
					output.seek((long)(accountNo-1)*Record.size());
					data.write(output);
				}
				accountField.setText("");
				nameField.setText("");
				balanceField.setText("");
			}
			catch(NumberFormatException nfe)
			{
				System.err.println("���ڸ� �Է��ϼ���");
			}
			catch(IOException io)
			{
				System.err.println("���Ͼ��� ����\n"+io.toString());
				System.exit(1);
			}
		}
	}
	
	public void prtRecord()
	{
		int accountNo = Integer.parseInt(accountField.getText());
		
		if(! accountField.getText().equals(""))
		{
			try
			{
				if(accountNo > 0 && accountNo <= 100)
				{
					output.seek((long)(accountNo-1)*Record.size());
					data.read(output);
					accountField.setText("" + data.getAccount());
					nameField.setText(data.getName());
					balanceField.setText("" + data.getBalance());
					
				}
			}
			catch(NumberFormatException nfe)
			{
				System.err.println("���ڸ� �Է��ϼ���");
			}
			catch(IOException io)
			{
				System.err.println("�����б� ����\n"+io.toString());
			}
		}
	}
	
	public static void main(String[] args)
	{
		new ex_03_03();
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == enter)
		{
			addRecord();
		}
		else if (e.getSource() == done)
		{
			prtRecord();
		}
	}
	class WinListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent we)
		{
			System.exit(0);
		}
	}
}

class Record
{
	private int account;
	private String name;
	private double balance;
	
	public void read(RandomAccessFile file) throws IOException
	{
		account = file.readInt();
		char namearray[] = new char[15];
		
		for(int i = 0; i < namearray.length; i++)
		{
			namearray[i] = file.readChar();
		}
		
		name = new String(namearray);
		balance = file.readDouble();
	}
	
	public void write(RandomAccessFile file) throws IOException
	{
		StringBuffer buf;
		file.writeInt(account);
		
		if(name != null)
			buf = new StringBuffer(name);
		else
			buf = new StringBuffer(15);
		
		buf.setLength(15);
		file.writeChars(buf.toString());
		file.writeDouble(balance);
	}
	
	public void setAccount(int a) { account = a;}
	public int getAccount() {return account;}
	public void setName(String f) {name = f;}
	public String getName() {return name;}
	public void setBalance(double b) {balance = b;}
	public double getBalance() {return balance;}
	public static int size() {return 42;}
}