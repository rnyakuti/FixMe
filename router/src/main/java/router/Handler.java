package router;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.io.BufferedReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


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
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
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
			System.out.println(getClass().getSimpleName()+" Server freaked out");
		}
	}
	
	  protected  void runServer()
	  {
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
	
	
	
	@Override
	public void run() {
		runServer();
	}

	
	

}