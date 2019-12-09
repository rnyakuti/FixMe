package market;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.CharBuffer;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;


public class Market
{
    private static BufferedReader input = null;
	static String message = null;
    public static final String[] instruments = {"The Gold Leaf Bread", "Roquefort and Almond Sourdough bread", "Brioche", "Baguette", "Brown Bread", "White Bread"};
	 public static final int[] invetory = {30, 60, 90, 120,150, 180};
    public static String ID ="";
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
	
	
	public static void main(String[] args) throws Exception 
	{
		
        InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 5001);
        Selector selector = Selector.open();
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(addr);
        sc.register(selector, SelectionKey.OP_CONNECT |
                SelectionKey.OP_READ | SelectionKey.
                OP_WRITE);
        input = new BufferedReader(new InputStreamReader(System.in));
		printInstruments();
        while (true) {
            if (selector.select() > 0) {
				
				
                Boolean doneStatus = processReadySet
                        (selector.selectedKeys());
                if (doneStatus) {
                    break;
                }
            }
        }
        sc.close();
    }

public static void printInstruments()
{
	System.out.println(YELLOW+"LIST OF AVAILABLE BREADS TO TRADE"+RESET_CO);
	for(int i = 0; i< instruments.length;i++)
	{
		System.out.println(GREEN+" [ "+instruments[i]+" ]"+RESET_CO);
	}
}



    public static Boolean processReadySet(Set readySet)
            throws Exception {
        SelectionKey key = null;
        Iterator iterator = null;
	
        iterator = readySet.iterator();
        while (iterator.hasNext()) {
            key = (SelectionKey) iterator.next();
            iterator.remove();
        }
        if (key.isConnectable()) {
            Boolean connected = processConnect(key);
            if (!connected) {
                return true;
            }
        }
       if (key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer bb = ByteBuffer.allocate(1024);
            sc.read(bb);
            String result = new String(bb.array()).trim();
           
			if(ID.isEmpty())
			{
				ID = result;
				System.out.println(GREEN+"Assigned ID: "+CYAN+"[ " +ID+" ]");
			}
			else
			{
				 System.out.println(GREEN+"Message received from Server: "+CYAN+"[ " + result+" ]");
			}
        }
		
       /* if (key.isWritable()) {
			printInstruments();
			//Market cannot write to router from cmd
            //System.out.println("Type a message (type quit to stop): ");
            String msg = input.readLine();
			//generate checksum of msg
			String checksum = createChecksum(msg);
			msg+="-"+checksum;
            if (msg.equalsIgnoreCase("quit")) {
               System.exit(0);
            }
            //SocketChannel sc = (SocketChannel) key.channel();
            //ByteBuffer bb = ByteBuffer.wrap(msg.getBytes());
            //sc.write(bb);
        }*/
        return false;
    }
	
	
	public static String createChecksum(String msg)  throws NoSuchAlgorithmException
	{
		//FIX NOTATION
		MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(msg.getBytes());
       byte[] digest = md.digest();
       String checksum = DatatypeConverter.printHexBinary(digest).toUpperCase();
	   return checksum;
	}
	
	
    public static Boolean processConnect(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            while (sc.isConnectionPending()) {
                sc.finishConnect();
            }
        } catch (IOException e) {
            key.cancel();
            e.printStackTrace();
            return false;
        }
        return true;
    }
}