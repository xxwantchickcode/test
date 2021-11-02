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
	
	//����
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
	
	
	
	
	//����������
	public void serverOpean() throws IOException {
		if (!staServer) {
			jTextArea.append("�����������ɹ�\r\n");
			serverSocket = new ServerSocket(port);
			topBut_1.setBorder(BorderFactory.createRaisedBevelBorder()); 
			JOptionPane.showMessageDialog(null, "�����ɹ�");
			staServer = true;
			
			//��������˵��߳�
			Runnable seRunnable = new ThreadServer(s);
			Thread t1 = new Thread(seRunnable);
			t1.start();
		}
	}
	//������߳�
	class ThreadServer implements Runnable{
		private int count=1;
		private Socket socket;

		
		public ThreadServer(Socket s) {
			// TODO �Զ����ɵĹ��캯�����
			this.socket=s;
		}
		

		@Override
		public void run() {
			// TODO �Զ����ɵķ������
			while (true) {
				//���ӿͻ���
				try {
					//������������
					if (count<=6) {
						socket = serverSocket.accept();
						s=socket;
						dis = new DataInputStream(socket.getInputStream());
						jTextArea.append("��"+count+"���û����ӿͻ���\r\n");
						
						
						// ���տͻ��˵Ļ����û���Ϣ  
	                    String inf = dis.readUTF();  
	                    //��StringTokenizer���������д���
	                    StringTokenizer st = new StringTokenizer(inf, "@");  
	                    //��User��newһ�����󣬴��ݲ���
	                    User user = new User(st.nextToken(), st.nextToken());
	                    jTextArea.append(inf+"���ӳɹ�\r\n");
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
						jTextArea.append("���������޷�����");
					}

				} catch (IOException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}finally {
					count++;
				}
			}
		
		}
		
	}
	
	//ʵ�ֽ�����Ϣ
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
					//������Ϣ
						String info = dis.readUTF();
			 			jTextArea.append(name+"��"+info +"\r\n");
			 			
			 			//����arraylist
			 			Iterator<Mulit> it = ts.iterator();
			 			while (it.hasNext()) {
							Mulit ts = it.next();
							ts.sendAll(name+"��"+info +"\r\n");
						}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//ת���û���Ϣ
		public void sendUser(String name) {
			try {
				dos = new DataOutputStream(socket.getOutputStream());
				dos.writeUTF(name);
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}
		//ת����Ϣ
		public void sendAll(String message) {
			try {
				dos = new DataOutputStream(socket.getOutputStream());
				dos.writeUTF(message);
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}
		
	}
	
	//������Ϣ
	public void send(String content) {
		
		// �ж��������Ϣ�Ƿ�Ϊ��
		if(content!=null && !content.trim().equals("")){
			Iterator<Mulit> it = ts.iterator();
 			while (it.hasNext()) {
				Mulit ts = it.next();
//				ts.sendAll("Server��"+content+"\r\n");
			try {
//				dos = new DataOutputStream(s.getOutputStream());	
//				dos.writeUTF(textField.getText());
//			 	writer.close(); 
				ts.sendAll("Server��"+content+"\r\n");
			} finally {
				textField.setText("");
			}
 		}
	}
		else{
			// ���Ϊ�գ���ʾ������Ϣ����Ϊ��
			jTextArea.append("������Ϣ����Ϊ��" + "\n");
		}
	}
	
	//�Ͽ�������
	public void StopServer() {
		try {
			if (s!=null) {
				jTextArea.append("�Ͽ�������\r\n");
				dis.close();
				dos.close();
				s.close();
			}
			if (serverSocket!=null) {
				serverSocket.close();
			}
			jTextArea.append("�������ر�");
			} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	

	
	/**
	 * ��ʼ������
	 */
	public void init() {
		jf = new JFrame();
		jf.setTitle("�����");
		//���ùرմ���ʱ��ͬʱ��������
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//���ô�С
		jf.setSize(800,400);
		//���ò���
		jf.setLayout(new BorderLayout());
		//���ð��
		leftJf = new JPanel();
		leftJf.setLayout(new BorderLayout());
		rightJf = new JPanel();
		rightJf.setLayout(new BorderLayout());
		
		//1-�����齨
		btn = new JButton("����");
		//����ť����������
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO �Զ����ɵķ������
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
		topJLabel_1 = new JLabel("��������:");
		topText_1 = new JTextField(10);
		topText_1.setText("6");
		topText_1.setEditable(false);
		topJLabel_2 = new JLabel("�˿ں�:");
		topText_2 = new JTextField(10);
		topText_2.setText("666");
		topText_2.setEditable(false);
		
		topBut_1 = new JButton("����");
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
		
		topBut_2 = new JButton("��ͣ");
		topBut_2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO �Զ����ɵķ������
				StopServer();
			}
		});
		
		
		
		rightToP = new JPanel();
		rightJLabel = new JLabel("�����û�");
		rightText = new JTextField(8);
		rightBut = new JButton("��");
		
		scorolle = new JScrollPane(jTextArea);
		//2-����������Ӧ��������У�������

		jPanel.add(textField);
		jPanel.add(btn);
		jPanel.setBorder(new TitledBorder("д��Ϣ"));
		
		jpanelLeft.add(jLeft);
		jpanelLeft.setBorder(new TitledBorder("�����û�"));
		
		
		centenJPanel.add(scorolle,BorderLayout.CENTER);
		centenJPanel.setBorder(new TitledBorder("��Ϣ��ʾ��"));
		
		topLeft.add(topJLabel_1);
		topLeft.add(topText_1);
		topRight.add(topJLabel_2);
		topRight.add(topText_2);
		topRight.add(topBut_1);
		topRight.add(topBut_2);
		topJPanel.add(topLeft,BorderLayout.WEST);
		topJPanel.add(topRight,BorderLayout.EAST);
		topJPanel.setBorder(new TitledBorder("������Ϣ"));
		
		rightToP.add(rightJLabel);
		rightToP.add(rightText);
		rightToP.add(rightBut);
		rightJf.add(rightToP,BorderLayout.NORTH);
		rightJf.setBorder(new TitledBorder("���������ܲ���"));
		
		leftJf.add(topJPanel,BorderLayout.NORTH);
		leftJf.add(centenJPanel,BorderLayout.CENTER);
		leftJf.add(jPanel,BorderLayout.SOUTH);
		leftJf.add(jpanelLeft,BorderLayout.WEST);
		//3-�Ѵ����Ŵ�����
		jf.add(leftJf,BorderLayout.CENTER);
		jf.add(rightJf,BorderLayout.EAST);
		
		
		jf.setVisible(true);

	}
	 
	public static void main(String[] args) {
		Server sev = new Server(); 
	}		
}
