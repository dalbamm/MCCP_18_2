/*
The code is modified from
https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;


/*
 * The Client with its GUI
 */
public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	// will first hold "Username:", later on "Enter message"
	private JLabel label;
	// to hold the Username and later on the messages
	private JTextField tf;
	public JTextField tf_private;
	// to hold the server address an the port number
	private JTextField tfServer, tfPort, tfName;
	// to Logout and get the list of the users
	private JButton login, logout, whoIsIn, addFriend,accept,reject,startchat;
	// for the chat room
	private JTextArea ta;
	public JTextArea ta_private;
	public JOptionPane opup=new JOptionPane("reject your request..");
	public JOptionPane opup_frequ=new JOptionPane("Do you agree to be friend with ");
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	// the default port number
	private int defaultPort;
	private String defaultHost;

	public String friendlist;
	public String friendLoginStr="";
	public HashMap<String,String> FriendMessages;
	public boolean YESNOFLAG;
	public boolean startFLAG;
	public boolean friendFLAG;
	public String YESNOtarget="";
	public String currentChatName="";
	// Constructor connection receiving a socket number
	ClientGUI(String host, int port) {

		super("Chat Client");
		defaultPort = port;
		defaultHost = host;
		
		// The NorthPanel with:
		JPanel northPanel = new JPanel(new GridLayout(3,1));
		// the server name anmd the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		// the two JTextField with default value for server address and port number
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfName = new JTextField("john");
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel("Name:  "));
		serverAndPort.add(tfName);
		// adds the Server an port field to the GUI
		northPanel.add(serverAndPort);

		// the Label and the TextField
		label = new JLabel("<<Enter your Login information", SwingConstants.CENTER);
		northPanel.add(label);
		tf = new JTextField("");
		tf.setEditable(false);
		tf.setBackground(Color.WHITE);
		northPanel.add(tf);

		tf_private = new JTextField("");
		tf_private.setEditable(false);
		tf_private.setBackground(Color.WHITE);
		northPanel.add(tf_private);

		add(northPanel, BorderLayout.NORTH);

		// The CenterPanel which is the chat room
		ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
		ta_private = new JTextArea("Start chatting!\nWrite the name of your friend and Click the \'start chat\' button", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,2));
		centerPanel.add(new JScrollPane(ta));
		centerPanel.add(new JScrollPane(ta_private));
		ta.setEditable(false);
		ta_private.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		// the 3 buttons
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);		// you have to login before being able to logout
		whoIsIn = new JButton("Who is in");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);		// you have to login before being able to Who is in

		addFriend = new JButton("Add Friend");
		addFriend.addActionListener(this);
		addFriend.setEnabled(false);		// you have to login before being able to Who is in

		accept= new JButton("Accept");
		accept.addActionListener(this);
		accept.setEnabled(false);		// you have to login before being able to Who is in

		reject	= new JButton("Reject");
		reject.addActionListener(this);
		reject.setEnabled(false);		// you have to login before being able to Who is in

		startchat	= new JButton("Start chat");
		startchat.addActionListener(this);
		startchat.setEnabled(false);		// you have to login before being able to Who is in

		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(whoIsIn);
		southPanel.add(addFriend);
		southPanel.add(accept);
		southPanel.add(reject);
		southPanel.add(startchat);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(700, 700);
		setVisible(true);
		tf.requestFocus();
		tf_private.requestFocus();

	}

	// called by the Client to append text in the TextArea 
	void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}
	void append_p(String str) {
		ta_private.append(str);
		ta_private.setCaretPosition(ta_private.getText().length() - 1);
	}
	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);addFriend.setEnabled(false);accept.setEnabled(false);
		reject.setEnabled(false);
		startchat.setEnabled(false);

		label.setText("<<Enter your Login information");
		tf.setText("");
		tf_private.setText("");
		// reset port number and host name as a construction time
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		// let the user change them
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		tfName.setEditable(false);
		// don't react to a <CR> after the username
		tf.removeActionListener(this);
		tf_private.removeActionListener(this);
		connected = false;
	}
		
	/*
	* Button or JTextField clicked
	*/
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if(o == logout) {
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			tfPort.setEditable(true);
			tfServer.setEditable(true);
			tfName.setEditable(true);
			return;
		}
		// if it the who is in button
		if(o == whoIsIn) {
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			return;
		}
		if(o == addFriend) {
			String name;
			client.sendMessage(new ChatMessage(ChatMessage.FRIEND, name=tf.getText()));
			System.out.println("MANAGER: The answer will be soon..");
			append("MANAGER: The request is delivered to "+"\""+name+"\".\n");
			tf.setText("");
			return;
		}
		if(o == accept) {
			if(YESNOFLAG){
				String respond = "y";
				client.sendMessage(new ChatMessage(ChatMessage.YESNO,"y\""+YESNOtarget+"\""));
				tf.setText("");
				YESNOtarget="";
				YESNOFLAG=false;
			}
			return;
		}

		if(o == reject) {
			if (YESNOFLAG) {
				String respond = "n";
				client.sendMessage(new ChatMessage(ChatMessage.YESNO, "n\"" + YESNOtarget + "\""));
				tf.setText("");
				YESNOtarget = "";
				YESNOFLAG = false;
			}
			return;
		}
		if(o == startchat) {
			startFLAG=true;
			currentChatName= tf.getText();
/*			if(!friendlist.contains(currentChatName))	{
				ta_private.setText("\""+currentChatName+"\" is not your friend....\n");
				return;
			}*/
			client.sendMessage(new ChatMessage(ChatMessage.YESNO, "isfriend?"+currentChatName+"?"+tfName.getText()));
			friendFLAG=true;

			tf.setText("");
			tf_private.setText("");
			return;
		}

	// ok it is coming from the JTextField
		if(connected) {
			// just have to send the message
			/*client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tf.getText()));
			tf.setText("");*/
			client.sendMessage(new ChatMessage(ChatMessage.pMESSAGE, tf_private.getText()+"<"+currentChatName+"<"+tfName.getText()));
			tf_private.setText("");
			return;
		}
		

		if(o == login) {
			ta.setText("");
			ta_private.setText("");
			// ok it is a connection request
			tf.setEditable(true);
			tf_private.setEditable(true);
			String username = tfName.getText().trim();
			// empty username ignore it
			if(username.length() == 0)
				return;
			// empty serverAddress ignore it
			String server = tfServer.getText().trim();
			if(server.length() == 0)
				return;
			// empty or invalid port numer, ignore it
			String portNumber = tfPort.getText().trim();
			tfName.setText(username);
			if(portNumber.length() == 0)
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;   // nothing I can do if port number is not valid
			}

			// try creating a new Client with GUI
			client = new Client(server, port, username, this);
			// test if we can start the Client
			if(!client.start()) 
				return;
			//append(friendlist);
			//append(friendLoginStr);
			tf.setText("");
			tf_private.setText("");
			label.setText("left: command / right: message");
			connected = true;
			
			// disable login button
			login.setEnabled(false);
			// enable the 2 buttons
			logout.setEnabled(true);
			whoIsIn.setEnabled(true);
			addFriend.setEnabled(true);
			accept.setEnabled(true);
			reject.setEnabled(true);
			startchat.setEnabled(true);
			// disable the Server and Port JTextField
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			tfName.setEditable(false);
			// Action listener for when the user enter a message
			tf.addActionListener(this);
			tf_private.addActionListener(this);
		}

	}

	// to start the whole thing the server
	public static void main(String[] args) {
		new ClientGUI("localhost",1500);//"147.46.241.102", 20141);
	}

}
