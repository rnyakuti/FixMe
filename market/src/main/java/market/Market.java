package market;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.CharBuffer;
public class Market
{
    private static BufferedReader input = null;
	    static String message = null;
    public static void main(String[] args) throws Exception {
        InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 5001);
        Selector selector = Selector.open();
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(addr);
        sc.register(selector, SelectionKey.OP_CONNECT |
                SelectionKey.OP_READ | SelectionKey.
                OP_WRITE);
        input = new BufferedReader(new
                InputStreamReader(System.in));
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
	
	
	public static String processRead(SelectionKey key) throws Exception {
      SocketChannel sChannel = (SocketChannel) key.channel();
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      sChannel.read(buffer);
      buffer.flip();
      Charset charset = Charset.forName("UTF-8");
      CharsetDecoder decoder = charset.newDecoder();
      CharBuffer charBuffer = decoder.decode(buffer);
      String msg = charBuffer.toString();
      return msg;
    }

    public static Boolean processReadySet(Set readySet)
            throws Exception {
        SelectionKey key = null;
        Iterator iterator = null;
		//Socket kkSocket = new Socket("127.0.0.1", 5001); 
		// BufferedReader in = new BufferedReader( new InputStreamReader(kkSocket.getInputStream()));
        iterator = readySet.iterator();
	//	String fromServer = in.readLine();
		//  System.out.println(fromServer);
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
            System.out.println("Message received from Server: " + result + " Message length= " + result.length());
        }
        if (key.isWritable()) {
            System.out.print("Type a message (type quit to stop): ");
            String msg = input.readLine();
            if (msg.equalsIgnoreCase("quit")) {
                return true;
            }
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer bb = ByteBuffer.wrap(msg.getBytes());
            sc.write(bb);
        }
        return false;
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