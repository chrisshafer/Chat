import java.io.*;
import java.net.*;
import java.util.*;
 
public class ServerSender extends Thread
{
    private Vector<String> messageQueue = new Vector<String>();
 
    private Manager manager;
    private ClientInfo info;
    private PrintWriter out;
 
    public ServerSender(ClientInfo info, Manager manager)
    throws IOException
    {
        this.info = info;
        this.manager = manager;
        Socket socket = info.socket;
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }
 


    //Adds message to the message queue
    public synchronized void sendMessage(String mesage)
    {
    	messageQueue.add(mesage);
        notify();
    }
 
    /**
     * returns and deletes the next message from the message queue. If the queue
     * is empty, waits until notified by sendMessage method.
     */
    private synchronized String getNextMessageFromQueue() throws InterruptedException
    {
        while (messageQueue.size()==0)
           wait();
        String message = (String) messageQueue.get(0);
        messageQueue.removeElementAt(0);
        return message;
    }
 
    //Sends given message to the client's socket.
    private void sendMessageToClient(String mesage)
    {
    	out.println(mesage);
    	out.flush();
    }
    //used to send a message immediatly independent of thread
    public void sendMessageToClientNow(String message)
    {
    	out.println(message);
    	out.flush();
    }

     //reads messages from the message queue and sends them to the client's socket.
    public void run()
    {
        try {
           while (!isInterrupted()) {
               String message = getNextMessageFromQueue();
               sendMessageToClient(message);
           }
        } catch (Exception e) {
           // Commuication problem
        }
 
        // Communication is broken. Interrupt both listener and sender threads
        info.listener.interrupt();
        try {
			manager.deleteClient(info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 
}