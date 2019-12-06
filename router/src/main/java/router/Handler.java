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
					System.out.println("Message from "+componentType +" "+ this.id + ": "	+cmsg);//fix componentType, change run
					
					if (this.runningClient && !cmsg.isEmpty()) {
						messages.add(cmsg);
					}
					buffer.flip();
					buffer.clear();
				}
			}

		} catch (IOException e){
			System.out.println("something went wrong");
		} 
	  }
	
	
	
	@Override
	public void run() {
		runServer();
	}

	
	

}