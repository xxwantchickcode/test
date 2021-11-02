package chatFirst;
import javax.swing.border.TitledBorder;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class Server extends Frame{
	
	//构造
	private JFrame jf;
	private JButton btn;
	private JTextField textField;
	private JPanel leftJf;
	private JPanel rightJf;
	private JPanel jPanel;
	private JPanel jpanelLeft;
	private JTextArea jLeft;
	private JTextArea jTextArea;
	private JPanel topJPanel;
	private JPanel topLeft;
	private JPanel topRight;
	private JLabel topJLabel_1;
	private JTextField topText_1;
	private JLabel topJLabel_2;
	private JTextField topText_2;
	private JButton topBut_1;
	private JButton topBut_2;
	private JPanel rightToP;
	private JLabel rightJLabel;
	private JTextField rightText;
	private JButton rightBut;
	private JPanel centenJPanel;
	
	private static final int port=666;
	private ServerSocket serverSocket=null;
	private Socket s=null;

	private boolean staServer = false;
	
	private ArrayList<Mulit> ts =  new ArrayList<Mulit>();
	//private HashMap<String, Socket> hM = new HashMap<String, Socket>();
	private DataInputStream dis;
	private DataOutputStream dos;
	
	public Server() {
		init();
	}
	
	public Server(String title) {
		super(title);
		init();
	}
	
	
	
	
	//启动服务器
	public void serverOpean() throws IOException {
		if (!staServer) {
			jTextArea.append("服务器启动成功\r\n");
			serverSocket = new ServerSocket(port);
			topBut_1.setBorder(BorderFactory.createRaisedBevelBorder()); 
			JOptionPane.showMessageDialog(null, "启动成功");
			staServer = true;
			
			//开启服务端的线程
			Runnable seRunnable = new ThreadServer(s);
			Thread t1 = new Thread(seRunnable);
			t1.start();
		}
	}
	//服务端线程
	class ThreadServer implements Runnable{
		private int count=1;
		private Socket socket;

		
		public ThreadServer(Socket s) {
			// TODO 自动生成的构造函数存根
			this.socket=s;
		}
		

		@Override
		public void run() {
			// TODO 自动生成的方法存根
			while (true) {
				//连接客户端
				try {
					//设置上限人数
					if (count<=6) {
						socket = serverSocket.accept();
						s=socket;
						dis = new DataInputStream(socket.getInputStream());
						jTextArea.append("有"+count+"个用户连接客户端\r\n");
						
						
						// 接收客户端的基本用户信息  
	                    String inf = dis.readUTF();  
	                    //用StringTokenizer对姓名进行处理
	                    StringTokenizer st = new StringTokenizer(inf, "@");  
	                    //给User类new一个对象，传递参数
	                    User user = new User(st.nextToken(), st.nextToken());
	                    jTextArea.append(inf+"连接成功\r\n");
	                    jLeft.append(inf+"\r\n");
	                    
	                    ts.add(new Mulit(s,inf));
	                   
	                    
						Runnable seRunnable = new Mulit(socket,inf);
						Thread muliThread = new Thread(seRunnable);
						muliThread.start();
						
						Iterator<Mulit> it = ts.iterator();
			 			while (it.hasNext()) {
							Mulit ts = it.next();
							//ts.sendUser(inf);
						}
					}else {
						jTextArea.append("人数上限无法连接");
					}

				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}finally {
					count++;
				}
			}
		
		}
		
	}
	
	//实现接收消息
	class Mulit implements Runnable{
		private Socket socket;
		private String name;
		public Mulit(Socket socket,String inf) {
			this.socket=socket;
			name = inf;
		}
		@Override
		public void run() {
			while (true) {
				
				try {
					dis = new DataInputStream(socket.getInputStream());
					//接收消息
						String info = dis.readUTF();
			 			jTextArea.append(name+"："+info +"\r\n");
			 			
			 			//遍历arraylist
			 			Iterator<Mulit> it = ts.iterator();
			 			while (it.hasNext()) {
							Mulit ts = it.next();
							ts.sendAll(name+"："+info +"\r\n");
						}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//转发用户信息
		public void sendUser(String name) {
			try {
				dos = new DataOutputStream(socket.getOutputStream());
				dos.writeUTF(name);
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		//转发消息
		public void sendAll(String message) {
			try {
				dos = new DataOutputStream(socket.getOutputStream());
				dos.writeUTF(message);
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		
	}
	
	//发送消息
	public void send(String content) {
		
		// 判断输入的信息是否为空
		if(content!=null && !content.trim().equals("")){
			Iterator<Mulit> it = ts.iterator();
 			while (it.hasNext()) {
				Mulit ts = it.next();
//				ts.sendAll("Server："+content+"\r\n");
			try {
//				dos = new DataOutputStream(s.getOutputStream());	
//				dos.writeUTF(textField.getText());
//			 	writer.close(); 
				ts.sendAll("Server："+content+"\r\n");
			} finally {
				textField.setText("");
			}
 		}
	}
		else{
			// 如果为空，提示聊天信息不能为空
			jTextArea.append("聊天信息不能为空" + "\n");
		}
	}
	
	//断开服务器
	public void StopServer() {
		try {
			if (s!=null) {
				jTextArea.append("断开服务器\r\n");
				dis.close();
				dos.close();
				s.close();
			}
			if (serverSocket!=null) {
				serverSocket.close();
			}
			jTextArea.append("服务器关闭");
			} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	

	
	/**
	 * 初始化界面
	 */
	public void init() {
		jf = new JFrame();
		jf.setTitle("服务端");
		//设置关闭窗口时，同时结束程序
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//设置大小
		jf.setSize(800,400);
		//设置布局
		jf.setLayout(new BorderLayout());
		//设置板块
		leftJf = new JPanel();
		leftJf.setLayout(new BorderLayout());
		rightJf = new JPanel();
		rightJf.setLayout(new BorderLayout());
		
		//1-生成组建
		btn = new JButton("发送");
		//给按钮创建监听器
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				send(textField.getText());
			}
		});
		
		textField = new JTextField(40);
		
		jPanel = new JPanel();
		jpanelLeft = new JPanel();
		
		jLeft = new JTextArea(11,5);
		jLeft.setEditable(false);
		
		centenJPanel = new JPanel();
		centenJPanel.setLayout(new BorderLayout());
		jTextArea = new JTextArea(12,34);
		JScrollPane scorolle = new JScrollPane();
		scorolle = new JScrollPane(jTextArea);
		jTextArea.setEditable(false);
		
		topJPanel = new JPanel();
		topJPanel.setLayout(new BorderLayout());
		topLeft = new JPanel();
		topRight = new JPanel();
		topJLabel_1 = new JLabel("人数上限:");
		topText_1 = new JTextField(10);
		topText_1.setText("6");
		topText_1.setEditable(false);
		topJLabel_2 = new JLabel("端口号:");
		topText_2 = new JTextField(10);
		topText_2.setText("666");
		topText_2.setEditable(false);
		
		topBut_1 = new JButton("启动");
		topBut_1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					serverOpean();
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
			}
		});
		
		topBut_2 = new JButton("暂停");
		topBut_2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				StopServer();
			}
		});
		
		
		
		rightToP = new JPanel();
		rightJLabel = new JLabel("被踢用户");
		rightText = new JTextField(8);
		rightBut = new JButton("踢");
		
		scorolle = new JScrollPane(jTextArea);
		//2-添加组件到相应的子面板中，并布局

		jPanel.add(textField);
		jPanel.add(btn);
		jPanel.setBorder(new TitledBorder("写消息"));
		
		jpanelLeft.add(jLeft);
		jpanelLeft.setBorder(new TitledBorder("在线用户"));
		
		
		centenJPanel.add(scorolle,BorderLayout.CENTER);
		centenJPanel.setBorder(new TitledBorder("消息显示区"));
		
		topLeft.add(topJLabel_1);
		topLeft.add(topText_1);
		topRight.add(topJLabel_2);
		topRight.add(topText_2);
		topRight.add(topBut_1);
		topRight.add(topBut_2);
		topJPanel.add(topLeft,BorderLayout.WEST);
		topJPanel.add(topRight,BorderLayout.EAST);
		topJPanel.setBorder(new TitledBorder("连接信息"));
		
		rightToP.add(rightJLabel);
		rightToP.add(rightText);
		rightToP.add(rightBut);
		rightJf.add(rightToP,BorderLayout.NORTH);
		rightJf.setBorder(new TitledBorder("服务器功能操作"));
		
		leftJf.add(topJPanel,BorderLayout.NORTH);
		leftJf.add(centenJPanel,BorderLayout.CENTER);
		leftJf.add(jPanel,BorderLayout.SOUTH);
		leftJf.add(jpanelLeft,BorderLayout.WEST);
		//3-把大面板放窗口上
		jf.add(leftJf,BorderLayout.CENTER);
		jf.add(rightJf,BorderLayout.EAST);
		
		
		jf.setVisible(true);

	}
	 
	public static void main(String[] args) {
		Server sev = new Server(); 
	}		
}
