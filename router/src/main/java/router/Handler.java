package router;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.io.BufferedReader;

public class Handler extends Thread {

	private SocketChannel socket;
	private List<String> messages;
	private String id;
	private String componentType;
	private boolean runningClient;
	
	/**********************************************/
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String RESET_CO = "\u001B[0m";
    /*********************************************/
	
	public Handler(SocketChannel socket, int clientListSize ,List<String> messages, int port, String id, String componentType){
		this.socket = socket;
		this.messages = messages;
		this.id = id;
		this.runningClient = true;
		this.componentType = componentType;
		sendMessage(id + " ");
	}

	public void sendMessage(String message){
		try {
			if (this.runningClient) {
				ByteBuffer msgBuffer = ByteBuffer.allocate(message.length());
				msgBuffer.wrap(message.getBytes());
				socket.write(msgBuffer.wrap(message.getBytes())); 
			} else {
				System.out.println(getClass().getSimpleName()+"Closed : "+runningClient);
			}
		}
		catch (IOException e){
			System.out.println(RED+" No market avalaible, please connect a market");
		}
	}
	public String getMessages() 
	{
		String ret = messages.get(0);
		updateMessages();
		return ret;
	}
	
	public void updateMessages()
	{
		messages.remove(0);
	}
	
	@Override
	public void run() {
		try {
			while(this.runningClient){
				if ((socket!= null) && (socket.isOpen()) && this.runningClient) {

					ByteBuffer buffer = ByteBuffer.allocate(1024);
					socket.read(buffer);
					String cmsg =  new String(buffer.array()).trim();
				System.out.println(GREEN+"Message from : "+PURPLE+componentType +YELLOW+" ID : "+GREEN+ this.id +CYAN+"  [ "+cmsg+"]"+RESET_CO);
					
					if (this.runningClient && !cmsg.isEmpty()) {
						messages.add(cmsg);
					}
					buffer.flip();
					buffer.clear();	 
				}
			}
		} catch (IOException e){
			System.out.println(RED+"DISCONNECTED FROM "+PURPLE+componentType +YELLOW+" ID : "+GREEN+ this.id);
			System.out.println(GREEN+"SERVER IS STILL RUNNING ...");
		} 	
	}
}