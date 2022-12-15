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

public class SimpleChatServer extends JFrame implements ActionListener {
	public static void main(String[] args) {
		new SimpleChatServer();
	}

	static JTextField portInput;
	JButton connectButton;
	JLabel portLb;
	static int portNum;
	static boolean running = false;

	SimpleChatServer() {
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

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == connectButton) {
			if (!running) {
				portNum = Integer.parseInt(portInput.getText());
				portInput.setEditable(false);
				try {
					System.out.println("IPアドレス：" + Inet4Address.getLocalHost().getHostAddress());
					System.out.println("ポート番号：" + portNum);
					PrintWriter out = new PrintWriter(
							new BufferedWriter(new FileWriter(new File("C://java/port.txt"))));
					out.write(Inet4Address.getLocalHost().getHostAddress() + "\r\n" + portNum);
					out.close();
					System.err.println("サーバー起動ちう");

					Loop t = new Loop();
					t.start();
					running = true;
					connectButton.setText("サーバー停止");

				} catch (Exception e) {
					// e.printStackTrace();
				}

			} else {
				System.err.println("サーバー停止ちう");
				running = false;
				connectButton.setText("サーバー起動");
				portInput.setEditable(true);
				ChatThread.stopServer();
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
			// e1.printStackTrace();
		}

	}

	public static void talk(String message) {

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
			// e.printStackTrace();
		}
	}
	
	public static void stopServer() {
		talk("サーバーが停止しました");
	}
}

class Loop extends Thread {
	static ServerSocket ss;
	static Socket cs;

	public void run() {
		try {
			ss = new ServerSocket(SimpleChatServer.portNum);
			cs = null;
			while (SimpleChatServer.running) {
				cs = ss.accept();
				ChatThread chat = new ChatThread(cs);
				chat.start();
				System.out.println("新しいスレッドが建てられました");
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}

}
