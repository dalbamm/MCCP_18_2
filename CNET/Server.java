/* This file is modified from
	
https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/

*/

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
    // a Hashmap to keep the mapping between client and the string of Friends
    private HashMap<String,ArrayList<String>> name2friends = new HashMap<String,ArrayList<String>>();
    // a Hashmap to store <friend_name, messages>
	//todo: You should write the code to store message logs.
    private HashMap<String,String> nametuple2messages = new HashMap<String,String>();
    // to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;
	/*
	 *  server constructor that receive the port to listen to for connection as parameter
	 *  in console
	 */
	public Server(int port) {
        this.port = port;
        // to display hh:mm:ss
        sdf = new SimpleDateFormat("HH:mm:ss");
        // ArrayList for the Client list
        al = new ArrayList<ClientThread>();
		//this(port, null);
	}

	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  // make a thread of it
				al.add(t);// save it in the ArrayList
				updateLogonStatus(t.username);
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}

	}		
    /*
     * For the GUI to stop the server
     */
	protected void stop() {
		keepGoing = false;
		// connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
		}
	}
	/*
	 * Display an event (not a message) to the console or the GUI
	 */
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
			System.out.println(time);

	}
	/*
	 *  to broadcast a message to specific client
	 */
	private synchronized void pbroadcast(String message, String receiver, String sender) {
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
		String messageLf = message + "\n";
		// display message on console
		String messageLf2 = time + " " +sender+ "> "+ message + "\n";
		System.out.print(time+" " + sender+"->"+receiver+" :"+messageLf);
		ClientThread ct = findThread(receiver);
		// try to write to the Client if it fails remove it from the list
		if(ct==null) ;
		else if(!ct.writeMsg(messageLf2)) {
			al.remove(ct);
			updateLogonStatus(receiver);
			display("Disconnected Client " + ct.username + " removed from list.");
		}
		//todo: receiver logout status -> message log remaining impl. needed
		String logname = arrangeAlpha(receiver,sender);
		if(!nametuple2messages.containsKey(logname))
			nametuple2messages.put(logname,"");
		String log = nametuple2messages.get(logname);
		log += messageLf2;
		nametuple2messages.replace(logname,log);
		System.out.println("DBG:::"+nametuple2messages.get(logname));
	}

	private synchronized void broadcast(String message) {
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
		String messageLf = time + " " + message + "\n";

			System.out.print(messageLf);
		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			String changename = ct.username;
			// try to write to the Client if it fails remove it from the list
			if(!ct.writeMsg(messageLf)) {
				al.remove(i);
				updateLogonStatus(changename);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}
	// for a client who logoff using the LOGOUT message
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			String changename=ct.username;
			// found it
			if(ct.id == id) {
				al.remove(i);
				updateLogonStatus(changename);
				return;
			}
		}
	}
	public boolean isLogin(String username){
		return findThread(username)!=null;
	}

	public ClientThread findThread(String username){
		for(int i = 0 ; i < al.size() ; ++i){
			if(al.get(i).username.equals(username))
				return al.get(i);
		}
		return null;
	}
	public void AddFriend(String friend1, String friend2){
		name2friends.get(friend1).add(friend2);
		name2friends.get(friend2).add(friend1);
	}

	public String CheckLoginFriends(String username){
		int len = name2friends.get(username).size();
		String[] rawlist=new String[len];
		name2friends.get(username).toArray(rawlist);
		String rst="/";
		for(int i = 0 ; i < rawlist.length; ++i){
			String tmp = rawlist[i];
			if(isLogin(tmp))	rst+=tmp+": ON/";
			else rst+=tmp+": OFF/";
		}
		return rst;
	}
	public String ArraytoString(ArrayList<String> list){
		String rst="";
		for(int i = 0 ; i < list.size(); ++i){
			rst += " "+list.get(i);
		}
		return rst.equals("") ? rst : rst.substring(1);
	}

	public void updateLogonStatus(String changeuser){
		ArrayList<String> tmp = name2friends.get(changeuser);
		for(int i = 0 ; i < tmp.size() ; ++i){
			ClientThread ct;
			ct = findThread(tmp.get(i));
			if(ct!=null) {
				ct.friend_status = CheckLoginFriends(ct.username);
				ct.writeMsg("\""+changeuser+"\"\'s status is changed: " + ct.friend_status);
				updateDialog(changeuser,ct.username) ;
			}

		}
	}
	public void updateHashMap(String key, String added){
		if(!nametuple2messages.containsKey(key))	nametuple2messages.put(key,"");
		String tmp = nametuple2messages.get(key);
		tmp+=added;
		nametuple2messages.replace(key,tmp);
	}
	public void updateDialog(String offliner, String counter){
		if(isLogin(offliner))	return;
		String logfilename=arrangeAlpha(offliner,counter);
		String checkpoint = "==="+offliner+"===";
		String tmp =nametuple2messages.get(logfilename);
		tmp=tmp.replace(checkpoint,"");
		nametuple2messages.replace(logfilename,tmp+checkpoint);
			//for(int i = 0 ; i < name2friends.get(offliner).size();++i){
		//}
	}
	private String arrangeAlpha(String sub1, String sub2){
		return sub1.compareTo(sub2)>0? sub2+"_"+sub1 : sub1+"_"+sub2;
	}
	//fixme ^- add methods
	/*
	 *  To run as a console application just open a console window and: 
	 * > java Server
	 * > java Server portNumber
	 * If the port number is not specified 1500 is used
	 */ 
	public static void main(String[] args) {
		// start server on port 1500 unless a PortNumber is specified 
		int portNumber = 1500;
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		String username;
		// the only type of message a will receive
		ChatMessage cm;
		// the date I connect
		String date;
        ArrayList<String> friendlist;
		String friend_status="";
		String friendlistStr="";
		// Constructore
		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username
				username = (String) sInput.readObject();
				display(username + " just connected.");
				//sOutput.writeObject("hello");
				if(!name2friends.containsKey(username)){
                    name2friends.put(username,new ArrayList<String>());
                }
                //name2friends.replace(username,name2friends.get(username)+" blahblah~!~!~!");
				friendlistStr = ArraytoString(name2friends.get(username));
				sOutput.writeObject("MANAGER: Your Friends../"+friendlistStr);
				friend_status = CheckLoginFriends(username);
				sOutput.writeObject("MANAGER: Log on.."+friend_status);
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// have to catch ClassNotFoundException
			// but I read a String, I am sure it will work
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}

		// what will run forever
		public void run() {int cnt=0;
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				//System.out.println(cnt++);
				try {
					cm = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				// the messaage part of the ChatMessage
				String message = cm.getMessage();

				// Switch on the type of message receive
				switch(cm.getType()) {
				//todo: complete below pMessage(private) .
					// You may implement branch version of broadcast method.
					case ChatMessage.pMESSAGE:
						String [] tmp_list = message.split("<");
						String tmp_send = tmp_list[2];
						String tmp_receiv = tmp_list[1];
						String msg = tmp_list[0];
					pbroadcast(msg,tmp_receiv,tmp_send);
					break;

				case ChatMessage.MESSAGE:
					broadcast(username + ": " + message);
					break;
				case ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				case ChatMessage.FRIEND:
                    display("FRIEND:::"+username + " send the request to " + message);
                    String receiver = message;
                    ClientThread T_receiver = findThread(receiver);
                    	if(T_receiver != null){
                    		T_receiver.writeMsg("MANAGER: Do you agree to be friend with \"" + username +"\"?"+
									" If you agree, just type one letter \'y\', or considered as rejection.");
							break;
                    	}
                    	//todo: T_receiver==null when receiver is offline. -> memo it
					break;
				case ChatMessage.YESNO:
					String responder, requestor;
					responder = username;
					requestor = message.substring(message.indexOf("\"")+1,message.lastIndexOf("\""));


					ClientThread T_responder = findThread(responder);
					ClientThread T_requestor = findThread(requestor);

					if(message.charAt(0)=='y') {
						display("FRIEND:::"+responder + " accepts the request from " + requestor);
						AddFriend(responder, requestor);


						if (T_responder != null)
							T_responder.writeMsg("MANAGER: Congratulaions! You are now friend with \"" + requestor + "\"!");
						if (T_requestor != null)
							T_requestor.writeMsg("MANAGER: Congratulaions! You are now friend with \"" + responder + "\"!");

					}
					else{
						display("FRIEND:::"+responder + " rejects the request from " + requestor);
						T_responder.writeMsg("MANAGER: OK, I will send rejection message to \"" + requestor + "\"!");
						T_requestor.writeMsg("MANAGER: Sorry, \"" + responder + "\" reject your request..");
					}
					break;
					//todo: You should configure the code to show status of only friends, not of all users.
					case ChatMessage.WHOISIN:
					writeMsg("List of the friends connected at " + sdf.format(new Date()) + "\n");
					// scan al the users connected
						cnt=0;
						friendlist = name2friends.get(username);
						for(int i = 0; i < friendlist.size(); ++i) {
							String friendcheck=friendlist.get(i);
							ClientThread ct = findThread(friendcheck);
							if(ct==null)	continue;
							writeMsg((++cnt) + ") " + ct.username + " since " + ct.date);
						}
					break;
				}
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		}
		
		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		/*
		 * Write a String to the Client output stream
		 */
		private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}

