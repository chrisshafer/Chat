import java.io.*;
import java.net.*;

public class Client
{
	public static final String SERVER_HOSTNAME = "localhost";
	public static final int SERVER_PORT = 11516;
	private static Socket socket;

	public static void main(String[] args) throws IOException
	{
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			// Connect to Server
			System.out.println("Attempting connection to server " + SERVER_HOSTNAME + ":" + SERVER_PORT);
		    socket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
			in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter( new OutputStreamWriter(socket.getOutputStream()));
			System.out.println("Connected to server " + SERVER_HOSTNAME + ":" + SERVER_PORT);
		} catch (IOException ioe) {
			System.err.println("Can not establish connection to " + SERVER_HOSTNAME + ":" + SERVER_PORT);
			ioe.printStackTrace();
			socket.close();
			System.exit(-1);
		}

		// Create and start Sender thread
		ClientSender clientSender = new ClientSender(out);
		clientSender.setDaemon(true);
		clientSender.start();

		try {
			// Read messages from the server and print them
			String message;
			while ((message=in.readLine()) != null) {
				System.out.println(message);
			}
		} catch (IOException ioe) {
			System.err.println("Connection to server broken.");
			socket.close();
			ioe.printStackTrace();
		}
		

	}
}

