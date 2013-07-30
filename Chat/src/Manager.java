
import java.io.IOException;
import java.util.*;
 
public class Manager
{
    private Vector<ClientInfo> clients = new Vector<ClientInfo>();
    public Vector<User> userDatabase = new Vector<User>();
    
    public void addClient(ClientInfo info) throws IOException
    {
    	if(this.clients.size() < EchoServer.MAX_CLIENTS)
        clients.add(info);
    	else
    	{
    		info.sender.sendMessageToClientNow("Maximum number of connections reached");
    		info.socket.close();
    	}
    }
    
    public void deleteClient(ClientInfo info) throws IOException
    {	
	        int clientIndex = clients.indexOf(info);
	        if (clientIndex >= 0 && clientIndex < clients.size()){
		        clients.get(clientIndex).socket.close();
		        clients.removeElementAt(clientIndex);
	        }
    }
 
    public void serverMessageToAll(String message)
    {
        for (int i=0; i<clients.size(); i++) {
            ClientInfo clientInfo = (ClientInfo) clients.get(i);
            if(clientInfo.isLoggedIn)
            clientInfo.sender.sendMessage("SERVER : " + message);
         }
    }
    
    public void serverMessageToSender(ClientInfo info, String message)
    {
        info.sender.sendMessage("SERVER : " + message);
    }
    
    public void who(ClientInfo info)
    {
        String message = "WHO REQUEST :\n";
        for(int i=0; i<clients.size(); i++)
        {	if(clients.get(i).isLoggedIn)
        	message += clients.get(i).user.name+"\n";
        }
        serverMessageToSender(info,message);
    }
    
    public void logout(ClientInfo info) throws IOException
    {
        String name = info.user.name;
        deleteClient(info);
        serverMessageToAll(name+" has logged off");
        System.out.println(name+" has logged off");
    }
    
    //broadcast
    public synchronized void dispatchMessageToAll(ClientInfo info, String message)
    {
        for (int i=0; i<clients.size(); i++) {
            ClientInfo clientInfo = (ClientInfo) clients.get(i);
            if(clientInfo.isLoggedIn)
            clientInfo.sender.sendMessage("[All]:"+info.user.name + " : " + message);
            System.out.println("[All]:"+info.user.name + " : " + message);
         }
    }
    //unicast
	public void dispatchMessageToUser(ClientInfo info, String message) {
		String name = message.substring(0, message.indexOf(" "));
		for (int i=0; i<clients.size(); i++) {
	           ClientInfo clientInfo = (ClientInfo) clients.get(i);
	           if(clientInfo.user.name.equals(name))
	           {
	           clientInfo.sender.sendMessage("[Private]:"+info.user.name+": "+message.substring(message.indexOf(" ")+1,message.length()));
	           System.out.println("[To-"+clientInfo.user.name+"]:"+info.user.name+": "+message.substring(message.indexOf(" ")+1,message.length()));
	           return;
	           }
	        }
		serverMessageToSender(info,"User not online");
		
	}
	//multicast
	public void dispatchMessageToGroup(ClientInfo info, String message) {
		int group = Integer.parseInt(message.substring(0, message.indexOf(" ")));
		if(info.user.group.contains(group)){
		for (int i=0; i<clients.size(); i++) {
	           ClientInfo clientInfo = (ClientInfo) clients.get(i);
	           if(clientInfo.user.group.contains(group))
	           clientInfo.sender.sendMessage("[Group-"+group+"]:"+info.user.name+": "+message.substring(message.indexOf(" ")+1,message.length()));
	           System.out.println("[Group-"+group+"]:"+info.user.name+": "+message.substring(message.indexOf(" ")+1,message.length()));
	     }
		}
		else
			serverMessageToSender(info,"You are not a member of group : "+group);
			
			
		
	}
	//geocast
	public void dispatchMessageToRegion(ClientInfo info, String message) {
		int region = info.user.region;
		for (int i=0; i<clients.size(); i++) {
	           ClientInfo clientInfo = (ClientInfo) clients.get(i);
	           if(clientInfo.user.region == region)
	           clientInfo.sender.sendMessage("[Region-"+region+"]:"+info.user.name+": "+message);
	           System.out.println("[Region-"+region+"]:"+info.user.name+": "+message);
	        }
		
	}
	//anycast
	public void dispatchMessageToNear(ClientInfo info, String message) {
	
		
	}
	
}