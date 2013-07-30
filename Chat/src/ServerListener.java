import java.io.*;
import java.net.*;
 
public class ServerListener extends Thread
{
    private Manager manager;
    private ClientInfo info;
    private BufferedReader in;
 
    public ServerListener(ClientInfo info, Manager manager)
    throws IOException
    {
        this.info = info;
        this.manager = manager;
        Socket socket = info.socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    public void run()
    {
        try {
           while (!isInterrupted()) {
               String message = in.readLine();
               if (message == null)
                   break;
               processCommand(info,message);
           }
        } catch (IOException ioex) {
           // Problem reading from socket (communication is broken)
        }
        // Communication is broken. Interrupt both listener and sender threads
        info.sender.interrupt();
        try {
			manager.deleteClient(info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //processes login command, makes sure that user is authenticated
    public void processLogin(ClientInfo info, String message)
    {
    	String[] commands = message.split(" ", 3);
    	if(commands.length == 3)
    	{
    		// check the the user list for the username and password
    		for(int i=0; i<manager.userDatabase.size(); i++)
    		{
    			if(manager.userDatabase.get(i).name.equals(commands[1]) && manager.userDatabase.get(i).password.equals(commands[2]))
    			{	
    				info.user = manager.userDatabase.get(i);
    				info.isLoggedIn = true;
    				System.out.println(info.user.name+" logged in");
        			manager.serverMessageToSender(info, "login successsfull");
        			manager.serverMessageToAll(info.user.name+ " has logged in");
    			}
    			
    		}
    		if(info.isLoggedIn != true)
				manager.serverMessageToSender(info, "login unsuccessful");
    		
    	}
    	else
    		manager.serverMessageToSender(info, "login unsuccessful insufficient ammount of commands");
    
    }
    //processes commands proceding the send command
    public void processSend (ClientInfo info, String message)
    {
    	String[] commands = message.split(" ", 3);
    	if(commands.length >=3)
    		if(commands[1].equalsIgnoreCase("all"))
    			manager.dispatchMessageToAll(info, message.substring(9,message.length()));
    		else if (commands[1].equalsIgnoreCase("userid"))
    			manager.dispatchMessageToUser(info, message.substring(12,message.length()));
    		else if (commands[1].equalsIgnoreCase("group"))
    			manager.dispatchMessageToGroup(info, message.substring(11,message.length()));
    		else if (commands[1].equalsIgnoreCase("region"))
    			manager.dispatchMessageToRegion(info, message.substring(12,message.length()));
    		else if (commands[1].equalsIgnoreCase("near"))
    			manager.dispatchMessageToNear(info, message.substring(10,message.length()));
    		else
    			manager.serverMessageToSender(info, "invalid send paramater try <all/userid/group/region/near> <value> <message>");
    }
    //proccesses the first command of the input
    public void processCommand(ClientInfo info, String message) throws IOException
    {
    	String command = "";
    	if(message.contains(" "))
    	command = message.substring(0,message.indexOf(" "));
    	else
    	command = message;
    	
    	if(command.equalsIgnoreCase("login"))
    		processLogin(info, message);
    	else if(command.equalsIgnoreCase("logout"))
    		manager.logout(info);
    	else if(!info.isLoggedIn)
    		manager.serverMessageToSender(info, "please login");
    	else if(command.equalsIgnoreCase("send"))
    		processSend(info,message);
    	else if(command.equalsIgnoreCase("who"))
    		manager.who(info);
    	else 
    		manager.serverMessageToSender(info, "invalid command");
    		
    }
 
}