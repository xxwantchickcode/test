package chatFirst;

import java.awt.BorderLayout;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;





public class Client extends Frame{
	private JFrame jf;
	
	private JPanel inputJPanel;
	private JButton inputButton;
	private JTextField inpuTextField;
	
	private JPanel leftJPanel;
	private JTextArea lefTextArea;
	
	private JPanel centenJPanel;
	private JTextArea jTextArea;
	private JScrollPane scorolle;
	
	private JPanel topJPanel;
	private JPanel topJPanel_1;
	private JPanel topJPanel_2;
	private JLabel topJLabel_1;
	private JLabel topJLabel_2;
	private JLabel topJLabel_3;
	private JTextField topJTextField_1;
	private JTextField topJTextField_2;
	private JTextField topJTextField_3;
	private JButton topButton_1;
	private JButton topButton_2;
	
	private Socket s;
	private static final String connentAdd="127.0.0.1";
	private static final int connentPort=666;
	
	private DataOutputStream dos;
	private DataInputStream dis;
	
	public Client() {
		init();
	}
	
	public Client(String title) {
		super(title);
		init();
	}
	
	//连接
	public void Connect() {
		try {
			//String Inf;
			String name = topJTextField_1.getText().trim();
			s=new Socket(connentAdd,connentPort);
			dos = new DataOutputStream(s.getOutputStream());
			jTextArea.append("与服务端连接成功\r\n");
			// 发送客户端用户基本信息(用户名和ip地址)
			sendMessage(name + "@" + s.getLocalAddress().toString());
			
			//dis = new DataInputStream(s.getInputStream());
			//Inf = dis.readUTF();
			//lefTextArea.append(Inf+"\r\n");
			//为接收消息开启线程
			Runnable seRunnable = new reserveClient(s,name);
			Thread t1 = new Thread(seRunnable);
			t1.start();
			
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void sendMessage(String str) {
		// TODO 自动生成的方法存根
		try {
			dos.writeUTF(str);
			dos.flush();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}
// 接收消息
	class reserveClient implements Runnable{
		private Socket s;
		private String name;
		public reserveClient (Socket s,String name) {
			this.s = s;
			this.name = name;
		}

		@Override
		public void run() {
			
			while (true) {
				//String Inf;
				String content = inpuTextField.getText();
				//接收用户
				
				try {
					
					dis = new DataInputStream(s.getInputStream());
					//Inf = dis.readUTF();
					//lefTextArea.append(Inf);
					while (true) {
						dis = new DataInputStream(s.getInputStream());
						String info = dis.readUTF();
						jTextArea.append(info +"\r\n");
					}
				} catch (IOException e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}
				 catch (Exception e) {	
					e.printStackTrace();
				}
			}
		}
		
	}
	
	//发送信息
	public void send() {
		String content = inpuTextField.getText();// 获取输入的文本信息
		// 判断输入的信息是否为空
		if(content!=null && !content.trim().equals("")){
			// 如果不为空，将输入的文本追加到到聊天窗口
			//jTextArea.append(content+"\r\n");
			
			try {
				dos = new DataOutputStream(s.getOutputStream());
				dos.writeUTF(inpuTextField.getText());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				inpuTextField.setText("");
			}
			}
		else{// 如果为空，提示聊天信息不能为空
			jTextArea.append("聊天信息不能为空" + "\n");
		}
	}
	
	
	//断开
	public void stop() {
		
			if (s!=null) {
				jTextArea.append("断开服务器\r\n");
				try {
					dos.close();
					dis.close();
					s.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				
			}
	}
	public void init() {
		jf = new JFrame();
		jf.setTitle("客户端");
		//设置关闭窗口时，同时结束程序
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//设置大小
		jf.setSize(800,400);
		//设置布局
		jf.setLayout(new BorderLayout());
		
		//创建组件
		inputJPanel = new JPanel();
		inpuTextField = new JTextField(60);
		
		
		inputButton = new JButton("发送");
		inputButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});
		
		leftJPanel = new JPanel();
		lefTextArea = new JTextArea(15,6);
		
		centenJPanel = new JPanel();
		centenJPanel.setLayout(new BorderLayout());
		jTextArea = new JTextArea(14,60);
		scorolle = new JScrollPane(jTextArea);
		jTextArea.setEditable(false);
		
		topJPanel = new JPanel();
		topJPanel.setLayout(new BorderLayout());
		topJPanel_1 = new JPanel();
		topJPanel_2 = new JPanel();
		topJLabel_1 = new JLabel("姓名:");
		topJTextField_1 = new JTextField(10);
		topJLabel_2 = new JLabel("服务器ip:");
		topJTextField_2 = new JTextField(10);
		topJTextField_2.setText("127.0.0.1");
		topJLabel_3 = new JLabel("端口:");
		topJTextField_3 = new JTextField(10);
		topJTextField_3.setText("666");
		
		
		topButton_1 = new JButton("连接");
		topButton_1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				Connect();
			}
		});
		
		
		topButton_2 = new JButton("断开");
		topButton_2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				stop();
			}
		});
		//2-添加组件到相应的子面板中，并布局
		inputJPanel.add(inpuTextField);
		inputJPanel.add(inputButton);
		inputJPanel.setBorder(new TitledBorder("写消息"));
		
		leftJPanel.add(lefTextArea);
		leftJPanel.setBorder(new TitledBorder("在线用户"));
		
		
		centenJPanel.add(scorolle,BorderLayout.CENTER);
		centenJPanel.setBorder(new TitledBorder("消息显示区"));
		
		topJPanel_1.add(topJLabel_1);
		topJPanel_1.add(topJTextField_1);
		topJPanel_2.add(topJLabel_2);
		topJPanel_2.add(topJTextField_2);
		topJPanel_2.add(topJLabel_3);
		topJPanel_2.add(topJTextField_3);
		topJPanel_2.add(topButton_1);
		topJPanel_2.add(topButton_2);
		topJPanel.add(topJPanel_1,BorderLayout.WEST);
		topJPanel.add(topJPanel_2,BorderLayout.EAST);
		topJPanel.setBorder(new TitledBorder("连接信息"));
		
		//3-把大面板放窗口上
		jf.add(inputJPanel,BorderLayout.SOUTH);
		jf.add(leftJPanel,BorderLayout.WEST);
		jf.add(centenJPanel,BorderLayout.CENTER);
		jf.add(topJPanel,BorderLayout.NORTH);
		
		
		jf.setVisible(true);
	}
	public static void main(String[] args) {
		Client sev = new Client(); 
	
	}	
}
