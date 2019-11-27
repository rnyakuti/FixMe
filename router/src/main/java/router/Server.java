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