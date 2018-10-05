/*
The code is modified from
https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
*/
import java.net.*;
import java.io.*;
import java.util.*;

/*
 * The Client that can be run both as a console or a GUI
 */
public class Client  {

	// for I/O
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;
    public String friendRawStr="";
	public String friendLoginStr="";
	public HashMap<String,String> FriendMessages;
	// if I use a GUI or not
	private ClientGUI cg;

	// the server, the port and the username
	private String server, username;
	private int port;
	private static boolean YESNOFLAG = false;
	private static String YESNOsender;
	private String friend_status;
	/*
	 *  Constructor called by console mode
	 *  server: the server address
	 *  port: the port number
	 *  username: the username
	 */
	Client(String server, int port, String username) {
		// which calls the common constructor with the GUI set to null
		this(server, port, username, null);
	}

	/*
	 * Constructor call when used from a GUI
	 * in console mode the ClienGUI parameter is null
	 */
	Client(String server, int port, String username, ClientGUI cg) {
		this.server = server;
		this.port = port;
		this.username = username;
		// save if we are in GUI mode or not
		this.cg = cg;
	}
	
	/*
	 * To start the dialog
	 */
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		} 
		// if it failed not much I can so
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		/* Creating both Data Stream */
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}
        try
        {
            sOutput.writeObject(username);

            friendRawStr = (String)sInput.readObject();
            System.out.println(friendRawStr);
            display(friendRawStr);
            cg.friendlist = friendRawStr;

			friendLoginStr = (String)sInput.readObject();
			System.out.println(friendLoginStr);
			display(friendLoginStr);
			cg.friendLoginStr = friendLoginStr;
		}
        catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // creates the Thread to listen from the server
		new ListenFromServer(this).start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects

        // success we inform the caller that it worked
		return true;
	}

	/*
	 * To send a message to the console or the GUI
	 */
	private void display(String msg) {
		if(cg == null)
			System.out.println(msg);      // println in console mode
		else
			cg.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	}
	
	/*
	 * To send a message to the server
	 */
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do
		
		// inform the GUI
		if(cg != null)
			cg.connectionFailed();
			
	}
	/*
	 * To start the Client in console mode use one of the following command
	 * > java Client
	 * > java Client username
	 * > java Client username portNumber
	 * > java Client username portNumber serverAddress
	 * at the console prompt
	 * If the portNumber is not specified 1500 is used
	 * If the serverAddress is not specified "localHost" is used
	 * If the username is not specified "Anonymous" is used
	 * > java Client 
	 * is equivalent to
	 * > java Client Anonymous 1500 localhost 
	 * are eqquivalent
	 * 
	 * In console mode, if an error occurs the program simply stops
	 * when a GUI id used, the GUI is informed of the disconnection
	 */
	public static void main(String[] args) {
		// default values
		int portNumber = 1500;
		String serverAddress = "localhost";//"147.46.241.102";
		String userName = "Client1";

		// depending of the number of arguments provided we fall through
		switch(args.length) {
			// > javac Client username portNumber serverAddr
			case 3:
				serverAddress = args[2];
			// > javac Client username portNumber
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
					return;
				}
			// > javac Client username
			case 1: 
				userName = args[0];
			// > java Client
			case 0:
				break;
			// invalid number of arguments
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		// create the Client object
		Client client = new Client(serverAddress, portNumber, userName);
		// test if we can start the connection to the Server
		// if it failed nothing we can do
		if(!client.start())
			return;
		
		// wait for messages from user
		Scanner scan = new Scanner(System.in);
		// loop forever for message from the user
		while(true) {
			System.out.print("> ");
			// read message from user
			String msg = scan.nextLine();
			// logout if message is LOGOUT
			if(YESNOFLAG && client.cg==null)	{
				YESNOFLAG = false;
				client.sendMessage(new ChatMessage(ChatMessage.YESNO, msg+"\""+YESNOsender+"\""));
			}
			else if(YESNOFLAG)	{
				YESNOFLAG = false;
			}
			else if(msg.equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				// break to do the disconnect
				break;
			}
			// message WhoIsIn
			else if(msg.equalsIgnoreCase("WHOISIN")) {
				client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			}
            else if(msg.equalsIgnoreCase("FRIEND")) {
            	System.out.print("MANAGER: Who do you want to be a friend? > ");
				//display("MANAGER: Who do you want to be a friend? > ");
                client.sendMessage(new ChatMessage(ChatMessage.FRIEND, scan.nextLine()));
				System.out.println("MANAGER: The answer will be soon..");
			}
			else if(msg.contains("<")){
				client.sendMessage(new ChatMessage(ChatMessage.pMESSAGE, msg+"<"+userName));
			}
			else {				// default to ordinary message
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
			}
		}
		// done disconnect
		client.disconnect();	
	}

	/*
	 * a class that waits for the message from the server and append them to the JTextArea
	 * if we have a GUI or simply System.out.println() it in console mode
	 */
	class ListenFromServer extends Thread {
		Client client;
		ListenFromServer(Client client){
			this.client = client;
		}
		public void run() {
			while(true) {
				try {
					String msg = (String) sInput.readObject();
					//if(msg.contains("MANAGER"))
					{
						if (msg.contains("MANAGER: Do you agree to be a friend with \"")) {
							YESNOFLAG = true;
							YESNOsender = msg.substring(msg.indexOf('\"') + 1, msg.lastIndexOf('\"'));
							cg.YESNOFLAG = true;
							cg.YESNOtarget = YESNOsender;
							//cg.opup_frequ.showMessageDialog(cg,cg.opup_frequ.getMessage()+" \""+YESNOsender+"\" ?");
						} else if (msg.contains("reject your request..")) {
							//cg.pup = new Popup(cg,"");
							String rejector = msg.substring(msg.indexOf("\"") + 1, msg.lastIndexOf("\""));
							cg.opup.showMessageDialog(cg, "\"" + rejector + "\" " + cg.opup.getMessage());
						}
						else if(msg.contains("isfriend?no")){
							cg.opup.showMessageDialog(cg, "\"" + cg.currentChatName + "\" " +"is not your friend.");
							continue;
						}
						else if(msg.contains("isfriend?yes")){
							cg.ta_private.setText("");
							cg.append_p("\nStart chatting with "+cg.currentChatName+'\n');
							continue;
						}
					}
					// if console mode print the message and add back the prompt
					if(cg == null) {
						System.out.println(msg);
						System.out.print("> ");
					}
					else if(msg.contains("private|||")){
						msg = msg.substring(10);
						String me = username;
						String counter;
						String receiver = msg.substring(msg.lastIndexOf(">")+1);
						String sender = msg.substring(msg.indexOf(" ")+1,msg.indexOf(">"));
						counter = me.equals(receiver)? sender : receiver;
						cg.currentChatName=counter;
						if(!cg.startFLAG)
						{
							cg.ta_private.setText("Chatting with \""+counter+"\" is just started...\n");
							cg.startFLAG=true;
						}

						cg.tf_private.setText("");
						cg.append_p(msg.substring(0,msg.indexOf('\n'))+"\n");
					}
					else
						cg.append(msg+"\n");

				}
				catch(IOException e) {
					display("Server has close the connection: " + e);
					if(cg != null) 
						cg.connectionFailed();
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}

}
