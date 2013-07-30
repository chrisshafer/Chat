import java.net.*;
import java.io.*;

public class EchoServer
{
	public static final int LISTENING_PORT = 11516;
	public static final int MAX_CLIENTS = 4;
	ServerSocket serverSocket;
	@SuppressWarnings("null")
	public static void main(String[] args) throws NumberFormatException, IOException
	{
		// Open server socket for listening
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(LISTENING_PORT);
			System.out.println("Server started on port " + LISTENING_PORT);
		} catch (IOException se) {
			System.err.println("Can not start listening on port " + LISTENING_PORT);
			se.printStackTrace();
			serverSocket.close();
			System.exit(-1);
		} 

		// Start manager
		Manager manager = new Manager();
		
		// read in user accounts
			BufferedReader br = new BufferedReader(new FileReader("users.txt"));
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				String[] input = currentLine.split(";",5 );
				if(input.length<5)
				{
					System.out.print("Input file incorrectly formatted");
					break;
				}
				// creates user and sets values
				User user = new User();
				user.name = input[0];
				user.password = input[1];
				user.region = Integer.parseInt(input[3]);
				user.anycast = input[4].split(",");
				if(input[2].contains(","))
				{
					String[] groups = input[2].split(",");
					for(int i=0; i<groups.length; i++)
						user.group.add(Integer.parseInt(groups[i]));
				}
				else
					user.group.add(Integer.parseInt(input[2]));
				// adds user to the list of possible users
				manager.userDatabase.add(user);
			}
			br.close();
 


		// Accept and handle client connections
		while (true) {
			try {
				//accept connections
				Socket socket = serverSocket.accept();
				//set sender and listener for the client
				ClientInfo clientInfo = new ClientInfo();
				clientInfo.socket = socket;
				ServerListener serverListener = new ServerListener(clientInfo, manager);
				ServerSender serverSender = new ServerSender(clientInfo, manager);	
				clientInfo.listener = serverListener;
				clientInfo.sender = serverSender;
				serverListener.start();
				serverSender.start();
				System.out.println("client connected:"+socket.toString());
				serverSender.sendMessage("type login <username> <password> to login");
				// adds clients to the manager
				manager.addClient(clientInfo);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

}