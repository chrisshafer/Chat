 import java.net.Socket;
public class ClientInfo
{
	public Socket socket = null;
    public ServerListener listener = null;
    public ServerSender sender = null;
    public User user = null;
    public boolean isLoggedIn;
}