import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class SimpleChatServer {
	public static void main(String[] args) {
		Thread frame = new Thread(new SimpleChatServerFrame());
		frame.start();
	}

	static void connect() {
		SimpleChatServerFrame.portNum = Integer.parseInt(SimpleChatServerFrame.portInput.getText());
		SimpleChatServerFrame.portInput.setEditable(false);
		try {
			System.out.println(Inet4Address.getLocalHost().getHostAddress());
			System.out.println(SimpleChatServerFrame.portNum);
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File("C://java/port.txt"))));
			out.write(Inet4Address.getLocalHost().getHostAddress() + "\r\n" + SimpleChatServerFrame.portNum);
			out.close();
			System.err.println("サーバー起動ちう");
			
			Loop t = new Loop();
			t.start();
			
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
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

class SimpleChatServerFrame extends JFrame implements Runnable, ActionListener {
	static JTextField portInput;
	static JButton connectButton;
	JLabel portLb;
	static int portNum;

	public void run() {

	}

	SimpleChatServerFrame() {
		portInput = new JTextField(4);
		connectButton = new JButton("サーバー起動");
		portLb = new JLabel("ポート番号");
		Container container = this.getContentPane();
		container.setLayout(new FlowLayout());
		container.add(portLb);
		container.add(portInput);
		container.add(connectButton);
		connectButton.addActionListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 300, 80);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == connectButton) {
			SimpleChatServer.connect();
		}
	}
}

class Loop extends Thread {
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(SimpleChatServerFrame.portNum);
			Socket cs = null;
			while (true) {
				cs = ss.accept();
				ChatThread chat = new ChatThread(cs);
				chat.start();
				System.out.println("新しいスレッドが建てられました");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
