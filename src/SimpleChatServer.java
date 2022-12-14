import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.*;

public class SimpleChatServer extends JFrame implements ActionListener {

	JTextField portInput;
	JButton connectButton;
//	JToggleButton connectButton2;
	JLabel portLb;
	int portNum;

	public static void main(String[] args) {
		new SimpleChatServer();
//
//		try {
//			System.out.println(Inet4Address.getLocalHost().getHostAddress());
//			System.err.println("サーバー起動ちう");
//			ServerSocket ss = new ServerSocket(portNum);
//
//			PrintWriter addrPort = new PrintWriter(new BufferedWriter(new FileWriter(new File("C:/java/server.txt"))));
//			addrPort.write(Inet4Address.getLocalHost().getHostAddress());
//			addrPort.close();
//
//			Socket cs = null;
//			while (true) {
//				cs = ss.accept();
//				ChatThread chat = new ChatThread(cs);
//				chat.start();
//				System.out.println("新しいスレッドが建てられました");
//			}
//		} catch (Exception e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
	}

	SimpleChatServer() {
		portInput = new JTextField(4);
		connectButton = new JButton("サーバー起動");
		portLb = new JLabel("ポート番号");
//		connectButton2=new JToggleButton("接続");
		Container container = this.getContentPane();
		container.setLayout(new FlowLayout());
		container.add(portLb);
		container.add(portInput);
		container.add(connectButton);
//		container.add(connectButton2);
//		connectButton2.addActionListener(this);
//		connectButton2.setActionCommand("connectButton2");
		connectButton.addActionListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 300, 80);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO 自動生成されたメソッド・スタブ
//		String actionCommand = ae.getActionCommand();
//		if(actionCommand.equals("connectButton2")) {
//			System.out.println(connectButton2.isSelected());
//			portNum = Integer.parseInt(portInput.getText());
//			portInput.setEditable(false);
//			try {
//				System.out.println(Inet4Address.getLocalHost().getHostAddress());
//				System.out.println(portNum);
//				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File("C://java/port.txt"))));
//				out.write(Inet4Address.getLocalHost().getHostAddress() + "\r\n" + portNum);
//				out.close();
//				System.err.println("サーバー起動ちう");
//				ServerSocket ss = new ServerSocket(portNum);
//				Socket cs = null;
//				while (connectButton2.isSelected()) {
//					cs = ss.accept();
//					ChatThread chat = new ChatThread(cs);
//					chat.start();
//					System.out.println("新しいスレッドが建てられました");
//				}
//			} catch (Exception e) {
//				// TODO 自動生成された catch ブロック
//				e.printStackTrace();
//			}
//		}
		if (ae.getSource() == connectButton) {
			portNum = Integer.parseInt(portInput.getText());
			portInput.setEditable(false);
			try {

				System.out.println(Inet4Address.getLocalHost().getHostAddress());
				System.out.println(portNum);

				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File("C://java/port.txt"))));
				out.write(Inet4Address.getLocalHost().getHostAddress() + "\r\n" + portNum);
				out.close();

				System.err.println("サーバー起動ちう");
				ServerSocket ss = new ServerSocket(portNum);
				
				Thread stop = new Thread(new ServerStop());
				stop.start();

				Socket cs = null;
				while (true) {
					cs = ss.accept();
					ChatThread chat = new ChatThread(cs);
					chat.start();
					System.out.println("新しいスレッドが建てられました");
				}
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}
}

class ChatThread extends Thread {

	Socket cs;
	static Vector<ChatThread> threads;

	public ChatThread(Socket cs) {
		super();
		this.cs = cs;
		if (threads == null) {
			threads = new Vector<ChatThread>();
		}
		threads.add(this);
	}

	public void run() {
		try {
			System.err.println("接続しました");
			BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(cs.getOutputStream())));
//			out.println(in.readLine());
//			out.flush();
			while (true) {
				try {
					System.out.println("クライアントからの入力を待ち受けています");
					String message = in.readLine();
					if (message == null) {
						cs.close();
						threads.remove(this);
						return;
					}
					System.out.println("クライアントから" + message + "を受け取りました");
					talk(message);
				} catch (Exception e) {
					System.err.println("接続が切れました");
					cs.close();
					in.close();
					out.close();
					threads.remove(this);
					return;
				}
			}

		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}

	}

	public void talk(String message) {

		for (int i = 0; i < threads.size(); i++) {
			ChatThread thread = (ChatThread) threads.get(i);
			if (thread.isAlive()) {
				thread.talkone(message);
			}
		}
	}

	public void talkone(String message) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(cs.getOutputStream())));
			out.println(message);
			out.flush();
			System.out.println(message + "を送りました");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}

class ServerStop extends JFrame implements ActionListener, Runnable {

	ServerStop() {
//		portInput = new JTextField(4);
//		connectButton = new JButton("サーバー起動");
//		portLb = new JLabel("ポート番号");
////		connectButton2=new JToggleButton("接続");
//		Container container = this.getContentPane();
//		container.setLayout(new FlowLayout());
//		container.add(portLb);
//		container.add(portInput);
//		container.add(connectButton);
////		container.add(connectButton2);
////		connectButton2.addActionListener(this);
////		connectButton2.setActionCommand("connectButton2");
//		connectButton.addActionListener(this);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setBounds(200, 200, 300, 80);
//		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 300, 80);
		setVisible(true);
	}

}