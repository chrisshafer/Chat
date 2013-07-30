import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


class ClientSender extends Thread
{
	private PrintWriter out;

	public ClientSender(PrintWriter out)
	{
		this.out = out;
	}

	//Reads messages from the console and sends them to the server
	public void run()
	{
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			while (!isInterrupted()) {
				String message = in.readLine();
				out.println(message);
				out.flush();
			}
		} catch (IOException ioe) {
			// Communication is broken
		}
	}
}
