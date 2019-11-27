package router;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;


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
    public String componentType;

    public Server(int recievedPort, String cType)
    {
        port = recievedPort;
        componentType = cType;
    }

    protected  void runServer()
    {
        try
        {
            Selector selector = Selector.open();
            ServerSocketChannel server = ServerSocketChannel.open();
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
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                        System.out.println(PURPLE + componentType+ CYAN + "[ CONNECTION ACCEPTED ]" + sc.getLocalAddress() + "\n"+RESET_CO);
                    }
                    if (key.isReadable())
                    {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer bb = ByteBuffer.allocate(1024);
                        sc.read(bb);
                        String result = new String(bb.array()).trim();
                        System.out.println(PURPLE + componentType+YELLOW+"[ Message received: " + result + " ]"+RESET_CO);
                        if (result.length() <= 0) {
                            sc.close();
                            System.out.println(PURPLE + componentType+RED+"[ CONNECTION CLOSED...]"+RESET_CO);
                            System.out.println(CYAN+"Server will keep running. " + "Run " +YELLOW+ componentType+CYAN+" to re-establish connection"+RESET_CO);
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.out.println(RED+"Disconnected from the server");
        }
    }

    @Override
    public void run()
    {
        runServer();
    }

}