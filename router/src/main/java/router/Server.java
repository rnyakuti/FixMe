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

public class Server extends Thread {

	private List<Handler> clientList;
	private ArrayList<String> messages = new ArrayList<String>();


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
    public int port;
    public String componentType;
	public BufferedReader input = null;
	protected int brokerID = 100000;//limit  499 999
	protected int marketID = 500000;//limit is 999 999
	Selector selector;
	
    public Server(int recievedPort, String cType)
    {
        port = recievedPort;
        componentType = cType;
		clientList = new ArrayList<Handler>();
    }

	protected void sendID(String componentType,String ID, SocketChannel sc)
	{
		   
			try
			{
				 ByteBuffer bc = ByteBuffer.wrap(ID.getBytes());
				sc.write(bc);
			}
			catch (IOException e)
			{
				System.out.println(PURPLE + componentType+RED+" FAILED TO ASSIGN ID"+RESET_CO);
			}
			 System.out.println(PURPLE + componentType+YELLOW+" [ ROUTER ASSIGNED ID " +ID+ " ]"+RESET_CO);
			      
	}
	
	private String setConnectionID(String componentType)
	{
		if(componentType.equalsIgnoreCase("broker"))
		{
			this.brokerID++;
			if(brokerID > 500000)
		    {
				System.out.println(PURPLE + componentType+RED+"YOU SOMEHOW EXHAUSTED THE UNIQUE 6 DIGIT POSSIPLE IDS"+RESET_CO);
				System.out.println(PURPLE + componentType+RED+" DISCONNECTING FROM SERVER"+RESET_CO);
				System.exit(0);
			}
			return brokerID+"";
		}
		else
		{
			this.marketID++;
			if(marketID > 1000000)
		    {
				System.out.println(PURPLE + componentType+RED+"YOU SOMEHOW EXHAUSTED THE UNIQUE 6 DIGIT POSSIPLE IDS"+RESET_CO);
				System.out.println(PURPLE + componentType+RED+" DISCONNECTING FROM SERVER"+RESET_CO);
				System.exit(0);
			}
			return marketID+"";
		}
	
	}
	
	
    protected  void runServer()
    {
        try
        {
			ServerSocketChannel server = ServerSocketChannel.open().bind(new InetSocketAddress("127.0.0.1", port));
            System.out.println(PURPLE + componentType + " " + CYAN + "[LISTENING ON PORT " + YELLOW + port + " ..." + CYAN + " ]" + RESET_CO);
		   String ID = setConnectionID(componentType);
		   while(true){
			
				SocketChannel sc = server.accept();
				Handler socketHandlerAsync = new Handler(sc, clientList.size() ,messages, port, ID,  componentType);
                System.out.println(PURPLE + componentType+ CYAN + "[ CONNECTION ACCEPTED ]"+ "\n"+RESET_CO);
				clientList.add(socketHandlerAsync);
				socketHandlerAsync.start();
		   }
		   
        }
        catch (IOException e)
        {
            System.out.println(RED+"Disconnected from the server");
        }
    }
	
	
	public static boolean validateChecksum(String msg, String checksum)  throws NoSuchAlgorithmException 
	{
   
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(msg.getBytes());
		byte[] digest = md.digest();
		String newChecksum = DatatypeConverter.printHexBinary(digest).toUpperCase();
		 if(newChecksum.equals(checksum))
		 {
			 return true;
		 }
		 else
		 {
			 return false;
		 }

	}

    @Override
    public void run()
    {
        runServer();
    }

}