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

public class Server extends Thread {

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
	// private static final Logger logger = Logger.getLogger(MD5Checksum.class.getName());
    public String componentType;
	public BufferedReader input = null;
	protected int brokerID = 100000;//limit  499 999
	protected int marketID = 500000;//limit is 999 999
	ServerSocketChannel server;
	Selector selector;
    public Server(int recievedPort, String cType)
    {
        port = recievedPort;
        componentType = cType;
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
				System.out.println("broker");
				System.out.println(PURPLE + componentType+RED+"YOU SOMEHOW EXHAUSTED THE UNIQUE 6 DIGIT POSSIPLE IDS"+RESET_CO);
				System.out.println(PURPLE + componentType+RED+" DISCONNECTING FROM SERVER"+RESET_CO);
				System.exit(0);
			}
			return brokerID+"";
		}
		else
		{
			System.out.println("market");
			System.out.println(marketID);
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
	
	protected void parseMessage(String ms)
	{

	}
	
    protected  void runServer()
    {
        try
        {
            selector = Selector.open();
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress("127.0.0.1", port));
            System.out.println(PURPLE + componentType + " " + CYAN + "[LISTENING ON PORT " + YELLOW + port + " ..." + CYAN + " ]" + RESET_CO);
            server.register(selector, SelectionKey.OP_ACCEPT);
            SelectionKey key = null;
            while (true)
            {
                if (selector.select() <= 0)
                    continue;
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext())
                {
                    key = (SelectionKey) iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        SocketChannel sc = server.accept();
					   // sc = (SocketChannel) key.channel();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                        System.out.println(PURPLE + componentType+ CYAN + "[ CONNECTION ACCEPTED ]" + sc.getLocalAddress() + "\n"+RESET_CO);
						String ID = setConnectionID(componentType);
						sendID(componentType,ID,sc);
		
                    }
					
                    if (key.isReadable())
                    {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer bb = ByteBuffer.allocate(1024);
                        sc.read(bb);
                        String result = new String(bb.array()).trim();
						//validate checksum
						String [] arrValidate = result.split("-");
						if( validateChecksum(arrValidate[0], arrValidate[1]))
						{
							System.out.println(CYAN+(port == 5000? brokerID : marketID)+PURPLE + componentType+YELLOW+"[ Message received: " + result + " ]"+RESET_CO);
							if(arrValidate[0].equalsIgnoreCase("buy") || arrValidate[0].equalsIgnoreCase("sell"))
						    {
								
								    if (key.isAcceptable()) {
										System.out.println(PURPLE +"parsing 1");
									//SocketChannel sc = server.accept();
								   // sc = (SocketChannel) key.channel();
									sc.configureBlocking(false);
									sc.register(selector, SelectionKey.OP_READ);
									System.out.println(PURPLE + componentType+ CYAN + "[ maassssssssss ACCEPTED ]" + sc.getLocalAddress() + "\n"+RESET_CO);
									//String ID = setConnectionID(componentType);
									//sendID(componentType,ID,sc);
					
									}
								//parseMessage( result);
								
								
							}
						}
						else
					    {
							
							System.out.println(RED+"Message from "+PURPLE + componentType+RED+" failed checksum and will be disregarded "+RESET_CO);
						}
                        
                        if (result.length() < 0) {
                            sc.close();
                            System.out.println(PURPLE + componentType+RED+"[ CONNECTION CLOSED...]"+RESET_CO);
                            System.out.println(CYAN+"Server will keep running. " + "Run " +YELLOW+ componentType+CYAN+" to re-establish connection"+RESET_CO);
                        }
                    }
                }
            }
        }
        catch (IOException | NoSuchAlgorithmException e)
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